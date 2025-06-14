package cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 无人机柜 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CabinetRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30098")
    @ExcelProperty("主键ID")
    private Long id;

    @Schema(description = "柜子名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("柜子名称")
    private String name;

    @Schema(description = "柜子编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("柜子编号")
    private String code;

    @Schema(description = "Modbus IP地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Modbus IP地址")
    private String ip;

    @Schema(description = "Modbus端口", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Modbus端口")
    private Integer port;

    @Schema(description = "Modbus从站ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24531")
    @ExcelProperty("Modbus从站ID")
    private Integer slaveId;

    @Schema(description = "实际地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("实际地址")
    private String address;

    @Schema(description = "经度")
    @ExcelProperty("经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    @ExcelProperty("纬度")
    private BigDecimal latitude;

    @Schema(description = "状态（0-离线 1-在线 2-故障）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态（0-离线 1-在线 2-故障）")
    private Integer status;

    @Schema(description = "部门编号")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "总格口数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("总格口数")
    private Integer totalBoxes;

    @Schema(description = "可用格口数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("可用格口数")
    private Integer availableBoxes;

    @Schema(description = "最后在线时间")
    @ExcelProperty("最后在线时间")
    private LocalDateTime lastOnlineTime;

    @Schema(description = "最后离线时间")
    @ExcelProperty("最后离线时间")
    private LocalDateTime lastOfflineTime;

    @Schema(description = "最后故障时间")
    @ExcelProperty("最后故障时间")
    private LocalDateTime lastErrorTime;

    @Schema(description = "故障信息")
    @ExcelProperty("故障信息")
    private String errorMessage;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}