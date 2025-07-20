package cn.iocoder.yudao.module.drone.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 无人机信息 Response VO")
@Data
public class AppDroneRespVO {

    @Schema(description = "无人机编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "DRONE001")
    private String droneCode;

    @Schema(description = "无人机名称", example = "配送无人机001")
    private String droneName;

    @Schema(description = "无人机型号", example = "DJI-M300")
    private String model;

    @Schema(description = "制造商", example = "大疆")
    private String manufacturer;

    @Schema(description = "状态（0-待机 1-飞行中 2-返航中 3-充电中 4-维护中 5-故障 6-离线）", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "飞行中")
    private String statusDesc;

    @Schema(description = "电池电量百分比（0-100）", example = "85")
    private Integer batteryLevel;

    @Schema(description = "当前经度", example = "116.397128")
    private BigDecimal longitude;

    @Schema(description = "当前纬度", example = "39.916527")
    private BigDecimal latitude;

    @Schema(description = "当前海拔高度（米）", example = "100.5")
    private BigDecimal altitude;

    @Schema(description = "当前速度（m/s）", example = "15.2")
    private BigDecimal speed;

    @Schema(description = "当前任务ID", example = "MISSION_20231201_001")
    private String currentMissionId;

    @Schema(description = "当前运送订单编号", example = "12345")
    private Long currentOrderId;

    @Schema(description = "当前运送订单流水号", example = "ORDER_20231201_001")
    private String currentOrderNo;

    @Schema(description = "起送柜子编号", example = "CABINET_001")
    private String sourceCabinetCode;

    @Schema(description = "目标柜子编号", example = "CABINET_002")
    private String targetCabinetCode;

    @Schema(description = "预计到达时间")
    private LocalDateTime estimatedArrivalTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;

} 