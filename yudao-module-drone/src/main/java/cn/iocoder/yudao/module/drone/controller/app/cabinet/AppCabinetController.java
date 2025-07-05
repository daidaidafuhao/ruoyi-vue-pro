package cn.iocoder.yudao.module.drone.controller.app.cabinet;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.CabinetNearestReqVO;
import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.CabinetNearestRespVO;
import cn.iocoder.yudao.module.drone.service.cabinet.CabinetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "客户端 - 无人机柜")
@RestController
@RequestMapping("/drone/cabinet")
@Validated
public class AppCabinetController {

    @Resource
    private CabinetService cabinetService;

    @GetMapping("/nearest")
    @Operation(summary = "获得最近的无人机柜")
    public CommonResult<List<CabinetNearestRespVO>> getNearestCabinets(@Validated CabinetNearestReqVO reqVO) {
        List<CabinetNearestRespVO> list = cabinetService.getNearestCabinets(reqVO);
        return success(list);
    }

} 