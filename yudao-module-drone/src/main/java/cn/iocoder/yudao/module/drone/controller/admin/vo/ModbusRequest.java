package cn.iocoder.yudao.module.drone.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "Modbus 请求参数")
@Data
public class ModbusRequest {

    @Schema(description = "Modbus IP地址", required = true, example = "192.168.1.100")
    @NotEmpty(message = "IP地址不能为空")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", message = "IP地址格式不正确")
    private String ip;

    @Schema(description = "Modbus 端口", required = true, example = "502")
    @NotNull(message = "端口不能为空")
    private Integer port;

    @Schema(description = "Modbus 从站ID", required = true, example = "1")
    @NotNull(message = "从站ID不能为空")
    private Integer slaveId;

    @Schema(description = "格口号", required = true, example = "1")
    @NotNull(message = "格口号不能为空")
    private Integer boxNo;
} 