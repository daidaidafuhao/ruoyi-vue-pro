package cn.iocoder.yudao.module.drone.dal.mysql.droneComm;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.drone.dal.dataobject.droneComm.dronesDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.drone.controller.admin.droneComm.vo.*;

/**
 * 无人机 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface dronesMapper extends BaseMapperX<dronesDO> {

    default PageResult<dronesDO> selectPage(dronesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<dronesDO>()
                .likeIfPresent(dronesDO::getDroneName, reqVO.getDroneName())
                .eqIfPresent(dronesDO::getModel, reqVO.getModel())
                .eqIfPresent(dronesDO::getSerialNumber, reqVO.getSerialNumber())
                .eqIfPresent(dronesDO::getManufacturer, reqVO.getManufacturer())
                .eqIfPresent(dronesDO::getStatus, reqVO.getStatus())
                .eqIfPresent(dronesDO::getBatteryLevel, reqVO.getBatteryLevel())
                .betweenIfPresent(dronesDO::getFlightTime, reqVO.getFlightTime())
                .eqIfPresent(dronesDO::getMaxPayload, reqVO.getMaxPayload())
                .betweenIfPresent(dronesDO::getMaxFlightTime, reqVO.getMaxFlightTime())
                .eqIfPresent(dronesDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(dronesDO::getLatitude, reqVO.getLatitude())
                .eqIfPresent(dronesDO::getAltitude, reqVO.getAltitude())
                .eqIfPresent(dronesDO::getSpeed, reqVO.getSpeed())
                .eqIfPresent(dronesDO::getHeading, reqVO.getHeading())
                .eqIfPresent(dronesDO::getHomeLongitude, reqVO.getHomeLongitude())
                .eqIfPresent(dronesDO::getHomeLatitude, reqVO.getHomeLatitude())
                .eqIfPresent(dronesDO::getHomeAltitude, reqVO.getHomeAltitude())
                .betweenIfPresent(dronesDO::getLastFlightTime, reqVO.getLastFlightTime())
                .betweenIfPresent(dronesDO::getLastLandingTime, reqVO.getLastLandingTime())
                .betweenIfPresent(dronesDO::getLastMaintenanceTime, reqVO.getLastMaintenanceTime())
                .betweenIfPresent(dronesDO::getNextMaintenanceTime, reqVO.getNextMaintenanceTime())
                .eqIfPresent(dronesDO::getCurrentMissionId, reqVO.getCurrentMissionId())
                .eqIfPresent(dronesDO::getCurrentOrderId, reqVO.getCurrentOrderId())
                .eqIfPresent(dronesDO::getCurrentOrderNo, reqVO.getCurrentOrderNo())
                .eqIfPresent(dronesDO::getSourceCabinetCode, reqVO.getSourceCabinetCode())
                .eqIfPresent(dronesDO::getTargetCabinetCode, reqVO.getTargetCabinetCode())
                .eqIfPresent(dronesDO::getOperator, reqVO.getOperator())
                .eqIfPresent(dronesDO::getErrorMessage, reqVO.getErrorMessage())
                .betweenIfPresent(dronesDO::getLastErrorTime, reqVO.getLastErrorTime())
                .eqIfPresent(dronesDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(dronesDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(dronesDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(dronesDO::getDroneCode));
    }

    /**
     * 根据订单流水号查询无人机
     *
     * @param currentOrderNo 当前运送订单流水号
     * @return 无人机信息
     */
    default dronesDO selectByCurrentOrderNo(String currentOrderNo) {
        return selectOne(new LambdaQueryWrapperX<dronesDO>()
                .eq(dronesDO::getCurrentOrderNo, currentOrderNo));
    }

}