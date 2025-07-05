package cn.iocoder.yudao.module.drone.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.drone.common.util.JLibModbusUtils;
import cn.iocoder.yudao.module.drone.common.util.DroneControlUtil;
import cn.iocoder.yudao.module.drone.controller.admin.vo.ModbusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 接驳柜管理")
@RestController
@RequestMapping("/drone")
@Validated
public class DroneController {

    @PostMapping("/door/open")
    @Operation(summary = "开舱门")
    public CommonResult<Boolean> openDoor(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.openDoor(master, request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_DOOR);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/door/close")
    @Operation(summary = "关舱门")
    public CommonResult<Boolean> closeDoor(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.closeDoor(master, request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_DOOR);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/drone/confirm-arrived")
    @Operation(summary = "确认停机坪有飞机")
    public CommonResult<Boolean> confirmDroneArrived(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.confirmDroneOnPad(master, request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_PARKING);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/drone/confirm-left")
    @Operation(summary = "确认停机坪无飞机")
    public CommonResult<Boolean> confirmDroneLeft(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.setParkingEmpty(master, request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_PARKING);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/package/can-store")
    @Operation(summary = "检查是否可以存件")
    public CommonResult<Boolean> canStorePackage(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.checkStorageStatus(master, request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_STORAGE_STATUS);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/package/store")
    @Operation(summary = "无人机存件")
    public CommonResult<Boolean> storePackage(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.droneStorePackage() == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/servo/open")
    @Operation(summary = "打开舵机")
    public CommonResult<Boolean> openServo(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.setServoStatus(master, 10, 11, "开舵机", request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_SERVO);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/servo/close")
    @Operation(summary = "关闭舵机")
    public CommonResult<Boolean> closeServo(@Validated @RequestBody ModbusRequest request) throws Exception {
        DroneControlUtil droneControl = new DroneControlUtil();
        ModbusMaster master = droneControl.connectContainer(request.getIp(), request.getPort());
        try {
            droneControl.setServoStatus(master, 20, 21, "关舵机", request.getSlaveId(), DroneControlUtil.DEFAULT_MODBUS_ADDR_SERVO);
            return success(true);
        } catch (Exception e) {
            return success(false);
        } finally {
            droneControl.closeConnection(master);
        }
    }

    @PostMapping("/package/drone-pickup")
    @Operation(summary = "无人机取件")
    public CommonResult<Boolean> dronePickupPackage(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.dronePickupPackage() == JLibModbusUtils.ResultCode.SUCCESS);
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

    @PostMapping("/package/recycle-empty")
    @Operation(summary = "空包裹回收")
    public CommonResult<Boolean> recycleEmptyBox(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        JLibModbusUtils.ResultCode step1 = modbusUtils.remoteRecycleStep1();
        if (step1 != JLibModbusUtils.ResultCode.SUCCESS) {
            return success(false);
        }
        return success(modbusUtils.remoteRecycleStep2() == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/package/remote-recycle")
    @Operation(summary = "远程回收空包裹")
    public CommonResult<Boolean> remoteRecycleEmptyBox(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        JLibModbusUtils.ResultCode step1 = modbusUtils.remoteRecycleStep1();
        if (step1 != JLibModbusUtils.ResultCode.SUCCESS) {
            return success(false);
        }
        return success(modbusUtils.remoteRecycleStep2() == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/package/remote-deposit")
    @Operation(summary = "远程寄件")
    public CommonResult<Boolean> remoteDeposit(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.remoteDeposit() == JLibModbusUtils.ResultCode.SUCCESS);
    }

    @PostMapping("/package/can-deposit")
    @Operation(summary = "检查是否可以寄件")
    public CommonResult<Boolean> canDeposit(@Validated @RequestBody ModbusRequest request) throws Exception {
        JLibModbusUtils modbusUtils = new JLibModbusUtils(request.getIp(), request.getPort(), request.getSlaveId());
        return success(modbusUtils.canDeposit());
    }
} 