<template>
  <el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" @keyup.enter="dataFormSubmitHandle()" label-width="120px">
          <el-form-item label="用户ID" prop="userId">
        <el-input v-model="dataForm.userId" placeholder="用户ID"></el-input>
      </el-form-item>
          <el-form-item label="招考类别" prop="examCategory">
        <el-input v-model="dataForm.examCategory" placeholder="招考类别(理工类/经管类/医学类等)"></el-input>
      </el-form-item>
          <el-form-item label="目标院校" prop="targetSchool">
        <el-input v-model="dataForm.targetSchool" placeholder="目标院校"></el-input>
      </el-form-item>
          <el-form-item label="当前专业" prop="currentMajor">
        <el-input v-model="dataForm.currentMajor" placeholder="当前专业"></el-input>
      </el-form-item>
          <el-form-item label="目标专业" prop="targetMajor">
        <el-input v-model="dataForm.targetMajor" placeholder="目标专业"></el-input>
      </el-form-item>
          <el-form-item label="备考月数" prop="preparationMonths">
        <el-input v-model="dataForm.preparationMonths" placeholder="备考月数"></el-input>
      </el-form-item>
          <el-form-item label="每日学习时间/h" prop="dailyStudyTime">
        <el-input v-model="dataForm.dailyStudyTime" placeholder="每日学习时间/h"></el-input>
      </el-form-item>
          <el-form-item label="公共基础课类型" prop="publicCourseType">
        <el-input v-model="dataForm.publicCourseType" placeholder="公共基础课类型(大学语文/高等数学)"></el-input>
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
  id: '',  userId: '',  examCategory: '',  targetSchool: '',  currentMajor: '',  targetMajor: '',  preparationMonths: '',  dailyStudyTime: '',  publicCourseType: '',  createDate: '',  updateDate: ''});

const rules = ref({
          userId: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          examCategory: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          targetSchool: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          currentMajor: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          targetMajor: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          preparationMonths: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          dailyStudyTime: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          publicCourseType: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
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
  baseService.get("/student/userprofile/" + id).then((res) => {
    Object.assign(dataForm, res.data);
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/student/userprofile", dataForm).then((res) => {
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
