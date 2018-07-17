package com.nsq.helper;

import java.io.Serializable;

/**
 * @author zw
 * @param <T>
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 5925101851082556645L;

    private T data;
    private Integer state;
    private String errorCode;
    private String errorMsg;

    public static <T> Result<T> newError(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result.setData(data);
    }

    public Result() {
        this.state = Status.SUCCESS.code;
    }

    public Result(ErrorCode appErrorCode) {
        this(Status.ERROR, appErrorCode.getCode(), appErrorCode.getMessage());
    }

    public Result(String errorCode, String errorMsg) {
        this(Status.ERROR, errorCode, errorMsg);
    }

    public Result(Result.Status status, String errorCode, String errorMsg) {
        this.state = status.code();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return this.data;
    }

    public Result<T> setData(T content) {
        this.data = content;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Result<T> setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public Result<T> setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public enum Status {
        // 失败
        ERROR(0),
        // 成功
        SUCCESS(1);

        private Integer code;

        private Status(Integer code) {
            this.code = code;
        }

        public Integer code() {
            return this.code;
        }
    }
}