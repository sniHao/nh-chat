package nh.chat.exception;

import nh.chat.constant.ResultCode;
import nh.chat.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @Description:统一处理异常
 * @Author: xph
 * @Date: 2024-09-25 16:36
 */
@ControllerAdvice
@Component("NhControllerException")
public class ControllerException {
    private static final Logger logger = LoggerFactory.getLogger(ControllerException.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        if (e.toString().startsWith("ChatException(")) {
            ArrayList<Object> list = upError(e.toString().replace("ChatException(", "").replace(")", ""));
            return Result.error(list.get(0).toString(), Integer.parseInt(list.get(1).toString()));
        }
        logger.error("系统异常:" + e);
        return Result.error("系统异常");
    }

    @ResponseBody
    @ExceptionHandler(value = ChatException.class)
    public Result<?> jobExceptionHandler(ChatException e) {
        if (!Objects.equals(e.getCode(), ResultCode.NO_LOGIN.value()))
            logger.error("业务异常:{} , {}", e.getCode(), e.getMsg());
        if (Objects.isNull(e.getCode())) return Result.error(e.getMsg());
        return Result.error(e.getMsg(), e.getCode());
    }

    /**
     * 异常回抛
     *
     * @param str 异常内容
     * @return ArrayList
     */
    public ArrayList<Object> upError(String str) {
        String[] parts = str.split(",");
        ArrayList<Object> list = new ArrayList<>();
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].trim().equals("msg")) {
                    list.add(keyValue[1].trim());
                } else if (keyValue[0].trim().equals("code")) {
                    list.add(keyValue[1].trim());
                }
            }
        }
        return list;
    }
}