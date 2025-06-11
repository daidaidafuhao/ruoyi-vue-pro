package cn.iocoder.yudao.module.drone.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 无人机模块错误码枚举类
 * <p>
 * drone 系统，使用 1-060-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 无人机柜 1-060-001-000 ==========
    ErrorCode CABINET_NOT_EXISTS = new ErrorCode(1_060_001_000, "无人机柜不存在");
    ErrorCode CABINET_CODE_DUPLICATE = new ErrorCode(1_060_001_001, "柜子编号已存在");
} 