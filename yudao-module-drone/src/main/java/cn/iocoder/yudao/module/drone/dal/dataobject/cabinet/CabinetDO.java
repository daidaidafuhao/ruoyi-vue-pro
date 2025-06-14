package cn.iocoder.yudao.module.drone.dal.dataobject.cabinet;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 无人机柜 DO
 *
 * @author 芋道源码
 */
@TableName("drone_cabinet")
@KeySequence("drone_cabinet_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CabinetDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 柜子名称
     */
    private String name;
    /**
     * 柜子编号
     */
    private String code;
    /**
     * Modbus IP地址
     */
    private String ip;
    /**
     * Modbus端口
     */
    private Integer port;
    /**
     * Modbus从站ID
     */
    private Integer slaveId;
    /**
     * 实际地址
     */
    private String address;
    /**
     * 经度
     */
    private BigDecimal longitude;
    /**
     * 纬度
     */
    private BigDecimal latitude;
    /**
     * 状态（0-离线 1-在线 2-故障）
     */
    private Integer status;
    /**
     * 总格口数
     */
    private Integer totalBoxes;
    /**
     * 可用格口数
     */
    private Integer availableBoxes;
    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;
    /**
     * 最后离线时间
     */
    private LocalDateTime lastOfflineTime;
    /**
     * 最后故障时间
     */
    private LocalDateTime lastErrorTime;
    /**
     * 故障信息
     */
    private String errorMessage;
    /**
     * 备注
     */
    private String remark;

    /**
     * 部门编号
     */
    private Long deptId;

}