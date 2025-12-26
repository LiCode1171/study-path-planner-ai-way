<template>
  <el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" @keyup.enter="dataFormSubmitHandle()" label-width="120px">
          <el-form-item label="计划ID" prop="planId">
        <el-input v-model="dataForm.planId" placeholder="学习计划ID"></el-input>
      </el-form-item>
          <el-form-item label="知识点ID" prop="knowledgePointId">
        <el-input v-model="dataForm.knowledgePointId" placeholder="知识点ID"></el-input>
      </el-form-item>
          <el-form-item label="第几天" prop="dayIndex">
        <el-input v-model="dataForm.dayIndex" placeholder="第几天"></el-input>
      </el-form-item>
          <el-form-item label="排序" prop="sort">
        <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
      </el-form-item>
          <el-form-item label="完成状态" prop="completed">
        <el-input v-model="dataForm.completed" placeholder="完成状态：0-未完成 1-已完成 2-补交"></el-input>
      </el-form-item>
          <el-form-item label="完成时间" prop="completionDate">
        <el-input v-model="dataForm.completionDate" placeholder="完成时间"></el-input>
      </el-form-item>
              <el-form-item label="科目名称" prop="subjectName">
        <el-input v-model="dataForm.subjectName" placeholder="科目名称"></el-input>
      </el-form-item>
          <el-form-item label="是否复习项" prop="isReview">
        <el-input v-model="dataForm.isReview" placeholder="是否复习项 0-否 1-是"></el-input>
      </el-form-item>
          <el-form-item label="拆分的第几部分" prop="splitPart">
        <el-input v-model="dataForm.splitPart" placeholder="拆分的第几部分，用于跨天学习的知识点"></el-input>
      </el-form-item>
          <el-form-item label="总部分数" prop="totalParts">
        <el-input v-model="dataForm.totalParts" placeholder="总部分数，用于跨天学习的知识点"></el-input>
      </el-form-item>
          <el-form-item label="本次分配的时长" prop="splitHours">
        <el-input v-model="dataForm.splitHours" placeholder="本次分配的时长，用于跨天学习的知识点"></el-input>
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
  id: '',  planId: '',  knowledgePointId: '',  dayIndex: '',  sort: '',  completed: '',  completionDate: '',  createDate: '',  updateDate: '',  subjectName: '',  isReview: '',  splitPart: '',  totalParts: '',  splitHours: ''});

const rules = ref({
          planId: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          knowledgePointId: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          dayIndex: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          sort: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          completed: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          completionDate: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
              subjectName: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          isReview: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          splitPart: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          totalParts: [
      { required: true, message: '必填项不能为空', trigger: 'blur' }
    ],
          splitHours: [
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
  baseService.get("/student/studyplanitem/" + id).then((res) => {
    Object.assign(dataForm, res.data);
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/student/studyplanitem", dataForm).then((res) => {
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
