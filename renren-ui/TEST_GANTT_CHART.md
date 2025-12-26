# 甘特图组件测试说明

## 🧪 测试目标

验证StudyGanttChart组件在不同总天数下的滚动条显示效果，确保：
- ✅ 30天计划：可以查看全部范围
- ✅ 365天计划：可以查看全部范围，不受30天限制
- ✅ 滚动条可以自由拖动到任何位置
- ✅ 鼠标滚轮可以正常缩放

## 📊 测试场景

### 场景1：30天计划（短期）
```javascript
studyPlanData: {
  totalDays: 30,
  dailyHours: 5,
  startDate: '2025-01-01',
  endDate: '2025-01-30',
  planItems: [/* 30天的任务 */]
}
```
**预期效果**：
- 滚动条可以拖动查看第1-30天
- 初始显示全部（0-100%）
- 鼠标滚轮可以缩放

### 场景2：90天计划（中期）
```javascript
studyPlanData: {
  totalDays: 90,
  dailyHours: 6,
  startDate: '2025-01-01',
  endDate: '2025-03-31',
  planItems: [/* 90天的任务 */]
}
```
**预期效果**：
- 滚动条可以拖动查看第1-90天
- 初始显示全部（0-100%）
- 鼠标滚轮可以缩放

### 场景3：365天计划（长期）
```javascript
studyPlanData: {
  totalDays: 365,
  dailyHours: 7,
  startDate: '2025-01-01',
  endDate: '2025-12-31',
  planItems: [/* 365天的任务 */]
}
```
**预期效果**：
- ✅ **关键测试点**：滚动条可以拖动查看第1-365天
- ✅ **关键测试点**：不受之前11.5天的限制
- 初始显示全部（0-100%）
- 鼠标滚轮可以缩放

## 🔧 验证步骤

### 1. 检查滚动条范围
```javascript
// 在浏览器控制台检查
const chart = echarts.getInstanceByDom(document.querySelector('.chart-container'))
const option = chart.getOption()
console.log('dataZoom配置:', option.dataZoom)
```

**应该看到**：
```javascript
{
  type: 'slider',
  start: 0,  // ✅ 从0开始
  end: 100,  // ✅ 到100结束
  // ❌ 不应该有 minSpan 和 maxSpan
}
```

### 2. 拖动滚动条测试
- 拖动滚动条到最左边 → 应该看到第1天附近
- 拖动滚动条到最右边 → 应该看到最后一天（第365天）
- 拖动到中间位置 → 应该看到对应天数范围

### 3. 鼠标滚轮测试
- 在图表区域使用鼠标滚轮 → 应该可以放大缩小
- 滚轮向上 → 放大（显示更少天数）
- 滚轮向下 → 缩小（显示更多天数）

### 4. 科目筛选测试
- 点击"全部科目" → 显示所有科目
- 点击单个科目 → 只显示该科目的任务
- 筛选后滚动条应该仍然可以自由拖动

## 🐛 如果仍有问题

如果滚动条仍然只能显示11.5天，请检查：

### 1. ECharts版本
```bash
cd renren-ui
npm list echarts
```
应该显示：`echarts@^5.2.2`

### 2. 组件代码
确保`getChartOption()`函数中的dataZoom配置为：
```javascript
dataZoom: [
  {
    type: 'slider',
    xAxisIndex: 0,
    start: 0,
    end: 100,
    // ❌ 没有 minSpan
    // ❌ 没有 maxSpan
  },
  {
    type: 'inside',
    xAxisIndex: 0
    // ❌ 没有 minSpan
    // ❌ 没有 maxSpan
  }
]
```

### 3. 浏览器缓存
如果修改后没生效，尝试：
- 清除浏览器缓存 (Ctrl+Shift+R)
- 重启开发服务器
- 重新编译项目

## 📝 预期结果总结

| 总天数 | 初始显示 | 最小可查看 | 最大可查看 | 是否符合预期 |
|--------|----------|------------|------------|--------------|
| 30天 | 第1-30天 | 第1-30天 | 第1-30天 | ✅ |
| 90天 | 第1-90天 | 第1-90天 | 第1-90天 | ✅ |
| 365天 | 第1-365天 | 第1-365天 | 第1-365天 | ✅ |

**关键验证点**：365天计划必须能查看全部范围，不受任何限制！
