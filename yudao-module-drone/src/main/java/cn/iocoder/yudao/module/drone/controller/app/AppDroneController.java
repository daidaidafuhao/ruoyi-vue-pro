package cn.iocoder.yudao.module.drone.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.drone.common.util.JLibModbusUtils;
import cn.iocoder.yudao.module.drone.controller.admin.vo.ModbusRequest;
import cn.iocoder.yudao.module.drone.controller.app.vo.AppDroneRespVO;
import cn.iocoder.yudao.module.drone.dal.dataobject.droneComm.dronesDO;
import cn.iocoder.yudao.module.drone.service.droneComm.dronesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "客户端 - 接驳柜操作")
@RestController
@RequestMapping("/app/drone")
@Validated
public class AppDroneController {

    @Resource
    private dronesService dronesService;

    @PostMapping("/package/can-store")
    @Operation(summary = "检查是否可以存件")
    public CommonResult<Boolean> canStorePackage(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.canStorePackage());
    }

    @PostMapping("/package/user-pickup-by-box")
    @Operation(summary = "用户取件（格口号方式）")
    public CommonResult<Boolean> userPickupByBox(
            @Validated @RequestBody ModbusRequest request,
            @RequestParam("boxNo") @Parameter(description = "格口号") Integer boxNo) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.userPickupByBox(boxNo) == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/package/user-pickup-by-code")
    @Operation(summary = "用户取件（取件码方式）")
    public CommonResult<Boolean> userPickupByCode(
            @Validated @RequestBody ModbusRequest request,
            @RequestParam("code") @Parameter(description = "取件码") Integer code) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.userPickupByCode(code) == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/package/user-pickup-empty")
    @Operation(summary = "用户取空包裹")
    public CommonResult<Boolean> userPickupEmptyBox(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.userPickupEmptyBox() == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/package/can-deposit")
    @Operation(summary = "检查是否可以寄件")
    public CommonResult<Boolean> canDeposit(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.canDeposit());
    }

    @GetMapping("/get-by-order")
    @Operation(summary = "根据订单号查询无人机信息")
    public CommonResult<AppDroneRespVO> getDroneByOrderNo(
            @RequestParam("orderNo") @Parameter(description = "订单流水号", required = true) String orderNo) {
        // 根据订单号查询无人机
        dronesDO drone = dronesService.getdronesByOrderNo(orderNo);
        if (drone == null) {
            return success(null);
        }
        
        // 转换为响应VO并添加状态描述
        AppDroneRespVO respVO = BeanUtils.toBean(drone, AppDroneRespVO.class);
        respVO.setStatusDesc(getStatusDesc(drone.getStatus()));
        respVO.setLastUpdateTime(drone.getUpdateTime());
        return success(respVO);
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0: return "待机";
            case 1: return "飞行中";
            case 2: return "返航中";
            case 3: return "充电中";
            case 4: return "维护中";
            case 5: return "故障";
            case 6: return "离线";
            default: return "未知状态";
        }
    }
} 