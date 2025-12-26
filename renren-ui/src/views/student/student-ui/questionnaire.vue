<template>
  <div class="container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <h3>福建专升本学情诊断问卷</h3>
        <el-button style="float: right;" type="primary" @click="submitForm">提交问卷</el-button>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <!-- 招考类别选择 -->
        <el-form-item label="招考类别" prop="examCategory">
          <el-select 
            v-model="form.examCategory" 
            placeholder="请选择您的招考类别"
            style="width: 300px;"
            @change="handleCategoryChange">
            <el-option label="理工类" value="理工类" />
            <el-option label="经管类" value="经管类" />
            <el-option label="医学类" value="医学类" />
            <el-option label="农林生物医药类" value="农林生物医药类" />
            <el-option label="文史哲法类" value="文史哲法类" />
            <el-option label="教育类" value="教育类" />
            <el-option label="艺术类" value="艺术类" />
          </el-select>
        </el-form-item>

        <!-- 动态显示考试科目 -->
        <el-form-item label="考试科目" v-if="form.examCategory">
          <div class="subject-display">
            <h4>公共基础课（每门100分，共300分）</h4>
            <div class="subject-list">
              <div class="subject-item" v-for="subject in publicSubjects" :key="subject">
                <el-tag type="info">{{ subject }}</el-tag>
              </div>
            </div>
            
            <h4 style="margin-top: 16px;">专业基础课（300分）</h4>
            <div class="subject-list">
              <el-tag type="success">{{ professionalSubject }}</el-tag>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="目标院校" prop="targetSchool">
          <el-input v-model="form.targetSchool" placeholder="请输入您心仪的专升本院校" style="width: 300px;" />
        </el-form-item>
        
        <el-form-item label="当前专业" prop="currentMajor">
          <el-input v-model="form.currentMajor" placeholder="请输入您目前的专业" style="width: 300px;" />
        </el-form-item>
        
        <el-form-item label="目标专业" prop="targetMajor">
          <el-input v-model="form.targetMajor" placeholder="请输入您希望报考的专业" style="width: 300px;" />
        </el-form-item>
        
        <el-form-item label="备考时间" prop="preparationTime">
          <el-radio-group v-model="form.preparationTime">
            <el-radio label="3个月">3个月</el-radio>
            <el-radio label="6个月">6个月</el-radio>
            <el-radio label="9个月">9个月</el-radio>
            <el-radio label="12个月">12个月</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="每日学习时间" prop="dailyStudyTime">
          <el-select v-model="form.dailyStudyTime" placeholder="请选择" style="width: 200px;">
            <el-option label="1-2小时" value="1-2小时" />
            <el-option label="2-4小时" value="2-4小时" />
            <el-option label="4-6小时" value="4-6小时" />
            <el-option label="6小时以上" value="6小时以上" />
          </el-select>
        </el-form-item>

        <!-- 动态科目基础评估 -->
        <div v-if="form.examCategory" class="subject-assessment">
          <h3>各科目基础评估</h3>
          
          <!-- 公共基础课评估 -->
          <div class="assessment-section">
            <h4>公共基础课</h4>
            <el-form-item 
              v-for="subject in publicSubjects" 
              :key="subject" 
              :label="subject" 
              class="subject-level-item">
              <el-rate
                v-model="form.subjectLevels[subject]"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                show-text
                :texts="['零基础', '基础薄弱', '一般', '良好', '优秀']">
              </el-rate>
            </el-form-item>
          </div>

          <!-- 专业基础课评估 -->
          <div class="assessment-section">
            <h4>专业基础课</h4>
            <el-form-item :label="professionalSubject" class="subject-level-item">
              <el-rate
                v-model="form.subjectLevels[professionalSubject]"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                show-text
                :texts="['零基础', '基础薄弱', '一般', '良好', '优秀']">
              </el-rate>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import baseService from '@/service/baseService'
const router = useRouter()
const formRef = ref(null)

// 表单数据 - 直接定义初始值
const form = reactive({
  examCategory: '',
  targetSchool: '',
  currentMajor: '',
  targetMajor: '',
  preparationTime: '',
  dailyStudyTime: '',
  subjectLevels: {}
})

// 表单验证规则
const rules = {
  examCategory: [
    { required: true, message: '请选择招考类别', trigger: 'change' }
  ],
  targetSchool: [
    { required: true, message: '请输入目标院校', trigger: 'blur' }
  ],
  targetMajor: [
    { required: true, message: '请输入目标专业', trigger: 'blur' }
  ],
  preparationTime: [
    { required: true, message: '请选择备考时间', trigger: 'change' }
  ],
  dailyStudyTime: [
    { required: true, message: '请选择每日学习时间', trigger: 'change' }
  ]
}

// 科目配置
const subjectConfig = {
  '理工类': {
    public: ['思政理论', '大学英语', '高等数学'],
    professional: '信息技术基础'
  },
  '经管类': {
    public: ['思政理论', '大学英语', '大学语文'],
    professional: '经济学与管理学基础'
  },
  '医学类': {
    public: ['思政理论', '大学英语', '高等数学'],
    professional: '医学基础'
  },
  '农林生物医药类': {
    public: ['思政理论', '大学英语', '高等数学'],
    professional: '化学基础'
  },
  '文史哲法类': {
    public: ['思政理论', '大学英语', '大学语文'],
    professional: '文史基础'
  },
  '教育类': {
    public: ['思政理论', '大学英语', '大学语文'],
    professional: '教育理论基础'
  },
  '艺术类': {
    public: ['思政理论', '大学英语', '大学语文'],
    professional: '艺术基础'
  }
}

// 计算属性
const publicSubjects = computed(() => {
  return form.examCategory ? subjectConfig[form.examCategory]?.public || [] : []
})

const professionalSubject = computed(() => {
  return form.examCategory ? subjectConfig[form.examCategory]?.professional || '' : ''
})

// 方法
const handleCategoryChange = () => {
  // 重置科目基础评估
  form.subjectLevels = {}
  
  // 初始化所有科目的基础水平为0
  publicSubjects.value.forEach(subject => {
    form.subjectLevels[subject] = 0
  })
  if (professionalSubject.value) {
    form.subjectLevels[professionalSubject.value] = 0
  }
}

// 使用 watch 监听 examCategory 变化
watch(() => form.examCategory, (newVal, oldVal) => {
  if (newVal && newVal !== oldVal) {
    handleCategoryChange()
  }
})
// 重置表单的方法 - 使用 resetFields()
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }

}
const submitForm = () => {
  if (!formRef.value) return
  
  formRef.value.validate(async (valid) => {
    if (valid) {
      // 验证所有科目都已评分
      const allSubjectsRated = Object.values(form.subjectLevels).every(level => level > 0)
      
      if (!allSubjectsRated) {
        ElMessage.error('请为所有科目评估基础水平')
        return false
      }
  
      try {
        ElMessage.success('问卷提交中...')
        
        // 使用baseService调用后端API - 遵循项目现有模式
        const response = await baseService.post('/student/questionnaire/submit', form)
        
        if (response.code === 0) {
          ElMessage.success('问卷提交成功，正在为您生成个性化学习路径...')
          
          // 提交成功后重置表单 - 使用 resetFields()
          resetForm()
  
          // 跳转到学习计划页面
          router.push({ name: 'StudentStudyPlan' })
        } else {
          ElMessage.error(response.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      }
    } else {
      ElMessage.error('请填写完整信息')
      return false
    }
  })
}
</script>

<style lang="less" scoped>
@import "@/assets/css/app.less";

.box-card {
  margin-top: 20px;
}

.subject-display {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 4px;
  
  h4 {
    margin-bottom: 12px;
    color: #303133;
  }
}

.subject-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.subject-assessment {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
  
  h3 {
    margin-bottom: 20px;
    color: #303133;
  }
}

.assessment-section {
  margin-bottom: 24px;
  
  h4 {
    margin-bottom: 16px;
    color: #606266;
    font-weight: 500;
  }
}

.subject-level-item {
  margin-bottom: 20px;
  
  :deep(.el-form-item__label) {
    width: 150px !important;
  }
  
  :deep(.el-form-item__content) {
    margin-left: 80px !important;
  }
}

// 响应式调整
@media (max-width: 768px) {
  .subject-level-item {
    :deep(.el-form-item__label) {
      width: 100px !important;
    }
    
    :deep(.el-form-item__content) {
      margin-left: 100px !important;
    }
  }
  
  .subject-list {
    flex-direction: column;
    gap: 8px;
  }
}
</style>