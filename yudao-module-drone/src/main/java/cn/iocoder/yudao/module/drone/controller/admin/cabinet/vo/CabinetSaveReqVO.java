package cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 无人机柜新增/修改 Request VO")
@Data
public class CabinetSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30098")
    private Long id;

    @Schema(description = "柜子名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "柜子名称不能为空")
    private String name;

    @Schema(description = "柜子编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "柜子编号不能为空")
    private String code;

    @Schema(description = "Modbus IP地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Modbus IP地址不能为空")
    private String ip;

    @Schema(description = "Modbus端口", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Modbus端口不能为空")
    private Integer port;

    @Schema(description = "Modbus从站ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24531")
    @NotNull(message = "Modbus从站ID不能为空")
    private Integer slaveId;

    @Schema(description = "实际地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "实际地址不能为空")
    private String address;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "状态（0-离线 1-在线 2-故障）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态（0-离线 1-在线 2-故障）不能为空")
    private Integer status;

    @Schema(description = "总格口数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总格口数不能为空")
    private Integer totalBoxes;

    @Schema(description = "可用格口数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "可用格口数不能为空")
    private Integer availableBoxes;

    @Schema(description = "最后在线时间")
    private LocalDateTime lastOnlineTime;

    @Schema(description = "最后离线时间")
    private LocalDateTime lastOfflineTime;

    @Schema(description = "最后故障时间")
    private LocalDateTime lastErrorTime;

    @Schema(description = "故障信息")
    private String errorMessage;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}