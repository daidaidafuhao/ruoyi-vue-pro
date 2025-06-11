package cn.iocoder.yudao.module.drone.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.drone.common.util.JLibModbusUtils;
import cn.iocoder.yudao.module.drone.controller.admin.vo.ModbusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 接驳柜管理")
@RestController
@RequestMapping("/drone")
@Validated
public class AppDroneController {

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
} 