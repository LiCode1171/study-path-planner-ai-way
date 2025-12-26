<template>
  <div class="mod-student__studyplanitem">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item>
        <el-button v-if="state.hasPermission('student:studyplanitem:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('student:studyplanitem:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="id" label="计划项ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="planId" label="计划ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="knowledgePointId" label="知识点ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="dayIndex" label="第几天" header-align="center" align="center"></el-table-column>
              <el-table-column prop="sort" label="排序" header-align="center" align="center"></el-table-column>
              <el-table-column prop="completed" label="完成状态" header-align="center" align="center"></el-table-column>
              <el-table-column prop="completionDate" label="完成时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="updateDate" label="更新时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="splitPart" label="拆分的第几部分" header-align="center" align="center"></el-table-column>
              <el-table-column prop="totalParts" label="总部分数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="splitHours" label="本次分配的时长" header-align="center" align="center"></el-table-column>
              <el-table-column prop="subjectName" label="科目名称" header-align="center" align="center"></el-table-column>
              <el-table-column prop="isReview" label="是否复习项" header-align="center" align="center"></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">
          <el-button v-if="state.hasPermission('student:studyplanitem:update')" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="state.hasPermission('student:studyplanitem:delete')" type="primary" link @click="state.deleteHandle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination :current-page="state.page" :page-sizes="[10, 20, 50, 100]" :page-size="state.limit" :total="state.total" layout="total, sizes, prev, pager, next, jumper" @size-change="state.pageSizeChangeHandle" @current-change="state.pageCurrentChangeHandle"> </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update ref="addOrUpdateRef" @refreshDataList="state.getDataList">确定</add-or-update>
  </div>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, toRefs } from "vue";
import AddOrUpdate from "./studyplanitem-add-or-update.vue";

const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/student/studyplanitem/page",
  getDataListIsPage: true,
  exportURL: "/student/studyplanitem/export",
  deleteURL: "/student/studyplanitem"
});

const state = reactive({ ...useView(view), ...toRefs(view) });

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
