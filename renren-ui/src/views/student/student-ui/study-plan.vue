<template>
  <div class="container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <h3>我的专属个性化学习路径</h3>
        <el-button style="float: right;" type="primary" @click="loadStudyPlan">
          刷新计划
        </el-button>
      </div>
      
      <!-- ECharts 甘特图组件 -->
      <StudyGanttChart
        v-if="studyPlanData && studyPlanData.planItems && studyPlanData.planItems.length > 0"
        :study-plan-data="studyPlanData"
        :knowledge-point-map="knowledgePointMap"
      />
      
      <!-- 学习计划概览 -->
      <div v-if="studyPlanData" class="plan-overview">
        <div class="plan-name-row">
          <div class="plan-name-item">
            <div class="statistic-title">计划名称</div>
            <div class="plan-name-content">{{ studyPlanData.planName }}</div>
          </div>
        </div>
        <el-row :gutter="20" class="stats-row">
          <el-col 
            v-for="stat in statistics" 
            :key="stat.title" 
            :span="8"
          >
            <div class="statistic-item">
              <div class="statistic-title">{{ stat.title }}</div>
              <div class="statistic-content">{{ stat.value }}</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 学习计划项列表 -->
      <div v-if="planItems.length > 0" class="plan-items">
        <h4>学习计划详情</h4>
        <el-table :data="planItems" style="width: 100%">
          <el-table-column prop="dayIndex" label="第几天" width="100" />
          <el-table-column prop="sort" label="任务排序" width="80" />
          <el-table-column label="科目" prop="subjectName" width="120" />
          <el-table-column label="知识点" min-width="200">
            <template #default="scope">
              <div v-if="scope.row.knowledgePointId && scope.row.knowledgePointId > 0">
                <el-tag v-if="scope.row.isReview === 1" type="warning" size="small">复习</el-tag>
                <span class="point-name">
                  {{ knowledgePointMap[scope.row.knowledgePointId] || `知识点 ${scope.row.knowledgePointId}` }}
                </span>
                <el-tag v-if="scope.row.subjectName" type="info" size="small" style="margin-left: 8px;">
                  {{ scope.row.subjectName }}
                </el-tag>
              </div>
              <span v-else class="text-2">待分配知识点</span>
            </template>
          </el-table-column>
          <el-table-column label="学习进度" width="120" align="center">
            <template #default="scope">
              <!-- 复习任务显示"复习" -->
              <span v-if="scope.row.isReview === 1" style="color: #67C23A; font-size: 14px;">复习</span>
              
              <!-- 跨天学习任务显示进度条和百分比 -->
              <template v-else-if="scope.row.totalParts > 1">
                <el-progress
                v-if="(scope.row.splitPart / scope.row.totalParts)!=1"
                  :percentage="Math.round((scope.row.splitPart / scope.row.totalParts) * 100)"
                  :stroke-width="6"
                  :show-text="false"
                  style="width: 60px; display: inline-block;"
                />
                <span style="margin-left: 8px; font-size: 12px; color: #67C23A;">
                  {{ Math.round((scope.row.splitPart / scope.row.totalParts) * 100) }}%
                </span>
              </template>
              
              <!-- 单次完成任务显示100% -->
              <span v-else style="color: #67C23A; font-size: 14px;">100%</span>
            </template>
          </el-table-column>
          <el-table-column prop="completed" label="完成状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.completed === 1 ? 'success' : scope.row.completed === 2 ? 'warning' : 'info'">
                {{ scope.row.completed === 1 ? '已完成' : scope.row.completed === 2 ? '补交' : '未开始' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 没有数据的提示 -->
      <div v-else class="empty-state">
        <el-empty description="暂无学习计划数据">
          <el-button type="primary" @click="$router.push({ name: 'StudentQuestionnaire' })">
            去填写学情问卷
          </el-button>
        </el-empty>
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { DataAnalysis } from '@element-plus/icons-vue'
import { ref, onMounted, onActivated, computed } from 'vue'
import baseService from '@/service/baseService'
import { ElMessage, ElMessageBox } from 'element-plus'
import StudyGanttChart from '@/components/student/StudyGanttChart.vue'

const studyPlanData = ref(null)
const planItems = ref([])
const knowledgePointMap = ref({}) // 知识点ID到名称的映射

// 计算剩余天数
const calculateRemainingDays = computed(() => {
  if (!studyPlanData.value || !studyPlanData.value.startDate || !studyPlanData.value.endDate) {
    return 0
  }

  const startDate = new Date(studyPlanData.value.startDate)
  const endDate = new Date(studyPlanData.value.endDate)
  const today = new Date()

  // 重置时间为午夜，避免时间影响
  startDate.setHours(0, 0, 0, 0)
  endDate.setHours(0, 0, 0, 0)
  today.setHours(0, 0, 0, 0)

  // 边界情况处理
  if (today < startDate) {
    // 计划尚未开始
    return studyPlanData.value.totalDays
  }
  if (today >= endDate) {
    // 计划已结束
    return 0
  }
  
  // 正常计算剩余天数（使用Math.floor避免多算一天）
  const diffTime = endDate.getTime() - today.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  // 确保不超过总天数
  return Math.min(diffDays, studyPlanData.value.totalDays)
})

// 统计数据计算属性
const statistics = computed(() => {
  if (!studyPlanData.value) return []
  
  return [
    {
      title: '总天数',
      value: studyPlanData.value.totalDays
    },
    {
      title: '每日学习',
      value: studyPlanData.value.dailyHours + '小时'
    },
    {
      title: '剩余天数', 
      value: calculateRemainingDays.value
    }
  ]
})

// 加载知识点名称
const loadKnowledgePointNames = async () => {
  try {
    // 使用分页接口获取所有知识点
    const response = await baseService.get('/student/knowledgepoint/page', {
      page: 1,
      limit: 500  // 设置一个足够大的数来获取所有数据
    })
    if (response.code === 0 && response.data && response.data.list) {
      const map = {}
      response.data.list.forEach(point => {
        if (point.id && point.pointName) {
          map[point.id] = point.pointName
        }
      })
      knowledgePointMap.value = map
      console.log('知识点映射加载完成:', Object.keys(map).length, '个知识点')
    }
  } catch (error) {
    console.error('加载知识点名称失败:', error)
    ElMessage.error('加载知识点数据失败')
  }
}

// 加载学习计划数据
const loadStudyPlan = async () => {
  try {
    const userId = 1;
    const response = await baseService.get('/student/studyplan/user/latest', {
      userId: userId // 先用测试用户ID
    })
    
    if (response.code === 0) {
      if(response.data === null){
        console.log('用户 '+userId+' 暂无学习计划')
        ElMessage.info('您还没有创建学习计划')
        return
      }
      studyPlanData.value = response.data
      planItems.value = response.data.planItems || []
      console.log('学习计划数据:', studyPlanData.value)
      
      // 在获取到学习计划数据后，立即加载知识点名称
      await loadKnowledgePointNames()
    }else {
      ElMessage.error('获取学习计划失败')
    }
  } catch (error) {
    console.error('获取学习计划失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 页面加载时获取数据
onMounted(() => {
  Promise.all([
    loadStudyPlan()
  ]).then(() => {
    console.log('页面数据加载完成')
  })
})

// keep-alive激活时重新加载数据
onActivated(() => {
  Promise.all([
    loadStudyPlan()
  ]).then(() => {
    console.log('页面数据重新加载完成')
  })
})

// 将loadStudyPlan暴露给路由守卫调用
defineExpose({
  loadStudyPlan
})


</script>

<style lang="less" scoped>
@import "@/assets/css/app.less";

.clearfix{
// haili
  display:flex;
  justify-content: space-between;
}

.chart-container {
  position: relative;
  width: 100%;
  height: 300px;
  margin-bottom: 30px;
}

.placeholder-chart {
  height: 100%;
  text-align: center;
  padding: 50px;
  background: #fafafa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  
  .chart-icon {
    font-size: 48px;
    color: #c0c4cc;
    margin-bottom: 16px;
  }
  
  .placeholder-text {
    color: #909399;
    font-size: 16px;
    margin-bottom: 8px;
  }
  
  .placeholder-desc {
    color: #909399;
    font-size: 14px;
  }
}

.timeline-container {
  margin-top: 30px;
}

.box-card{
  border: 1px solid rgb(255, 255, 255); /* 修改边框颜色 */
}

/* 新增样式：计划名称单独一行 (deepseek)*/
.plan-overview {
  margin-bottom: 24px;
}

.plan-name-row {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.plan-name-item {
  .statistic-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .plan-name-content {
    font-size: 18px;
    color: #303133;
    font-weight: 600;
    line-height: 1.4;
    word-break: break-word;
  }
}
.stats-row{
  margin-top: 30px;
}

.statistic-item {
  text-align: center;
  padding: 16px 0;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
  
  .statistic-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }
  
  .statistic-content {
    font-size: 24px;
    color: #303133;
    font-weight: bold;
  }
}

.plan-items {
  margin-top: 24px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}
</style>
