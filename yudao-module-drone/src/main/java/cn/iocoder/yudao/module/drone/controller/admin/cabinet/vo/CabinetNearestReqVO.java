package cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 最近柜子查询 Request VO")
@Data
public class CabinetNearestReqVO {

    @Schema(description = "纬度", required = true, example = "30.123456")
    @NotNull(message = "纬度不能为空")
    private Double latitude;

    @Schema(description = "经度", required = true, example = "120.123456")
    @NotNull(message = "经度不能为空")
    private Double longitude;

    @Schema(description = "返回数量", example = "10")
    private Integer limit = 10;

} 