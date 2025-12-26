<template>
  <div class="container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <h3>推荐学习资源</h3>
        <p class="text-2" style="margin: 8px 0 0 0;">根据您的学习路径为您精心筛选的学习资料</p>
      </div>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="8" v-for="resource in resources" :key="resource.id" style="margin-bottom: 20px;">
          <el-card class="resource-card" shadow="hover">
            <div slot="header" class="resource-header">
              <span class="resource-title">{{ resource.title }}</span>
              <el-tag :type="resource.tagType" size="small">{{ resource.type }}</el-tag>
            </div>
            <div class="resource-content">
              <p class="resource-desc">{{ resource.description }}</p>
              <div class="resource-meta text-2">
                <span><el-icon><Clock /></el-icon> {{ resource.duration }}</span>
                <span style="margin-left: 16px;"><el-icon><Collection /></el-icon> {{ resource.difficulty }}</span>
              </div>
            </div>
            <div class="resource-actions">
              <el-button type="primary" size="small" @click="openResource(resource)">开始学习</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { Clock, Collection } from '@element-plus/icons-vue'

export default {
  name: 'StudentResources',
  components: {
    Clock,
    Collection
  },
  data() {
    return {
      resources: [
        {
          id: 1,
          title: '高等数学基础入门',
          type: '视频课程',
          tagType: 'success',
          description: '从零开始学习高等数学基本概念和公式，适合专升本考生系统复习。',
          duration: '2.5小时',
          difficulty: '初级',
          url: '#'
        },
        {
          id: 2,
          title: '专升本英语核心词汇',
          type: '文档资料',
          tagType: 'warning',
          description: '专升本考试必备的1500个核心英语词汇，包含记忆技巧和例句。',
          duration: '1小时',
          difficulty: '初级',
          url: '#'
        },
        {
          id: 3,
          title: '计算机基础习题集',
          type: '练习题',
          tagType: 'danger',
          description: '包含历年真题和模拟题的练习题库，帮助巩固知识点。',
          duration: '3小时',
          difficulty: '中级',
          url: '#'
        }
      ]
    }
  },
  methods: {
    openResource(resource) {
      this.$message.info(`即将打开: ${resource.title}`)
      // 后续这里实现打开资源链接的逻辑
    }
  }
}
</script>

<style lang="less" scoped>
@import "@/assets/css/app.less";

.resource-card {
  height: 200px;
  display: flex;
  flex-direction: column;
  
  .resource-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 0;
    
    .resource-title {
      flex: 1;
      margin-right: 12px;
      font-weight: 500;
    }
  }
  
  .resource-content {
    flex: 1;
    padding: 0;
    
    .resource-desc {
      color: #606266;
      line-height: 1.5;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .resource-meta {
      font-size: 12px;
      display: flex;
      align-items: center;
      
      .el-icon {
        margin-right: 4px;
      }
    }
  }
  
  .resource-actions {
    text-align: right;
    margin-top: 12px;
  }
}

.box-card {
  margin-top: 20px;
  border: 1px solid rgb(255, 255, 255); /* 修改边框颜色 */
}
</style>