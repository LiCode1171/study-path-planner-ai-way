# 甘特图测试数据示例

## 📊 测试数据1：30天计划（短期）

```javascript
const studyPlanData30 = {
  planName: '30天短期计划',
  totalDays: 30,
  dailyHours: 5,
  startDate: '2025-01-01',
  endDate: '2025-01-30',
  planItems: [
    // 第1天
    { id: 1, dayIndex: 1, sort: 1, knowledgePointId: 101, subjectName: '高等数学', completed: 1, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 2, dayIndex: 1, sort: 2, knowledgePointId: 201, subjectName: '大学英语', completed: 0, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    
    // 第2天 - 进行中
    { id: 3, dayIndex: 2, sort: 1, knowledgePointId: 102, subjectName: '高等数学', completed: 0, isReview: 0, splitPart: 1, totalParts: 3, splitHours: 1 },
    { id: 4, dayIndex: 2, sort: 2, knowledgePointId: 202, subjectName: '大学英语', completed: 1, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    
    // 第3天 - 补交
    { id: 5, dayIndex: 3, sort: 1, knowledgePointId: 103, subjectName: '高等数学', completed: 2, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 6, dayIndex: 3, sort: 2, knowledgePointId: 301, subjectName: '信息技术基础', completed: 0, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    
    // 第4天 - 复习
    { id: 7, dayIndex: 4, sort: 1, knowledgePointId: 101, subjectName: '高等数学', completed: 0, isReview: 1, splitPart: 1, totalParts: 1, splitHours: 1 },
    { id: 8, dayIndex: 4, sort: 2, knowledgePointId: 203, subjectName: '大学英语', completed: 0, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    
    // 第5-30天 - 未开始
    ...Array.from({ length: 26 }, (_, i) => ({
      id: 9 + i,
      dayIndex: 5 + i,
      sort: 1,
      knowledgePointId: 100 + i,
      subjectName: i % 3 === 0 ? '高等数学' : i % 3 === 1 ? '大学英语' : '信息技术基础',
      completed: 0,
      isReview: 0,
      splitPart: 1,
      totalParts: 1,
      splitHours: 2
    }))
  ]
}

const knowledgePointMap = {
  101: '函数的概念与性质',
  102: '导数与微分',
  103: '积分基础',
  201: '基础词汇',
  202: '语法结构',
  203: '阅读理解',
  301: '计算机基础',
  // ... 更多知识点
}
```

## 📊 测试数据2：365天计划（长期）

```javascript
const studyPlanData365 = {
  planName: '365天长期计划',
  totalDays: 365,
  dailyHours: 7,
  startDate: '2025-01-01',
  endDate: '2025-12-31',
  planItems: [
    // 第1-10天：基础阶段
    ...Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      dayIndex: i + 1,
      sort: 1,
      knowledgePointId: 100 + i,
      subjectName: ['高等数学', '大学英语', '信息技术基础', '思政理论'][i % 4],
      completed: i < 3 ? 1 : (i === 3 ? 2 : 0),
      isReview: 0,
      splitPart: 1,
      totalParts: 1,
      splitHours: 2
    })),
    
    // 第11-50天：进行中和跨天任务
    ...Array.from({ length: 40 }, (_, i) => ({
      id: 11 + i,
      dayIndex: 11 + i,
      sort: 1,
      knowledgePointId: 200 + i,
      subjectName: ['高等数学', '大学英语', '信息技术基础'][i % 3],
      completed: 0,
      isReview: 0,
      splitPart: i % 5 === 0 ? 1 : 1, // 部分任务可以设置跨天
      totalParts: i % 5 === 0 ? 3 : 1,
      splitHours: 2
    })),
    
    // 第51-365天：未开始
    ...Array.from({ length: 315 }, (_, i) => ({
      id: 51 + i,
      dayIndex: 51 + i,
      sort: 1,
      knowledgePointId: 500 + i,
      subjectName: ['高等数学', '大学英语', '信息技术基础', '思政理论'][i % 4],
      completed: 0,
      isReview: 0,
      splitPart: 1,
      totalParts: 1,
      splitHours: 2
    }))
  ]
}
```

## 🧪 在study-plan.vue中测试

```vue
<template>
  <div class="container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <h3>我的专属个性化学习路径</h3>
        <el-button style="float: right;" type="primary" @click="togglePlan">
          切换测试数据
        </el-button>
      </div>
      
      <!-- ECharts 甘特图组件 -->
      <StudyGanttChart
        v-if="studyPlanData && studyPlanData.planItems && studyPlanData.planItems.length > 0"
        :study-plan-data="studyPlanData"
        :knowledge-point-map="knowledgePointMap"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import StudyGanttChart from '@/components/student/StudyGanttChart.vue'

// 测试数据1：30天
const studyPlanData30 = {
  planName: '30天短期计划',
  totalDays: 30,
  dailyHours: 5,
  startDate: '2025-01-01',
  endDate: '2025-01-30',
  planItems: [
    { id: 1, dayIndex: 1, sort: 1, knowledgePointId: 101, subjectName: '高等数学', completed: 1, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 2, dayIndex: 1, sort: 2, knowledgePointId: 201, subjectName: '大学英语', completed: 0, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 3, dayIndex: 2, sort: 1, knowledgePointId: 102, subjectName: '高等数学', completed: 0, isReview: 0, splitPart: 1, totalParts: 3, splitHours: 1 },
    { id: 4, dayIndex: 2, sort: 2, knowledgePointId: 202, subjectName: '大学英语', completed: 1, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 5, dayIndex: 3, sort: 1, knowledgePointId: 103, subjectName: '高等数学', completed: 2, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 6, dayIndex: 3, sort: 2, knowledgePointId: 301, subjectName: '信息技术基础', completed: 0, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    { id: 7, dayIndex: 4, sort: 1, knowledgePointId: 101, subjectName: '高等数学', completed: 0, isReview: 1, splitPart: 1, totalParts: 1, splitHours: 1 },
    { id: 8, dayIndex: 4, sort: 2, knowledgePointId: 203, subjectName: '大学英语', completed: 0, isReview: 0, splitPart: 1, totalParts: 1, splitHours: 2 },
    ...Array.from({ length: 26 }, (_, i) => ({
      id: 9 + i,
      dayIndex: 5 + i,
      sort: 1,
      knowledgePointId: 100 + i,
      subjectName: i % 3 === 0 ? '高等数学' : i % 3 === 1 ? '大学英语' : '信息技术基础',
      completed: 0,
      isReview: 0,
      splitPart: 1,
      totalParts: 1,
      splitHours: 2
    }))
  ]
}

// 测试数据2：365天
const studyPlanData365 = {
  planName: '365天长期计划',
  totalDays: 365,
  dailyHours: 7,
  startDate: '2025-01-01',
  endDate: '2025-12-31',
  planItems: [
    ...Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      dayIndex: i + 1,
      sort: 1,
      knowledgePointId: 100 + i,
      subjectName: ['高等数学', '大学英语', '信息技术基础', '思政理论'][i % 4],
      completed: i < 3 ? 1 : (i === 3 ? 2 : 0),
      isReview: 0,
      splitPart: 1,
      totalParts: 1,
      splitHours: 2
    })),
    ...Array.from({ length: 40 }, (_, i) => ({
      id: 11 + i,
      dayIndex: 11 + i,
      sort: 1,
      knowledgePointId: 200 + i,
      subjectName: ['高等数学', '大学英语', '信息技术基础'][i % 3],
      completed: 0,
      isReview: 0,
      splitPart: i % 5 === 0 ? 1 : 1,
      totalParts: i % 5 === 0 ? 3 : 1,
      splitHours: 2
    })),
    ...Array.from({ length: 315 }, (_, i) => ({
      id: 51 + i,
      dayIndex: 51 + i,
      sort: 1,
      knowledgePointId: 500 + i,
      subjectName: ['高等数学', '大学英语', '信息技术基础', '思政理论'][i % 4],
      completed: 0,
      isReview: 0,
      splitPart: 1,
      totalParts: 1,
      splitHours: 2
    }))
  ]
}

const knowledgePointMap = {
  101: '函数的概念与性质',
  102: '导数与微分',
  103: '积分基础',
  201: '基础词汇',
  202: '语法结构',
  203: '阅读理解',
  301: '计算机基础',
  // ... 可以生成更多
}

// 当前显示的数据
const studyPlanData = ref(studyPlanData30)

// 切换测试数据
const togglePlan = () => {
  if (studyPlanData.value.totalDays === 30) {
    studyPlanData.value = studyPlanData365
  } else {
    studyPlanData.value = studyPlanData30
  }
}
</script>
```

## 🎯 预期测试结果

### 30天计划测试
- ✅ X轴显示：1, 2, 3, 4, 5...30（每个数字一个刻度）
- ✅ 滚动条：可以拖动查看第1-30天
- ✅ 状态颜色：已完成(绿)、进行中(蓝)、补交(红)、未开始(灰)
- ✅ 点击任务：弹窗显示详细信息
- ✅ 科目筛选：点击按钮可以筛选科目

### 365天计划测试
- ✅ X轴显示：1, 2, 3, 4, 5...365（每个数字一个刻度）
- ✅ 滚动条：可以拖动查看第1-365天（关键测试！）
- ✅ 鼠标滚轮：可以放大缩小查看范围
- ✅ 最小显示1天，最大显示30天范围
- ✅ 所有功能正常工作

## 🔍 调试技巧

在浏览器控制台检查：
```javascript
// 获取图表实例
const chart = echarts.getInstanceByDom(document.querySelector('.echarts-chart'))

// 检查配置
const option = chart.getOption()
console.log('xAxis配置:', option.xAxis)
console.log('dataZoom配置:', option.dataZoom)

// 检查数据
console.log('任务数量:', option.series[0].data.length)
console.log('X轴范围:', option.xAxis[0].min, '-', option.xAxis[0].max)
