package cn.iocoder.yudao.module.drone.dal.dataobject.droneComm;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 无人机 DO
 *
 * @author 芋道源码
 */
@TableName("drone")
@KeySequence("drone_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class dronesDO extends BaseDO {

    /**
     * 无人机编号（主键）
     */
    @TableId(type = IdType.INPUT)
    private String droneCode;
    /**
     * 无人机名称
     */
    private String droneName;
    /**
     * 无人机型号
     */
    private String model;
    /**
     * 序列号
     */
    private String serialNumber;
    /**
     * 制造商
     */
    private String manufacturer;
    /**
     * 状态（0-待机 1-飞行中 2-返航中 3-充电中 4-维护中 5-故障 6-离线）
     */
    private Integer status;
    /**
     * 电池电量百分比（0-100）
     */
    private Integer batteryLevel;
    /**
     * 累计飞行时间（分钟）
     */
    private Integer flightTime;
    /**
     * 最大载重（kg）
     */
    private BigDecimal maxPayload;
    /**
     * 最大飞行时间（分钟）
     */
    private Integer maxFlightTime;
    /**
     * 当前经度
     */
    private BigDecimal longitude;
    /**
     * 当前纬度
     */
    private BigDecimal latitude;
    /**
     * 当前海拔高度（米）
     */
    private BigDecimal altitude;
    /**
     * 当前速度（m/s）
     */
    private BigDecimal speed;
    /**
     * 航向角度（0-360度）
     */
    private BigDecimal heading;
    /**
     * 起飞点经度
     */
    private BigDecimal homeLongitude;
    /**
     * 起飞点纬度
     */
    private BigDecimal homeLatitude;
    /**
     * 起飞点海拔高度（米）
     */
    private BigDecimal homeAltitude;
    /**
     * 最后飞行时间
     */
    private LocalDateTime lastFlightTime;
    /**
     * 最后降落时间
     */
    private LocalDateTime lastLandingTime;
    /**
     * 最后维护时间
     */
    private LocalDateTime lastMaintenanceTime;
    /**
     * 下次维护时间
     */
    private LocalDateTime nextMaintenanceTime;
    /**
     * 当前任务ID
     */
    private String currentMissionId;
    /**
     * 当前运送订单编号
     */
    private Long currentOrderId;
    /**
     * 当前运送订单流水号
     */
    private String currentOrderNo;
    /**
     * 起送柜子编号
     */
    private String sourceCabinetCode;
    /**
     * 目标柜子编号
     */
    private String targetCabinetCode;
    /**
     * 当前操作员
     */
    private String operator;
    /**
     * 故障信息
     */
    private String errorMessage;
    /**
     * 最后故障时间
     */
    private LocalDateTime lastErrorTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 部门编号
     */
    private Long deptId;


}