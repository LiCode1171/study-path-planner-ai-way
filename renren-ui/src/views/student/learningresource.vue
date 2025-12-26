<template>
  <div class="mod-student__learningresource">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item>
        <el-button v-if="state.hasPermission('student:learningresource:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('student:learningresource:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="id" label="资源ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="title" label="资源标题" header-align="center" align="center"></el-table-column>
              <el-table-column prop="url" label="资源链接" header-align="center" align="center"></el-table-column>
              <el-table-column prop="resourceType" label="类型" header-align="center" align="center"></el-table-column>
              <el-table-column prop="examCategory" label="适用招考类别" header-align="center" align="center"></el-table-column>
              <el-table-column prop="subjectName" label="适用科目" header-align="center" align="center"></el-table-column>
              <el-table-column prop="knowledgePointIds" label="关联知识点" header-align="center" align="center"></el-table-column>
              <el-table-column prop="difficulty" label="难度等级" header-align="center" align="center"></el-table-column>
              <el-table-column prop="sourcePlatform" label="来源平台" header-align="center" align="center"></el-table-column>
              <el-table-column prop="description" label="资源描述" header-align="center" align="center"></el-table-column>
              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="updateDate" label="更新时间" header-align="center" align="center"></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">
          <el-button v-if="state.hasPermission('student:learningresource:update')" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="state.hasPermission('student:learningresource:delete')" type="primary" link @click="state.deleteHandle(scope.row.id)">删除</el-button>
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
import AddOrUpdate from "./learningresource-add-or-update.vue";

const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/student/learningresource/page",
  getDataListIsPage: true,
  exportURL: "/student/learningresource/export",
  deleteURL: "/student/learningresource"
});

const state = reactive({ ...useView(view), ...toRefs(view) });

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
