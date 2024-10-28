package nh.chat.bean.dto;

import lombok.Data;

/**
 * @Description:发送消息参数
 * @Author: xph
 * @Date: 2024-09-29 15:37
 */
@Data
public class SendMessageDto {
    private Long receiveUid; //接收用户id
    private String message; //内容
    private int type; //消息类型
}
