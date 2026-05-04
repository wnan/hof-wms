package com.hof.wms.common.result;

import com.hof.wms.common.constant.ApiConstants;

public record ApiResult<T>(Integer code, String message, T data) {

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ApiConstants.SUCCESS, "success", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(ApiConstants.SUCCESS, message, data);
    }

    public static <T> ApiResult<T> failure(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(1, message, null);
    }
}
