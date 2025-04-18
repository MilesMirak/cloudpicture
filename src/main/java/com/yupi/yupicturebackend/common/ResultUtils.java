package com.yupi.yupicturebackend.common;

import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;

/**
 * 响应工具类
 */
public class ResultUtils {
    /**
     * 成功
     * @param data 返回数据
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }
    /**
     * 失败
     * @param errorCode 错误码
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
    /**
     * 失败
     * @param code 错误码
     * @param message 错误信息
     */
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
    /**
     * 失败
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

}
