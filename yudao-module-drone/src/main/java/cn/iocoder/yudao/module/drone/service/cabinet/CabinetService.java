package cn.iocoder.yudao.module.drone.service.cabinet;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.cabinet.CabinetDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 无人机柜 Service 接口
 *
 * @author 芋道源码
 */
public interface CabinetService {

    /**
     * 创建无人机柜
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCabinet(@Valid CabinetSaveReqVO createReqVO);

    /**
     * 更新无人机柜
     *
     * @param updateReqVO 更新信息
     */
    void updateCabinet(@Valid CabinetSaveReqVO updateReqVO);

    /**
     * 删除无人机柜
     *
     * @param id 编号
     */
    void deleteCabinet(Long id);

    /**
    * 批量删除无人机柜
    *
    * @param ids 编号
    */
    void deleteCabinetListByIds(List<Long> ids);

    /**
     * 获得无人机柜
     *
     * @param id 编号
     * @return 无人机柜
     */
    CabinetDO getCabinet(Long id);

    /**
     * 获得无人机柜分页
     *
     * @param pageReqVO 分页查询
     * @return 无人机柜分页
     */
    PageResult<CabinetDO> getCabinetPage(CabinetPageReqVO pageReqVO);

    /**
     * 获得最近的无人机柜列表
     *
     * @param reqVO 查询条件
     * @return 最近的无人机柜列表
     */
    List<CabinetNearestRespVO> getNearestCabinets(CabinetNearestReqVO reqVO);

}