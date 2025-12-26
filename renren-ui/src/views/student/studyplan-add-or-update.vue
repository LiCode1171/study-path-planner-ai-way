<template>
  <el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" @keyup.enter="dataFormSubmitHandle()" label-width="120px">
          <el-form-item label="用户ID" prop="userId">
        <el-input v-model="dataForm.userId" placeholder="用户ID"></el-input>
      </el-form-item>
          <el-form-item label="计划名称" prop="planName">
        <el-input v-model="dataForm.planName" placeholder="计划名称"></el-input>
      </el-form-item>
          <el-form-item label="总天数" prop="totalDays">
        <el-input v-model="dataForm.totalDays" placeholder="总天数"></el-input>
      </el-form-item>
          <el-form-item label="每日学习小时数" prop="dailyHours">
        <el-input v-model="dataForm.dailyHours" placeholder="每日学习小时数"></el-input>
      </el-form-item>
          <el-form-item label="开始日期" prop="startDate">
        <el-input v-model="dataForm.startDate" placeholder="开始日期"></el-input>
      </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
        <el-input v-model="dataForm.endDate" placeholder="结束日期"></el-input>
      </el-form-item>
          <el-form-item label="完成状态" prop="status">
        <el-input v-model="dataForm.status" placeholder="完成状态：0-未开始，1-进行中，2-已完成"></el-input>
      </el-form-item>
              <el-form-item label="总科目数" prop="totalSubjects">
        <el-input v-model="dataForm.totalSubjects" placeholder="总科目数"></el-input>
      </el-form-item>
          <el-form-item label="已完成科目数" prop="completedSubjects">
        <el-input v-model="dataForm.completedSubjects" placeholder="已完成科目数"></el-input>
      </el-form-item>
          <el-form-item label="总复习天数" prop="totalReviewDays">
        <el-input v-model="dataForm.totalReviewDays" placeholder="总复习天数"></el-input>
      </el-form-item>
          <el-form-item label="总体进度" prop="overallProgress">
        <el-input v-model="dataForm.overallProgress" placeholder="总体进度"></el-input>
      </el-form-item>
      </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import baseService from "@/service/baseService";
import { ElMessage } from "element-plus";
const emit = defineEmits(["refreshDataList"]);

const visible = ref(false);
const dataFormRef = ref();

const dataForm = reactive({
  id: '',  userId: '',  planName: '',  totalDays: '',  dailyHours: '',  startDate: '',  endDate: '',  status: '',  createDate: '',  updateDate: '',  totalSubjects: '',  completedSubjects: '',  totalReviewDays: '',  overallProgress: ''});

const rules = ref({
          userId: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          planName: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          totalDays: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          dailyHours: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          startDate: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          endDate: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          status: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
              totalSubjects: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          completedSubjects: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          totalReviewDays: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          overallProgress: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ]
  });

const init = (id?: number) => {
  visible.value = true;
  dataForm.id = "";

  // 重置表单数据
  if (dataFormRef.value) {
    dataFormRef.value.resetFields();
  }

  if (id) {
    getInfo(id);
  }
};

// 获取信息
const getInfo = (id: number) => {
  baseService.get("/student/studyplan/" + id).then((res) => {
    Object.assign(dataForm, res.data);
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/student/studyplan", dataForm).then((res) => {
      ElMessage.success({
        message: '成功',
        duration: 500,
        onClose: () => {
          visible.value = false;
          emit("refreshDataList");
        }
      });
    });
  });
};

defineExpose({
  init
});
</script>
