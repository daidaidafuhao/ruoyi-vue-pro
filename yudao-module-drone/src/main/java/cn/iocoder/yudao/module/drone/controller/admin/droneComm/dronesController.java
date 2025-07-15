package cn.iocoder.yudao.module.drone.controller.admin.droneComm;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.drone.controller.admin.droneComm.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.droneComm.dronesDO;
import cn.iocoder.yudao.module.drone.service.droneComm.dronesService;

@Tag(name = "管理后台 - 无人机")
@RestController
@RequestMapping("/drone/drones")
@Validated
public class dronesController {

    @Resource
    private dronesService dronesService;

    @PostMapping("/create")
    @Operation(summary = "创建无人机")
    @PreAuthorize("@ss.hasPermission('drone:drones:create')")
    public CommonResult<String> createdrones(@Valid @RequestBody dronesSaveReqVO createReqVO) {
        return success(dronesService.createdrones(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新无人机")
    @PreAuthorize("@ss.hasPermission('drone:drones:update')")
    public CommonResult<Boolean> updatedrones(@Valid @RequestBody dronesSaveReqVO updateReqVO) {
        dronesService.updatedrones(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除无人机")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('drone:drones:delete')")
    public CommonResult<Boolean> deletedrones(@RequestParam("id") String id) {
        dronesService.deletedrones(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除无人机")
                @PreAuthorize("@ss.hasPermission('drone:drones:delete')")
    public CommonResult<Boolean> deletedronesList(@RequestParam("ids") List<String> ids) {
        dronesService.deletedronesListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得无人机")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('drone:drones:query')")
    public CommonResult<dronesRespVO> getdrones(@RequestParam("id") String id) {
        dronesDO drones = dronesService.getdrones(id);
        return success(BeanUtils.toBean(drones, dronesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得无人机分页")
    @PreAuthorize("@ss.hasPermission('drone:drones:query')")
    public CommonResult<PageResult<dronesRespVO>> getdronesPage(@Valid dronesPageReqVO pageReqVO) {
        PageResult<dronesDO> pageResult = dronesService.getdronesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, dronesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出无人机 Excel")
    @PreAuthorize("@ss.hasPermission('drone:drones:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportdronesExcel(@Valid dronesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<dronesDO> list = dronesService.getdronesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "无人机.xls", "数据", dronesRespVO.class,
                        BeanUtils.toBean(list, dronesRespVO.class));
    }

}