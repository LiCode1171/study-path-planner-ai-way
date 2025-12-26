package io.renren.modules.student.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.ExcelUtils;
import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.renren.modules.student.dto.StudyPlanItemDTO;
import io.renren.modules.student.excel.StudyPlanItemExcel;
import io.renren.modules.student.service.StudyPlanItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-28
 */
@RestController
@RequestMapping("student/studyplanitem")
@Tag(name="学习计划项表")
public class StudyPlanItemController {
    @Autowired
    private StudyPlanItemService studyPlanItemService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
        @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", in = ParameterIn.QUERY, required = true, ref="int") ,
        @Parameter(name = Constant.LIMIT, description = "每页显示记录数", in = ParameterIn.QUERY,required = true, ref="int") ,
        @Parameter(name = Constant.ORDER_FIELD, description = "排序字段", in = ParameterIn.QUERY, ref="String") ,
        @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)", in = ParameterIn.QUERY, ref="String")
    })
    @RequiresPermissions("student:studyplanitem:page")
    public Result<PageData<StudyPlanItemDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<StudyPlanItemDTO> page = studyPlanItemService.page(params);

        return new Result<PageData<StudyPlanItemDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @RequiresPermissions("student:studyplanitem:info")
    public Result<StudyPlanItemDTO> get(@PathVariable("id") Long id){
        StudyPlanItemDTO data = studyPlanItemService.get(id);

        return new Result<StudyPlanItemDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions("student:studyplanitem:save")
    public Result save(@RequestBody StudyPlanItemDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        studyPlanItemService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions("student:studyplanitem:update")
    public Result update(@RequestBody StudyPlanItemDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        studyPlanItemService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions("student:studyplanitem:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        studyPlanItemService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @RequiresPermissions("student:studyplanitem:export")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<StudyPlanItemDTO> list = studyPlanItemService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "学习计划项表", list, StudyPlanItemExcel.class);
    }

}
