<template>
  <div class="container">
    <div class="filter-container">
      <h3>学习概览</h3>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      closable
      @close="error = null"
      style="margin-bottom: 20px"
    />

    <!-- 数据展示区域 -->
    <div v-else-if="dashboardData && statistics.totalDays > 0">
      <!-- 数据卡片 -->
      <el-row :gutter="20" class="panel-group">
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
            <div class="card-panel-icon-wrapper icon-people">
            <!-- 使用 Element Plus 图标替代 svg-icon -->
              <el-icon class="card-panel-icon"><Calendar /></el-icon>
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">学习天数</div>
              <div class="card-panel-num">{{ statistics.currentDay }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
            <div class="card-panel-icon-wrapper icon-message">
              <el-icon class="card-panel-icon"><DataAnalysis /></el-icon>
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">学习总进度</div>
              <div class="card-panel-num">{{ statistics.overallProgress }}%</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
            <div class="card-panel-icon-wrapper icon-money">
              <el-icon class="card-panel-icon"><List /></el-icon>
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">已完成任务</div>
              <div class="card-panel-num">{{ statistics.completedTasks }}/{{ statistics.totalTasks }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
            <div class="card-panel-icon-wrapper icon-shopping">
              <el-icon class="card-panel-icon"><Clock /></el-icon>
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">剩余天数</div>
              <div class="card-panel-num">{{ statistics.remainingDays }}</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 今日任务 -->
      <el-card class="box-card" shadow="never">
        <div slot="header" class="clearfix">
          <span>今日任务</span>
          <el-button
            style="float: right; padding: 3px 0"
            type="text"
            @click="viewAllTasks"
          >
            查看全部
          </el-button>
        </div>
        
        <el-table
          :data="todayTasks"
          style="width: 100%"
          :show-header="false"
          empty-text="今日暂无任务"
          v-loading="loadingTasks"
        >
          <el-table-column label="任务名称" min-width="200">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <!-- 完成状态：0-未完成 -->
                <el-checkbox
                  v-show="row.completed === 0"
                  :model-value="false"
                  @change="() => handleTaskComplete(row, true)"
                >
                  {{ getKnowledgePointName(row.knowledgePointId) }}
                  <el-tag v-if="row.isReview === 1" type="warning" size="small" style="margin-left: 8px;">
                    复习
                  </el-tag>
                </el-checkbox>
                
                <!-- 完成状态：1-已完成 -->
                <div v-show="row.completed === 1" style="display: flex; align-items: center;">
                  <el-icon style="color: #67C23A; margin-right: 8px;"><Check /></el-icon>
                  <span style="text-decoration: line-through; color: #909399;">
                    {{ getKnowledgePointName(row.knowledgePointId) }}
                    <el-tag v-if="row.isReview === 1" type="info" size="small" style="margin-left: 8px;">
                      复习
                    </el-tag>
                  </span>
                </div>
                
                <!-- 完成状态：2-补交 -->
                <div v-show="row.completed === 2" style="display: flex; align-items: center;">
                  <el-icon style="color: #E6A23C; margin-right: 8px;"><Refresh /></el-icon>
                  <span style="color: #E6A23C; font-weight: bold;">
                    {{ getKnowledgePointName(row.knowledgePointId) }}
                    <el-tag type="warning" size="small" style="margin-left: 8px;">
                      补交
                    </el-tag>
                    <el-tag v-if="row.isReview === 1" type="info" size="small" style="margin-left: 8px;">
                      复习
                    </el-tag>
                  </span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="科目" width="100">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.subjectName }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="进度" width="120" align="center">
            <template #default="{ row }">
              <!-- 复习任务显示"复习" -->
              <span v-if="row.isReview === 1" style="color: #67C23A; font-size: 14px;">复习</span>
              
              <!-- 跨天学习任务显示进度条和百分比 -->
              <template v-else-if="row.totalParts > 1">
                <el-progress
                v-if="(row.splitPart / row.totalParts)!=1"
                  :percentage="Math.round((row.splitPart / row.totalParts) * 100)"
                  :stroke-width="6"
                  :show-text="false"
                  style="width: 60px; display: inline-block;"
                />
                <span style="margin-left: 8px; font-size: 12px; color: #67C23A;">
                  {{ Math.round((row.splitPart / row.totalParts) * 100) }}%
                </span>
              </template>

              <!-- 单次完成任务显示100%  -->
              <span v-else style="color: #67C23A; font-size: 12px;">100%</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button
                type="text"
                size="small"
                @click="viewTaskDetail(row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 补交任务（显示所有需要补交的任务，包括未完成的和已补交的） -->
      <el-card class="box-card" shadow="never" style="margin-top: 20px;">
        <div slot="header" class="clearfix">
          <span>补交任务</span>
          <el-tag type="danger" size="small" style="margin-left: 8px;">
            已过期 {{ overdueTasks.length }}
          </el-tag>
        </div>
        
        <div v-if="overdueTasks.length === 0" style="padding: 20px; text-align: center; color: #909399;">
          <el-icon style="font-size: 48px; margin-bottom: 10px;"><SuccessFilled /></el-icon>
          <p>暂无补交任务，继续保持！！你很棒！！！</p>
        </div>
        
        <el-table
          v-else
          :data="overdueTasks"
          style="width: 100%"
          :show-header="false"
          v-loading="loadingTasks"
        >
          <el-table-column label="任务名称" min-width="200">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <!-- 完成状态：0-未完成 -->
                <el-checkbox
                  v-show="row.completed === 0"
                  :model-value="false"
                  @change="() => handleTaskComplete(row, true)"
                >
                  {{ getKnowledgePointName(row.knowledgePointId) }}
                  <el-tag v-if="row.isReview === 1" type="warning" size="small" style="margin-left: 8px;">
                    复习
                  </el-tag>
                </el-checkbox>
                
                <!-- 完成状态：1-已完成 -->
                <div v-show="row.completed === 1" style="display: flex; align-items: center;">
                  <el-icon style="color: #67C23A; margin-right: 8px;"><Check /></el-icon>
                  <span style="text-decoration: line-through; color: #909399;">
                    {{ getKnowledgePointName(row.knowledgePointId) }}
                    <el-tag v-if="row.isReview === 1" type="info" size="small" style="margin-left: 8px;">
                      复习
                    </el-tag>
                  </span>
                </div>
                
                <!-- 完成状态：2-补交 -->
                <div v-show="row.completed === 2" style="display: flex; align-items: center;">
                  <el-icon style="color: #E6A23C; margin-right: 8px;"><Refresh /></el-icon>
                  <span style="color: #E6A23C; font-weight: bold;">
                    {{ getKnowledgePointName(row.knowledgePointId) }}
                    <el-tag type="warning" size="small" style="margin-left: 8px;">
                      补交
                    </el-tag>
                    <el-tag v-if="row.isReview === 1" type="info" size="small" style="margin-left: 8px;">
                      复习
                    </el-tag>
                  </span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="科目" width="100">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.subjectName }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="应完成日期" width="120" align="center">
            <template #default="{ row }">
              <span style="color: #F56C6C; font-size: 12px;">
                第{{ row.dayIndex }}天
              </span>
            </template>
          </el-table-column>
          
          <el-table-column label="进度" width="120" align="center">
            <template #default="{ row }">
              <!-- 复习任务显示"复习" -->
              <span v-if="row.isReview === 1" style="color: #67C23A; font-size: 14px;">复习</span>
              
              <!-- 跨天学习任务显示进度条和百分比 -->
              <template v-else-if="row.totalParts > 1">
                <!-- 未完成的多部分任务，显示进度条 -->
                <el-progress
                v-if="(row.splitPart / row.totalParts)!=1"
                  :percentage="Math.round((row.splitPart / row.totalParts) * 100)"
                  :stroke-width="6"
                  :show-text="false"
                  style="width: 60px; display: inline-block;"
                />
                <span style="margin-left: 8px; font-size: 12px; color: #67C23A;">
                  {{ Math.round((row.splitPart / row.totalParts) * 100) }}%
                </span>
              </template>

              <!-- 单次完成任务显示100% -->
              <span v-else style="color: #67C23A; font-size: 12px;">100%</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button
                type="text"
                size="small"
                @click="viewTaskDetail(row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 无数据状态 -->
    <el-empty
      v-else
      description="暂无学习计划"
      :image-size="200"
    >
      <el-button type="primary" @click="goToQuestionnaire">
        去创建学习计划
      </el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, DataAnalysis, List, Clock, Check, SuccessFilled } from '@element-plus/icons-vue'
import baseService from '@/service/baseService'

const router = useRouter()

// 数据状态
const loading = ref(false)
const loadingTasks = ref(false)
const error = ref(null)
const dashboardData = ref(null)

// 知识点名称映射（从study-plan页面获取）
const knowledgePointMap = ref({})

// 统计数据
const statistics = computed(() => {
  if (!dashboardData.value) {
    return {
      totalDays: 0,
      completedTasks: 0,
      totalTasks: 0,
      remainingDays: 0,
      overallProgress: 0,
      currentDay: 0
    }
  }
  
  // 计算今天是第几天
  const startDate = dashboardData.value.startDate ? new Date(dashboardData.value.startDate) : null
  const today = new Date()
  let currentDay = 1
  
  if (startDate) {
    // 重置时间为午夜，避免时间影响
    startDate.setHours(0, 0, 0, 0)
    today.setHours(0, 0, 0, 0)
    
    const daysDiff = Math.floor((today - startDate) / (1000 * 60 * 60 * 24))
    currentDay = daysDiff + 1 // 第1天是startDate当天
  }
  
  return {
    totalDays: dashboardData.value.totalDays || 0,
    completedTasks: dashboardData.value.completedTasks || 0,
    totalTasks: dashboardData.value.totalTasks || 0,
    remainingDays: dashboardData.value.remainingDays || 0,
    overallProgress: dashboardData.value.overallProgress || 0,
    currentDay: currentDay
  }
})

// 今日任务
const todayTasks = computed(() => {
  return dashboardData.value?.todayTasks || []
})

// 补交任务（过去日期未完成的任务，包括未完成的和已补交的）
// 完成状态：0-未完成, 1-已完成, 2-补交
const overdueTasks = computed(() => {
  if (!dashboardData.value?.allTasks || !dashboardData.value?.startDate) {
    return []
  }
  
  // 计算今天是第几天
  const startDate = new Date(dashboardData.value.startDate)
  const today = new Date()
  const daysDiff = Math.floor((today - startDate) / (1000 * 60 * 60 * 24)) + 1
  const todayDayIndex = daysDiff
  
  // 过滤出过去日期（dayIndex < todayDayIndex）且未完成或补交的任务
  return dashboardData.value.allTasks.filter(task => {
    return task.dayIndex < todayDayIndex && (task.completed === 0 || task.completed === 2)
  })
})

// 获取知识点名称
const getKnowledgePointName = (knowledgePointId) => {
  if (!knowledgePointId || knowledgePointId <= 0) {
    return '复习任务'
  }
  return knowledgePointMap.value[knowledgePointId] || `知识点 ${knowledgePointId}`
}

// 加载Dashboard数据
const loadDashboardData = async () => {
  loading.value = true
  error.value = null
  
  try {
    const userId = 1 // 测试用户ID，实际应该从登录信息获取
    const response = await baseService.get('/student/dashboard/data', { userId })
    
    if (response.code === 0) {
      // 检查是否有有效数据（totalDays > 0 表示有学习计划）
      if (response.data && response.data.totalDays > 0) {
        dashboardData.value = response.data
        console.log('Dashboard数据加载完成:', response.data)
        
        // 加载知识点名称映射
        await loadKnowledgePointMap()
      } else {
        // 用户没有学习计划，显示无数据状态
        dashboardData.value = null
        console.log('用户 '+userId+' 暂无学习计划')
        ElMessage.info('您还没有创建学习计划')
      }
    } else {
      error.value = response.msg || '获取Dashboard数据失败'
      ElMessage.error(error.value)
    }
  } catch (err) {
    console.error('获取Dashboard数据失败:', err)
    error.value = '网络错误，请稍后重试'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 加载知识点名称映射
const loadKnowledgePointMap = async () => {
  try {
    const response = await baseService.get('/student/knowledgepoint/page', {
      page: 1,
      limit: 1000
    })
    
    if (response.code === 0 && response.data && response.data.list) {
      const map = {}
      response.data.list.forEach(point => {
        if (point.id && point.pointName) {
          map[point.id] = point.pointName
        }
      })
      knowledgePointMap.value = map
      console.log('知识点映射加载完成，共', Object.keys(map).length, '个')
    }
  } catch (error) {
    console.error('加载知识点映射失败:', error)
  }
}

// 处理任务完成状态更新
const handleTaskComplete = async (task, completed) => {
  loadingTasks.value = true
  
  // 保存原始状态，用于失败时恢复
  const originalCompleted = task.completed
  
  try {
    // 先获取完整的任务数据（参考studyplanitem-add-or-update.vue的getInfo方法）
    const getResponse = await baseService.get(`/student/studyplanitem/${task.id}`)
    
    if (getResponse.code !== 0 || !getResponse.data) {
      ElMessage.error('获取任务数据失败')
      // 恢复原始状态
      task.completed = originalCompleted
      loadingTasks.value = false
      return
    }
    
    // 使用获取到的完整数据，只修改completed和completionDate字段
    // 参考studyplanitem-add-or-update.vue的数据格式，所有字段都转换为字符串
    // Date字段需要特殊处理，使用后端能识别的格式
    const data = getResponse.data
    
    // 格式化日期为后端可识别的格式（yyyy-MM-dd HH:mm:ss）
    const formatDate = (date) => {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      const seconds = String(d.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
    
    // 判断是否是补交任务（dayIndex < 今天）
    const startDate = dashboardData.value?.startDate ? new Date(dashboardData.value.startDate) : null
    const today = new Date()
    const daysDiff = startDate ? Math.floor((today - startDate) / (1000 * 60 * 60 * 24)) + 1 : 0
    const todayDayIndex = daysDiff
    
    const isOverdueTask = task.dayIndex < todayDayIndex
    const newCompletedStatus = completed ? (isOverdueTask ? 2 : 1) : 0
    
    const updateData = {
      id: data.id ? String(data.id) : '',
      planId: data.planId ? String(data.planId) : '',
      knowledgePointId: data.knowledgePointId ? String(data.knowledgePointId) : '',
      dayIndex: data.dayIndex !== null && data.dayIndex !== undefined ? String(data.dayIndex) : '',
      sort: data.sort !== null && data.sort !== undefined ? String(data.sort) : '',
      completed: String(newCompletedStatus),
      completionDate: completed ? formatDate(new Date()) : '',
      createDate: data.createDate ? formatDate(data.createDate) : '',
      updateDate: data.updateDate ? formatDate(data.updateDate) : '',
      subjectName: data.subjectName ? String(data.subjectName) : '',
      isReview: data.isReview !== null && data.isReview !== undefined ? String(data.isReview) : '0',
      splitPart: data.splitPart !== null && data.splitPart !== undefined ? String(data.splitPart) : '1',
      totalParts: data.totalParts !== null && data.totalParts !== undefined ? String(data.totalParts) : '1',
      splitHours: data.splitHours !== null && data.splitHours !== undefined ? String(data.splitHours) : '0'
    }
    
    const response = await baseService.put('/student/studyplanitem', updateData)
    
    if (response.code === 0) {
      ElMessage.success(completed ? (isOverdueTask ? '任务已补交完成' : '任务已完成') : '任务已重置')
      // 刷新Dashboard数据
      await loadDashboardData()
    } else {
      ElMessage.error('更新失败')
      // 恢复原始状态
      task.completed = originalCompleted
    }
  } catch (error) {
    console.error('更新任务状态失败:', error)
    ElMessage.error('网络错误')
    // 恢复原始状态
    task.completed = originalCompleted
  } finally {
    loadingTasks.value = false
  }
}

// 查看全部任务
const viewAllTasks = () => {
  router.push({ name: 'StudentStudyPlan' })
}

// 查看任务详情
const viewTaskDetail = (task) => {
  const detail = `
    <div style="padding: 10px; line-height: 1.8;">
      <p><strong>任务ID：</strong>${task.id}</p>
      <p><strong>知识点：</strong>${getKnowledgePointName(task.knowledgePointId)}</p>
      <p><strong>科目：</strong>${task.subjectName}</p>
      <p><strong>第几天：</strong>第${task.dayIndex}天</p>
      <p><strong>顺序：</strong>第${task.sort}个</p>
      <p><strong>状态：</strong>${task.completed === 0 ? '未完成' : (task.completed === 1 ? '已完成' : '补交')}</p>
      <p><strong>类型：</strong>${task.isReview === 1 ? '复习知识点' : '新知识点'}</p>
      ${task.totalParts > 1 ? `<p><strong>进度：</strong>${Math.round((task.splitPart / task.totalParts) * 100)}%</p>` : ''}
      ${task.completionDate ? `<p><strong>完成时间：</strong>${new Date(task.completionDate).toLocaleString()}</p>` : ''}
    </div>
  `
  
  // 根据任务状态设置按钮
  const confirmButtonText = task.completed === 1 || task.completed === 2 ? '恢复为未完成' : '确定'
  
  ElMessageBox.confirm(detail, '任务详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: confirmButtonText,
    cancelButtonText: '取消'
  }).then(() => {
    // 如果任务已完成或补交完成，点击确认按钮则恢复为未完成
    if (task.completed === 1 || task.completed === 2) {
      handleTaskComplete(task, false)
    }
  }).catch(() => {
    // 取消操作，不做任何处理
  })
}

// 跳转到问卷页面
const goToQuestionnaire = () => {
  router.push({ name: 'StudentQuestionnaire' })
}

// 页面加载时获取数据
onMounted(() => {
  loadDashboardData()
})

// keep-alive激活时重新加载数据
onActivated(() => {
  loadDashboardData()
})

</script>

<style lang="less" scoped>
@import "@/assets/css/app.less";

.loading-container {
  padding: 20px;
}

.panel-group {
  margin-top: 18px;
  
  .card-panel-col {
    margin-bottom: 32px;
  }
  
  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.05);
    border: 1px solid #ebeef5;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: space-around;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      
      .card-panel-icon-wrapper {
        color: #fff;
      }
      
      .icon-people {
        background: #40c9c6;
      }
      
      .icon-message {
        background: #36a3f7;
      }
      
      .icon-money {
        background: #f4516c;
      }
      
      .icon-shopping {
        background: #34bfa3
      }
    }
    
    .icon-people {
      color: #40c9c6;
    }
    
    .icon-message {
      color: #36a3f7;
    }
    
    .icon-money {
      color: #f4516c;
    }
    
    .icon-shopping {
      color: #34bfa3;
    }
    
    .card-panel-icon-wrapper {
      float: left;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }
    
    .card-panel-icon {
      float: left;
      font-size: 48px;
    }
    
    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;
      
      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }
      
      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

.box-card {
  margin-top: 20px;
}

</style>
