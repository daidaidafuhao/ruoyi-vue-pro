package cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 无人机柜分页 Request VO")
@Data
public class CabinetPageReqVO extends PageParam {

    @Schema(description = "柜子名称", example = "赵六")
    private String name;

    @Schema(description = "柜子编号")
    private String code;

    @Schema(description = "Modbus IP地址")
    private String ip;

    @Schema(description = "Modbus端口")
    private Integer port;

    @Schema(description = "Modbus从站ID", example = "24531")
    private Integer slaveId;

    @Schema(description = "实际地址")
    private String address;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "状态（0-离线 1-在线 2-故障）", example = "2")
    private Integer status;

    @Schema(description = "总格口数")
    private Integer totalBoxes;

    @Schema(description = "可用格口数")
    private Integer availableBoxes;

    @Schema(description = "最后在线时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastOnlineTime;

    @Schema(description = "最后离线时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastOfflineTime;

    @Schema(description = "最后故障时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastErrorTime;

    @Schema(description = "故障信息")
    private String errorMessage;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}