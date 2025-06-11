package cn.iocoder.yudao.module.drone.dal.mysql.cabinet;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.drone.dal.dataobject.cabinet.CabinetDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.*;

/**
 * 无人机柜 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CabinetMapper extends BaseMapperX<CabinetDO> {

    default PageResult<CabinetDO> selectPage(CabinetPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CabinetDO>()
                .likeIfPresent(CabinetDO::getName, reqVO.getName())
                .eqIfPresent(CabinetDO::getCode, reqVO.getCode())
                .eqIfPresent(CabinetDO::getIp, reqVO.getIp())
                .eqIfPresent(CabinetDO::getPort, reqVO.getPort())
                .eqIfPresent(CabinetDO::getSlaveId, reqVO.getSlaveId())
                .eqIfPresent(CabinetDO::getAddress, reqVO.getAddress())
                .eqIfPresent(CabinetDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(CabinetDO::getLatitude, reqVO.getLatitude())
                .eqIfPresent(CabinetDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CabinetDO::getTotalBoxes, reqVO.getTotalBoxes())
                .eqIfPresent(CabinetDO::getAvailableBoxes, reqVO.getAvailableBoxes())
                .betweenIfPresent(CabinetDO::getLastOnlineTime, reqVO.getLastOnlineTime())
                .betweenIfPresent(CabinetDO::getLastOfflineTime, reqVO.getLastOfflineTime())
                .betweenIfPresent(CabinetDO::getLastErrorTime, reqVO.getLastErrorTime())
                .eqIfPresent(CabinetDO::getErrorMessage, reqVO.getErrorMessage())
                .eqIfPresent(CabinetDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(CabinetDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CabinetDO::getId));
    }

}