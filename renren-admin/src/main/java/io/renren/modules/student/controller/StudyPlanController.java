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
import io.renren.modules.student.dto.StudyPlanDTO;
import io.renren.modules.student.entity.StudyPlanEntity;
import io.renren.modules.student.entity.StudyPlanItemEntity;
import io.renren.modules.student.excel.StudyPlanExcel;
import io.renren.modules.student.service.StudyPlanItemService;
import io.renren.modules.student.service.StudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 学习计划表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-23
 */
@RestController
@RequestMapping("student/studyplan")
@Tag(name="学习计划表")
public class StudyPlanController {
    private static final Logger logger = LoggerFactory.getLogger(StudyPlanController.class);

    @Autowired
    private StudyPlanService studyPlanService;

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
    @RequiresPermissions("student:studyplan:page")
    public Result<PageData<StudyPlanDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<StudyPlanDTO> page = studyPlanService.page(params);

        return new Result<PageData<StudyPlanDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @RequiresPermissions("student:studyplan:info")
    public Result<StudyPlanDTO> get(@PathVariable("id") Long id){
        StudyPlanDTO data = studyPlanService.get(id);

        return new Result<StudyPlanDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions("student:studyplan:save")
    public Result save(@RequestBody StudyPlanDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        studyPlanService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions("student:studyplan:update")
    public Result update(@RequestBody StudyPlanDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        studyPlanService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions("student:studyplan:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        studyPlanService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @RequiresPermissions("student:studyplan:export")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<StudyPlanDTO> list = studyPlanService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "学习计划表", list, StudyPlanExcel.class);
    }

    /**
     * 获取用户最新学习计划
     */
    @GetMapping("/user/latest")
    @Operation(summary = "获取用户最新学习计划")
    public Result<StudyPlanEntity> getLatestByUserId(@RequestParam Long userId) {
        StudyPlanEntity plan = studyPlanService.getByUserId(userId);
        if (plan != null) {
            // 获取计划项数据
            List<StudyPlanItemEntity> planItems = studyPlanItemService.getByPlanId(plan.getId());
            plan.setPlanItems(planItems);
            return new Result<StudyPlanEntity>().ok(plan);
        }
        logger.info("用户没有学习计划");
        return new Result<StudyPlanEntity>().ok(null);
    }
}
