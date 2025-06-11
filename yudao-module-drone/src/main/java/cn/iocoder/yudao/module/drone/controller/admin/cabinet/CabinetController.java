package cn.iocoder.yudao.module.drone.controller.admin.cabinet;

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

import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.cabinet.CabinetDO;
import cn.iocoder.yudao.module.drone.service.cabinet.CabinetService;

@Tag(name = "管理后台 - 无人机柜")
@RestController
@RequestMapping("/drone/cabinet")
@Validated
public class CabinetController {

    @Resource
    private CabinetService cabinetService;

    @PostMapping("/create")
    @Operation(summary = "创建无人机柜")
    @PreAuthorize("@ss.hasPermission('drone:cabinet:create')")
    public CommonResult<Long> createCabinet(@Valid @RequestBody CabinetSaveReqVO createReqVO) {
        return success(cabinetService.createCabinet(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新无人机柜")
    @PreAuthorize("@ss.hasPermission('drone:cabinet:update')")
    public CommonResult<Boolean> updateCabinet(@Valid @RequestBody CabinetSaveReqVO updateReqVO) {
        cabinetService.updateCabinet(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除无人机柜")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('drone:cabinet:delete')")
    public CommonResult<Boolean> deleteCabinet(@RequestParam("id") Long id) {
        cabinetService.deleteCabinet(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除无人机柜")
                @PreAuthorize("@ss.hasPermission('drone:cabinet:delete')")
    public CommonResult<Boolean> deleteCabinetList(@RequestParam("ids") List<Long> ids) {
        cabinetService.deleteCabinetListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得无人机柜")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('drone:cabinet:query')")
    public CommonResult<CabinetRespVO> getCabinet(@RequestParam("id") Long id) {
        CabinetDO cabinet = cabinetService.getCabinet(id);
        return success(BeanUtils.toBean(cabinet, CabinetRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得无人机柜分页")
    @PreAuthorize("@ss.hasPermission('drone:cabinet:query')")
    public CommonResult<PageResult<CabinetRespVO>> getCabinetPage(@Valid CabinetPageReqVO pageReqVO) {
        PageResult<CabinetDO> pageResult = cabinetService.getCabinetPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CabinetRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出无人机柜 Excel")
    @PreAuthorize("@ss.hasPermission('drone:cabinet:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCabinetExcel(@Valid CabinetPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CabinetDO> list = cabinetService.getCabinetPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "无人机柜.xls", "数据", CabinetRespVO.class,
                        BeanUtils.toBean(list, CabinetRespVO.class));
    }

}