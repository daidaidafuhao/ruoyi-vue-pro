package cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 最近柜子查询 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CabinetNearestRespVO extends CabinetRespVO {

    @Schema(description = "距离（米）", example = "1000")
    private Double distance;

} 