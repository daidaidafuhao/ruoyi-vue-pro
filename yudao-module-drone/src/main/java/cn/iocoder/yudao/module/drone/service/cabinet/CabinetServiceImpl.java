package cn.iocoder.yudao.module.drone.service.cabinet;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.cabinet.CabinetDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.drone.dal.mysql.cabinet.CabinetMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.drone.enums.ErrorCodeConstants.*;

/**
 * 无人机柜 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CabinetServiceImpl implements CabinetService {

    @Resource
    private CabinetMapper cabinetMapper;

    @Override
    public Long createCabinet(CabinetSaveReqVO createReqVO) {
        // 校验柜子编号是否重复
        if (cabinetMapper.selectCount(new LambdaQueryWrapperX<CabinetDO>()
                .eq(CabinetDO::getCode, createReqVO.getCode())) > 0) {
            throw exception(CABINET_CODE_DUPLICATE);
        }
        // 插入
        CabinetDO cabinet = BeanUtils.toBean(createReqVO, CabinetDO.class);
        cabinetMapper.insert(cabinet);
        // 返回
        return cabinet.getId();
    }

    @Override
    public void updateCabinet(CabinetSaveReqVO updateReqVO) {
        // 校验存在
        validateCabinetExists(updateReqVO.getId());
        // 更新
        CabinetDO updateObj = BeanUtils.toBean(updateReqVO, CabinetDO.class);
        cabinetMapper.updateById(updateObj);
    }

    @Override
    public void deleteCabinet(Long id) {
        // 校验存在
        validateCabinetExists(id);
        // 删除
        cabinetMapper.deleteById(id);
    }

    @Override
        public void deleteCabinetListByIds(List<Long> ids) {
        // 校验存在
        validateCabinetExists(ids);
        // 删除
        cabinetMapper.deleteByIds(ids);
        }

    private void validateCabinetExists(List<Long> ids) {
        List<CabinetDO> list = cabinetMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(CABINET_NOT_EXISTS);
        }
    }

    private void validateCabinetExists(Long id) {
        if (cabinetMapper.selectById(id) == null) {
            throw exception(CABINET_NOT_EXISTS);
        }
    }

    @Override
    public CabinetDO getCabinet(Long id) {
        return cabinetMapper.selectById(id);
    }

    @Override
    public PageResult<CabinetDO> getCabinetPage(CabinetPageReqVO pageReqVO) {
        return cabinetMapper.selectPage(pageReqVO);
    }

}