<template>
  <div class="mod-student__studyplan">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item>
        <el-button v-if="state.hasPermission('student:studyplan:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('student:studyplan:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="id" label="计划ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="userId" label="用户ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="planName" label="计划名称" header-align="center" align="center"></el-table-column>
              <el-table-column prop="totalDays" label="总天数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="dailyHours" label="每日学习小时数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="startDate" label="开始日期" header-align="center" align="center"></el-table-column>
              <el-table-column prop="endDate" label="结束日期" header-align="center" align="center"></el-table-column>
              <el-table-column prop="status" label="完成状态" header-align="center" align="center"></el-table-column>
              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="updateDate" label="更新时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="totalSubjects" label="总科目数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="completedSubjects" label="已完成科目数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="totalReviewDays" label="总复习天数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="overallProgress" label="总体进度" header-align="center" align="center"></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">
          <el-button v-if="state.hasPermission('student:studyplan:update')" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="state.hasPermission('student:studyplan:delete')" type="primary" link @click="state.deleteHandle(scope.row.id)">删除</el-button>
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
import AddOrUpdate from "./studyplan-add-or-update.vue";

const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/student/studyplan/page",
  getDataListIsPage: true,
  exportURL: "/student/studyplan/export",
  deleteURL: "/student/studyplan"
});

const state = reactive({ ...useView(view), ...toRefs(view) });

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
