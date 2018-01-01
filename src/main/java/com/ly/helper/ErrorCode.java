package com.ly.helper;

/**
 * ErrorCode
 * <p>
 * Created by weizhong on 2017/6/16.
 */
public enum ErrorCode {

    /**
     * 会话已过期，请重新登录
     */
    SESSION_EXPIRE("99000101", "会话已过期，请重新登录"),

    SESSION_ERROR("99000102", "会话错误，请重新登录");

    private String code;

    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取错误信息
     *
     * @param code
     * @return
     */
    public static ErrorCode getByCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (code.equals(errorCode.getCode())) {
                return errorCode;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
