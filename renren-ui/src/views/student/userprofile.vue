<template>
  <div class="mod-student__userprofile">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item>
        <el-button v-if="state.hasPermission('student:userprofile:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('student:userprofile:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="id" label="唯一标识ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="userId" label="用户ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="examCategory" label="招考类别" header-align="center" align="center"></el-table-column>
              <el-table-column prop="targetSchool" label="目标院校" header-align="center" align="center"></el-table-column>
              <el-table-column prop="currentMajor" label="当前专业" header-align="center" align="center"></el-table-column>
              <el-table-column prop="targetMajor" label="目标专业" header-align="center" align="center"></el-table-column>
              <el-table-column prop="preparationMonths" label="备考月数" header-align="center" align="center"></el-table-column>
              <el-table-column prop="dailyStudyTime" label="每日学习时间/h" header-align="center" align="center"></el-table-column>
              <el-table-column prop="publicCourseType" label="公共基础课类型" header-align="center" align="center"></el-table-column>
              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="updateDate" label="更新时间" header-align="center" align="center"></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">
          <el-button v-if="state.hasPermission('student:userprofile:update')" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="state.hasPermission('student:userprofile:delete')" type="primary" link @click="state.deleteHandle(scope.row.id)">删除</el-button>
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
import AddOrUpdate from "./userprofile-add-or-update.vue";

const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/student/userprofile/page",
  getDataListIsPage: true,
  exportURL: "/student/userprofile/export",
  deleteURL: "/student/userprofile"
});

const state = reactive({ ...useView(view), ...toRefs(view) });

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
