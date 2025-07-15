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
     * @param id 编号
     */
    void deletedrones(String id);

    /**
    * 批量删除无人机
    *
    * @param ids 编号
    */
    void deletedronesListByIds(List<String> ids);

    /**
     * 获得无人机
     *
     * @param id 编号
     * @return 无人机
     */
    dronesDO getdrones(String id);

    /**
     * 获得无人机分页
     *
     * @param pageReqVO 分页查询
     * @return 无人机分页
     */
    PageResult<dronesDO> getdronesPage(dronesPageReqVO pageReqVO);

}