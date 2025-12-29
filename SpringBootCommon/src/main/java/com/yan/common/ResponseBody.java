package com.yan.common;

/**
 * 响应体
 *
 * @param <T> 数据类型
 */
public class ResponseBody<T> {

    private boolean success;

    private T data;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <T> ResponseBody<T> success(T t) {
        ResponseBody<T> responseBody = new ResponseBody<>();
        responseBody.setSuccess(Boolean.TRUE);
        responseBody.setData(t);
        responseBody.setMessage("success");
        return responseBody;
    }

    public static <T> ResponseBody<T> fail(String message) {
        ResponseBody<T> responseBody = new ResponseBody<>();
        responseBody.setSuccess(Boolean.FALSE);
        responseBody.setMessage(message);
        return responseBody;
    }
}
