<template>
  <div class="study-gantt-chart">
    <!-- 统计卡片区域 -->
    <div class="stats-container" v-if="studyPlanData">
      <div class="stat-card">
        <div class="stat-value">{{ studyPlanData.totalDays }}</div>
        <div class="stat-label">总天数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ studyPlanData.dailyHours }}</div>
        <div class="stat-label">每日学习(小时)</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ remainingDays }}</div>
        <div class="stat-label">剩余天数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ overallProgress }}%</div>
        <div class="stat-label">当前进度</div>
      </div>
    </div>

    <!-- 科目筛选按钮 -->
    <div class="filter-buttons">
      <el-button 
        size="small"
        :type="currentFilter === 'all' ? 'primary' : ''"
        @click="filterSubject('all')"
      >
        全部科目
      </el-button>
      <el-button 
        v-for="subject in allSubjects" 
        :key="subject"
        size="small"
        :type="currentFilter === subject ? 'primary' : ''"
        @click="filterSubject(subject)"
      >
        {{ subject }}
      </el-button>
    </div>

    <!-- ECharts 图表 -->
    <v-chart 
      v-if="chartOption"
      ref="chartRef"
      class="echarts-chart"
      :option="chartOption"
      :autoresize="true"
      @click="handleChartClick"
    />
    <div v-else class="echarts-empty">
      <div class="empty-content">
        <div class="empty-text">暂无数据或数据加载中...</div>
      </div>
    </div>

    <!-- 状态图例 -->
    <div class="status-legend">
      <div class="legend-item">
        <div class="legend-color" :style="{ background: statusColors[1] }"></div>
        <span>已完成</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ background: statusColors[3] }"></div>
        <span>进行中</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ background: statusColors[2] }"></div>
        <span>补交</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ background: statusColors[0] }"></div>
        <span>未开始</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ background: 'transparent', border: '2px dashed #e67e22', width: '14px', height: '10px', borderRadius: 0 }"></div>
        <span>复习</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, defineAsyncComponent, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

// 局部注册vue-echarts组件
const VChart = defineAsyncComponent(() => import('vue-echarts'))

const props = defineProps({
  studyPlanData: {
    type: Object,
    required: true,
    default: () => ({})
  },
  knowledgePointMap: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

// 状态颜色映射
const statusColors = {
  0: '#95a5a6',  // 未开始 - 灰色
  1: '#27ae60',  // 已完成 - 绿色
  2: '#f4516c',  // 补交 - 红色
  3: '#3498db'   // 进行中 - 蓝色
}

// 当前筛选
const currentFilter = ref('all')

// 图表引用
const chartRef = ref(null)

// 所有科目
const allSubjects = computed(() => {
  if (!props.studyPlanData?.planItems) return []
  const subjects = new Set(props.studyPlanData.planItems.map(item => item.subjectName))
  return Array.from(subjects)
})

// 剩余天数
const remainingDays = computed(() => {
  if (!props.studyPlanData?.startDate || !props.studyPlanData?.endDate) return 0
  
  const startDate = new Date(props.studyPlanData.startDate)
  const endDate = new Date(props.studyPlanData.endDate)
  const today = new Date()
  
  startDate.setHours(0, 0, 0, 0)
  endDate.setHours(0, 0, 0, 0)
  today.setHours(0, 0, 0, 0)
  
  if (today < startDate) {
    return props.studyPlanData.totalDays
  }
  if (today >= endDate) {
    return 0
  }
  
  const diffTime = endDate.getTime() - today.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  return Math.min(diffDays, props.studyPlanData.totalDays)
})

// 总体进度
const overallProgress = computed(() => {
  if (!props.studyPlanData?.planItems) return 0
  
  const totalItems = props.studyPlanData.planItems.length
  const completedItems = props.studyPlanData.planItems.filter(item => item.completed === 1).length
  
  return totalItems > 0 ? Math.round((completedItems / totalItems) * 100) : 0
})

// 计算当前天数
const todayDayIndex = computed(() => {
  if (!props.studyPlanData?.startDate) return 1
  
  const startDate = new Date(props.studyPlanData.startDate)
  const today = new Date()
  
  startDate.setHours(0, 0, 0, 0)
  today.setHours(0, 0, 0, 0)
  
  const daysDiff = Math.floor((today - startDate) / (1000 * 60 * 60 * 24))
  return daysDiff + 1
})

// 获取任务状态（修复问题四：正确的状态颜色逻辑）
const getTaskStatus = (task) => {
  // 1. 未开始状态 - 灰色
  if (task.completed === 0) {
    return 0
  }
  
  // 2. 补交状态 - 红色
  if (task.completed === 2) {
    return 2
  }
  
  // 3. 已完成状态 - 需要判断进度
  if (task.completed === 1) {
    // 计算进度百分比
    const progress = task.totalParts > 0 ? (task.splitPart / task.totalParts) * 100 : 100
    
    // 进度100% - 绿色（已完成）
    if (progress >= 100) {
      return 1
    }
    // 进度未100% - 蓝色（进行中）
    else {
      return 3
    }
  }
  
  // 默认未开始
  return 0
}

// 数据转换
const transformData = () => {
  if (!props.studyPlanData?.planItems) return []
  
  let items = props.studyPlanData.planItems
  
  // 筛选科目
  if (currentFilter.value !== 'all') {
    items = items.filter(item => item.subjectName === currentFilter.value)
  }
  
  // 转换为ECharts数据
  return items.map(item => {
    const status = getTaskStatus(item)
    const subjectIndex = allSubjects.value.indexOf(item.subjectName)
    
    return {
      value: [
        subjectIndex,
        item.dayIndex - 0.5,
        item.dayIndex + 0.5,
        item.knowledgePointId,
        item.dayIndex,
        item.sort,
        status,
        item.splitPart,
        item.totalParts,
        item.isReview,
        item.subjectName
      ],
      itemStyle: {
        color: statusColors[status]
      },
      originalData: item,
      status: status
    }
  })
}

// 获取唯一科目列表（筛选后）
const getFilteredSubjects = () => {
  if (!props.studyPlanData?.planItems) return []
  
  let items = props.studyPlanData.planItems
  if (currentFilter.value !== 'all') {
    items = items.filter(item => item.subjectName === currentFilter.value)
  }
  
  const subjects = new Set(items.map(item => item.subjectName))
  return Array.from(subjects)
}

// 创建图表配置（改为computed属性）
const chartOption = computed(() => {
  if (!props.studyPlanData || !props.studyPlanData.totalDays) {
    return null
  }
  
  const data = transformData()
  const filteredSubjects = getFilteredSubjects()
  
    // 计算实际的天数范围（动态获取全部天数，不显示空白）
    if (data.length === 0) {
      return null
    }
    
    // 获取数据中的最小和最大天数
    const dayIndices = data.map(d => d.value[4]) // dayIndex在value数组中的位置
    const minDay = Math.min(...dayIndices)
    const maxDay = Math.max(...dayIndices)
    const actualDays = maxDay - minDay + 1
    
    // 计算X轴间隔，确保一天一格，平均分配
    const interval = 1
    
    return {
      backgroundColor: 'transparent',
      title: {
        text: '学习进度甘特图',
        subtext: `显示第${minDay}-${maxDay}天计划 (${data.length}个任务)`,
        left: 'center',
        textStyle: {
          color: '#303133',
          fontSize: 18,
          fontWeight: 'bold'
        },
        subtextStyle: {
          color: '#909399',
          fontSize: 13
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#dcdfe6',
        borderWidth: 1,
        textStyle: {
          color: '#303133',
          fontSize: 13
        },
        formatter: function(params) {
          if (!params.data || !params.data.originalData) return ''
          const data = params.data.originalData
          const status = params.data.status
          const statusText = status === 1 ? '已完成' : status === 2 ? '补交' : status === 3 ? '进行中' : '未开始'
          const statusColor = statusColors[status]
          
          const progress = data.totalParts > 1 
            ? `${Math.round((data.splitPart / data.totalParts) * 100)}%` 
            : '100%'
          
          const typeText = data.isReview === 1 ? '复习' : '新知识点'
          
          return `
            <div style="padding: 8px; min-width: 200px;">
              <div style="font-weight: bold; margin-bottom: 6px; font-size: 14px; color: #303133;">
                ${props.knowledgePointMap[data.knowledgePointId] || `知识点 ${data.knowledgePointId}`}
              </div>
              <div style="margin-bottom: 4px; display: flex; align-items: center;">
                <span style="color: #909399; width: 60px;">科目：</span>
                <span style="color: #303133; font-weight: 500;">${data.subjectName}</span>
              </div>
              <div style="margin-bottom: 4px; display: flex; align-items: center;">
                <span style="color: #909399; width: 60px;">天数：</span>
                <span style="color: #303133;">第${data.dayIndex}天</span>
              </div>
              <div style="margin-bottom: 4px; display: flex; align-items: center;">
                <span style="color: #909399; width: 60px;">类型：</span>
                <span style="color: #303133;">${typeText}</span>
              </div>
              <div style="margin-bottom: 4px; display: flex; align-items: center;">
                <span style="color: #909399; width: 60px;">进度：</span>
                <span style="color: #303133;">${progress}</span>
              </div>
              <div style="display: flex; align-items: center;">
                <span style="color: #909399; width: 60px;">状态：</span>
                <span style="color: ${statusColor}; font-weight: 500;">${statusText}</span>
              </div>
            </div>
          `
        }
      },
      grid: {
        left: '15%',
        right: '5%',
        top: '15%',
        bottom: '20%'  // 增加底部空间，避免文字被滚动条遮挡
      },
      xAxis: {
        type: 'value',
        name: '天数',
        nameLocation: 'middle',
        nameGap: 35,  // 增加名称间距
        min: 0,  // ✅ 从0开始，确保第一天能完整显示
        max: maxDay + 1,  // ✅ 到实际天数+1结束，确保最后一天能完整显示
        interval: interval,  // 强制一天一格
        axisLine: {
          lineStyle: {
            color: '#909399'
          }
        },
        axisLabel: {
          color: '#606266',
          formatter: function(value) {
            // 使用四舍五入，确保只显示整数
            return Math.round(value)
          },
          margin: 8
        },
        nameTextStyle: {
          color: '#909399'
        },
        splitLine: {
          lineStyle: {
            color: 'rgba(0, 0, 0, 0.05)'
          }
        }
      },
    yAxis: {
      type: 'category',
      data: filteredSubjects,
      axisLine: {
        lineStyle: {
          color: '#909399'
        }
      },
      axisLabel: {
        color: '#606266',
        formatter: function(value) {
          return value.length > 8 ? value.substring(0, 8) + '...' : value
        }
      },
      axisTick: {
        alignWithLabel: true,
        lineStyle: {
          color: 'rgba(0, 0, 0, 0.1)'
        }
      }
    },
    series: [{
      type: 'custom',
      name: '学习任务',
      renderItem: function(params, api) {
        const categoryIndex = api.value(0)
        const start = api.coord([api.value(1), categoryIndex])
        const end = api.coord([api.value(2), categoryIndex])
        const height = api.size([0, 1])[1] * 0.6
        
        // 获取是否为复习项
        const isReview = api.value(9)
        
        const rectShape = echarts.graphic.clipRectByRect({
          x: start[0],
          y: start[1] - height / 2,
          width: end[0] - start[0],
          height: height
        }, {
          x: params.coordSys.x,
          y: params.coordSys.y,
          width: params.coordSys.width,
          height: params.coordSys.height
        })
        
        return rectShape && {
          type: 'rect',
          shape: rectShape,
          style: {
            fill: api.visual('color'),
            stroke: isReview ? '#e67e22' : '#ffffff',  // 复习项用橙色边框
            strokeDashArray: isReview ? [5, 5] : [],  // 复习项用虚线
            lineWidth: isReview ? 2 : 1
          },
          emphasis: {
            style: {
              shadowBlur: 8,
              shadowColor: 'rgba(0, 0, 0, 0.2)',
              stroke: isReview ? '#e67e22' : '#333333',  // 复习项hover也用橙色
              strokeDashArray: isReview ? [5, 5] : [],
              lineWidth: 2
            }
          }
        }
      },
      encode: {
        x: [1, 2],
        y: 0
      },
      data: data,
      animation: true,
      animationDuration: 800,
      animationEasing: 'cubicOut'
    }],
    dataZoom: [
      {
        type: 'slider',
        xAxisIndex: 0,
        start: 0,
        end: 100,
        minSpan: 7 / actualDays * 100,  // ✅ 最小显示7天
        maxSpan: 30 / actualDays * 100,  // ✅ 最大显示30天
        precision: 0,  // ✅ 整数精度
        realTime: true,  // ✅ 实时更新
        height: 25,
        bottom: 30,  // 增加底部距离，避免遮挡文字
        handleStyle: {
          color: '#3498db',
          borderColor: '#3498db',
          shadowBlur: 2,
          shadowColor: 'rgba(52, 152, 219, 0.3)'
        },
        backgroundColor: 'rgba(255, 255, 255, 0.8)',
        borderColor: '#dcdfe6',
        fillerColor: 'rgba(52, 152, 219, 0.2)',
        textStyle: {
          color: '#606266',
          fontSize: 11
        },
        showDetail: false,
        showDataShadow: false
      },
      {
        type: 'inside',
        xAxisIndex: 0,
        minSpan: 7 / actualDays * 100,
        maxSpan: 30 / actualDays * 100
      }
    ]
  }
})

// 处理任务点击（供v-chart使用）
const handleChartClick = (params) => {
  if (params.data && params.data.originalData) {
    const task = params.data.originalData
    const knowledgeName = props.knowledgePointMap[task.knowledgePointId] || `知识点 ${task.knowledgePointId}`
    const status = getTaskStatus(task)
    const statusText = status === 1 ? '已完成' : status === 2 ? '补交' : status === 3 ? '进行中' : '未开始'
    
    ElMessageBox.confirm(
      `
        <div style="padding: 10px; line-height: 1.8;">
          <p><strong>知识点：</strong>${knowledgeName}</p>
          <p><strong>科目：</strong>${task.subjectName}</p>
          <p><strong>第几天：</strong>第${task.dayIndex}天</p>
          <p><strong>顺序：</strong>第${task.sort}个</p>
          <p><strong>状态：</strong><span style="color: ${statusColors[status]}">${statusText}</span></p>
          <p><strong>类型：</strong>${task.isReview === 1 ? '复习知识点' : '新知识点'}</p>
          ${task.totalParts > 1 ? `<p><strong>进度：</strong>${Math.round((task.splitPart / task.totalParts) * 100)}%</p>` : ''}
        </div>
      `,
      '任务详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        
      }
    ).catch(() => {
      // 用户点击取消，不做处理
    })
  }
}

// 筛选科目
const filterSubject = (subject) => {
  currentFilter.value = subject
  // v-chart会自动响应chartOption的变化
}

// 监听props变化，自动更新
watch(() => props.studyPlanData, (newVal) => {
  // chartOption是computed，会自动更新
}, { deep: true })

// 监听筛选变化，自动更新
watch(currentFilter, () => {
  // chartOption是computed，会自动更新
})

// 生命周期
onMounted(() => {
  // chartOption是computed，会自动更新
})
</script>

<style lang="less" scoped>
.study-gantt-chart {
  width: 100%;
  margin-bottom: 30px;
  
  // 统计卡片
  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 15px;
    margin-bottom: 20px;
    
    .stat-card {
      background: #ffffff;
      border-radius: 8px;
      padding: 15px;
      text-align: center;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
      border: 1px solid #ebeef5;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 5px;
      }
      
      .stat-label {
        font-size: 13px;
        color: #909399;
      }
    }
  }
  
  // 筛选按钮
  .filter-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 15px;
  }
  
  // 图表容器
  .echarts-chart {
    width: 100%;
    height: 500px;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #ebeef5;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 15px;
  }
  
  // 状态图例
  .status-legend {
    display: flex;
    justify-content: center;
    gap: 20px;
    padding: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #ebeef5;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      
      .legend-color {
        width: 16px;
        height: 12px;
        border-radius: 2px;
      }
      
      span {
        font-size: 13px;
        color: #606266;
        font-weight: 500;
      }
    }
  }
  
  // 空状态
  .echarts-empty {
    width: 100%;
    height: 500px;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #ebeef5;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .empty-content {
      text-align: center;
      
      .empty-text {
        color: #909399;
        font-size: 16px;
        margin-top: 10px;
      }
    }
  }
}
</style>
