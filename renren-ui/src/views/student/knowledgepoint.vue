<template>
  <div class="mod-student__knowledgepoint">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item>
        <el-button v-if="state.hasPermission('student:knowledgepoint:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('student:knowledgepoint:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="id" label="知识点ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="examCategory" label="招考类别" header-align="center" align="center"></el-table-column>
              <el-table-column prop="subjectName" label="科目类别" header-align="center" align="center"></el-table-column>
              <el-table-column prop="chapter" label="章节" header-align="center" align="center"></el-table-column>
              <el-table-column prop="pointName" label="知识点名称" header-align="center" align="center"></el-table-column>
              <el-table-column prop="difficulty" label="难度等级" header-align="center" align="center"></el-table-column>
              <el-table-column prop="estimatedHours" label="预计学习时长/h" header-align="center" align="center"></el-table-column>
              <el-table-column prop="weight" label="考试权重" header-align="center" align="center"></el-table-column>
              <el-table-column prop="prerequisiteIds" label="前置知识点ID" header-align="center" align="center"></el-table-column>
              <el-table-column prop="description" label="知识点简述" header-align="center" align="center"></el-table-column>
              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center"></el-table-column>
              <el-table-column prop="updateDate" label="更新时间" header-align="center" align="center"></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">
          <el-button v-if="state.hasPermission('student:knowledgepoint:update')" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button v-if="state.hasPermission('student:knowledgepoint:delete')" type="primary" link @click="state.deleteHandle(scope.row.id)">删除</el-button>
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
import AddOrUpdate from "./knowledgepoint-add-or-update.vue";

const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/student/knowledgepoint/page",
  getDataListIsPage: true,
  exportURL: "/student/knowledgepoint/export",
  deleteURL: "/student/knowledgepoint"
});

const state = reactive({ ...useView(view), ...toRefs(view) });

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
