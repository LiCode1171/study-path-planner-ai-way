<template>
  <el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" @keyup.enter="dataFormSubmitHandle()" label-width="120px">
          <el-form-item label="招考类别" prop="examCategory">
        <el-input v-model="dataForm.examCategory" placeholder="招考类别"></el-input>
      </el-form-item>
          <el-form-item label="科目名称" prop="subjectName">
        <el-input v-model="dataForm.subjectName" placeholder="科目名称"></el-input>
      </el-form-item>
          <el-form-item label="章节" prop="chapter">
        <el-input v-model="dataForm.chapter" placeholder="章节"></el-input>
      </el-form-item>
          <el-form-item label="知识点名称" prop="pointName">
        <el-input v-model="dataForm.pointName" placeholder="知识点名称"></el-input>
      </el-form-item>
          <el-form-item label="难度等级" prop="difficulty">
        <el-input v-model="dataForm.difficulty" placeholder="难度等级1-5"></el-input>
      </el-form-item>
          <el-form-item label="预计学习时长/h" prop="estimatedHours">
        <el-input v-model="dataForm.estimatedHours" placeholder="预计学习时长/h"></el-input>
      </el-form-item>
          <el-form-item label="考试权重" prop="weight">
        <el-input v-model="dataForm.weight" placeholder="考试权重"></el-input>
      </el-form-item>
          <el-form-item label="前置知识点ID" prop="prerequisiteIds">
        <el-input v-model="dataForm.prerequisiteIds" placeholder="前置知识点ID"></el-input>
      </el-form-item>
          <el-form-item label="知识点简述" prop="description">
        <el-input v-model="dataForm.description" placeholder="知识点简述"></el-input>
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
  id: '',  examCategory: '',  subjectName: '',  chapter: '',  pointName: '',  difficulty: '',  estimatedHours: '',  weight: '',  prerequisiteIds: '',  description: '',  createDate: '',  updateDate: ''});

const rules = ref({
          examCategory: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          subjectName: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          chapter: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          pointName: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          difficulty: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          estimatedHours: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          weight: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          prerequisiteIds: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          description: [
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
  baseService.get("/student/knowledgepoint/" + id).then((res) => {
    Object.assign(dataForm, res.data);
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/student/knowledgepoint", dataForm).then((res) => {
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
