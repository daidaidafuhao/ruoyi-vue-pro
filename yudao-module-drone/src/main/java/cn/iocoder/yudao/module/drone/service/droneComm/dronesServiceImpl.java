package cn.iocoder.yudao.module.drone.service.droneComm;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.drone.controller.admin.droneComm.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.droneComm.dronesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.drone.dal.mysql.droneComm.dronesMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.drone.enums.ErrorCodeConstants.*;

/**
 * 无人机 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class dronesServiceImpl implements dronesService {

    @Resource
    private dronesMapper dronesMapper;

    @Override
    public String createdrones(dronesSaveReqVO createReqVO) {
        // 插入
        dronesDO drones = BeanUtils.toBean(createReqVO, dronesDO.class);
        dronesMapper.insert(drones);
        // 返回
        return drones.getDroneCode();
    }

    @Override
    public void updatedrones(dronesSaveReqVO updateReqVO) {
        // 校验存在
        validatedronesExists(updateReqVO.getDroneCode());
        // 更新
        dronesDO updateObj = BeanUtils.toBean(updateReqVO, dronesDO.class);
        dronesMapper.updateById(updateObj);
    }

    @Override
    public void deletedrones(String droneCode) {
        // 校验存在
        validatedronesExists(droneCode);
        // 删除
        dronesMapper.deleteById(droneCode);
    }

    @Override
        public void deletedronesListByIds(List<String> droneCodes) {
        // 校验存在
        validatedronesExists(droneCodes);
        // 删除
        dronesMapper.deleteByIds(droneCodes);
        }

    private void validatedronesExists(List<String> droneCodes) {
        List<dronesDO> list = dronesMapper.selectByIds(droneCodes);
        if (CollUtil.isEmpty(list) || list.size() != droneCodes.size()) {
            throw exception(DRONES_NOT_EXISTS);
        }
    }

    private void validatedronesExists(String droneCode) {
        if (dronesMapper.selectById(droneCode) == null) {
            throw exception(DRONES_NOT_EXISTS);
        }
    }

    @Override
    public dronesDO getdrones(String droneCode) {
        return dronesMapper.selectById(droneCode);
    }

    @Override
    public PageResult<dronesDO> getdronesPage(dronesPageReqVO pageReqVO) {
        return dronesMapper.selectPage(pageReqVO);
    }

}