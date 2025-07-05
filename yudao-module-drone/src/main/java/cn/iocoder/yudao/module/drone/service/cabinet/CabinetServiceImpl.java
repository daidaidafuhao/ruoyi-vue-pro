package cn.iocoder.yudao.module.drone.service.cabinet;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.drone.controller.admin.cabinet.vo.*;
import cn.iocoder.yudao.module.drone.dal.dataobject.cabinet.CabinetDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

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

    @Override
    public List<CabinetNearestRespVO> getNearestCabinets(CabinetNearestReqVO reqVO) {
        // 1. 获取所有未删除的柜子
        List<CabinetDO> cabinets = cabinetMapper.selectList();
        
        // 2. 计算每个柜子到目标点的距离
        List<CabinetNearestRespVO> result = new ArrayList<>();
        for (CabinetDO cabinet : cabinets) {
            if (cabinet.getLatitude() == null || cabinet.getLongitude() == null) {
                continue;
            }
            
            // 计算距离（使用Haversine公式）
            double distance = calculateDistance(
                reqVO.getLatitude().doubleValue(), reqVO.getLongitude().doubleValue(),
                cabinet.getLatitude().doubleValue(), cabinet.getLongitude().doubleValue()
            );
            
            // 转换为响应对象
            CabinetNearestRespVO respVO = BeanUtils.toBean(cabinet, CabinetNearestRespVO.class);
            respVO.setDistance(distance);
            result.add(respVO);
        }
        
        // 3. 按距离排序并限制返回数量
        result.sort(Comparator.comparing(CabinetNearestRespVO::getDistance));
        return result.stream()
                .limit(reqVO.getLimit())
                .collect(Collectors.toList());
    }

    /**
     * 使用Haversine公式计算两点之间的距离（单位：米）
     *
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 两点之间的距离（米）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 地球半径（米）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

}