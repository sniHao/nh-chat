package nh.chat.exception;

import nh.chat.constant.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:自定义业务异常
 * @Author: xph
 * @Date: 2024-09-25 16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatException extends Exception {
    private String msg;
    private Integer code;

    public ChatException(String msg) {
        this.msg = msg;
        this.code = ResultCode.FAIL.value();
    }

    public ChatException(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }
}
