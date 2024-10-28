package nh.chat.utils;

import nh.chat.constant.ResultCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 结果集封装
 * @Author: xph
 * @Date: 2024-09-25 16:39
 */
@Data
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // 状态码
    private Integer code;
    // 信息描述
    private String msg;
    // 数据
    private T data;

    // 成功响应
    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.value());
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.value());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String msg, T data, Integer code) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }


    // 失败响应
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.value());
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(String msg, Integer code) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
