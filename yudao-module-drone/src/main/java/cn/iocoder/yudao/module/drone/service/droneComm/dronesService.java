package cn.iocoder.yudao.module.drone.service.droneComm;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.drone.controller.admin.droneComm.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.droneComm.dronesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 无人机 Service 接口
 *
 * @author 芋道源码
 */
public interface dronesService {

    /**
     * 创建无人机
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    String createdrones(@Valid dronesSaveReqVO createReqVO);

    /**
     * 更新无人机
     *
     * @param updateReqVO 更新信息
     */
    void updatedrones(@Valid dronesSaveReqVO updateReqVO);

    /**
     * 删除无人机
     *
     * @param droneCode 无人机编号
     */
    void deletedrones(String droneCode);

    /**
    * 批量删除无人机
    *
    * @param droneCodes 无人机编号列表
    */
    void deletedronesListByIds(List<String> droneCodes);

    /**
     * 获得无人机
     *
     * @param droneCode 无人机编号
     * @return 无人机
     */
    dronesDO getdrones(String droneCode);

    /**
     * 获得无人机分页
     *
     * @param pageReqVO 分页查询
     * @return 无人机分页
     */
    PageResult<dronesDO> getdronesPage(dronesPageReqVO pageReqVO);

    /**
     * 根据订单流水号获得无人机
     *
     * @param currentOrderNo 当前运送订单流水号
     * @return 无人机
     */
    dronesDO getdronesByOrderNo(String currentOrderNo);

}