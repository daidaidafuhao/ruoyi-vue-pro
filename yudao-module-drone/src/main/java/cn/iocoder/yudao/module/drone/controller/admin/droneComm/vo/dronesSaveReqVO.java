package cn.iocoder.yudao.module.drone.controller.admin.droneComm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 无人机新增/修改 Request VO")
@Data
public class dronesSaveReqVO {

    @Schema(description = "无人机编号（主键）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String droneCode;

    @Schema(description = "无人机名称", example = "赵六")
    private String droneName;

    @Schema(description = "无人机型号")
    private String model;

    @Schema(description = "序列号")
    private String serialNumber;

    @Schema(description = "制造商")
    private String manufacturer;

    @Schema(description = "状态（0-待机 1-飞行中 2-返航中 3-充电中 4-维护中 5-故障 6-离线）", example = "1")
    private Integer status;

    @Schema(description = "电池电量百分比（0-100）")
    private Integer batteryLevel;

    @Schema(description = "累计飞行时间（分钟）")
    private Integer flightTime;

    @Schema(description = "最大载重（kg）")
    private BigDecimal maxPayload;

    @Schema(description = "最大飞行时间（分钟）")
    private Integer maxFlightTime;

    @Schema(description = "当前经度")
    private BigDecimal longitude;

    @Schema(description = "当前纬度")
    private BigDecimal latitude;

    @Schema(description = "当前海拔高度（米）")
    private BigDecimal altitude;

    @Schema(description = "当前速度（m/s）")
    private BigDecimal speed;

    @Schema(description = "航向角度（0-360度）")
    private BigDecimal heading;

    @Schema(description = "起飞点经度")
    private BigDecimal homeLongitude;

    @Schema(description = "起飞点纬度")
    private BigDecimal homeLatitude;

    @Schema(description = "起飞点海拔高度（米）")
    private BigDecimal homeAltitude;

    @Schema(description = "最后飞行时间")
    private LocalDateTime lastFlightTime;

    @Schema(description = "最后降落时间")
    private LocalDateTime lastLandingTime;

    @Schema(description = "最后维护时间")
    private LocalDateTime lastMaintenanceTime;

    @Schema(description = "下次维护时间")
    private LocalDateTime nextMaintenanceTime;

    @Schema(description = "当前任务ID", example = "19550")
    private String currentMissionId;

    @Schema(description = "当前运送订单编号", example = "15741")
    private Long currentOrderId;

    @Schema(description = "当前运送订单流水号")
    private String currentOrderNo;

    @Schema(description = "起送柜子编号")
    private String sourceCabinetCode;

    @Schema(description = "目标柜子编号")
    private String targetCabinetCode;

    @Schema(description = "当前操作员")
    private String operator;

    @Schema(description = "故障信息")
    private String errorMessage;

    @Schema(description = "最后故障时间")
    private LocalDateTime lastErrorTime;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "部门编号", example = "8112")
    private Long deptId;

}