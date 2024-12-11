package nh.chat.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description:消息Vo
 * @Author: xph
 * @Date: 2024-09-29 14:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVo {
    private Long id;
    private Long sendUid; //发送用户id
    private Long receiveUid; //接收用户id
    private int type; //消息类型
    private int sendState; //发送用户消息状态
    private int receiveState; //接收用户消息状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date; //发送时间

    private String message; //内容

    private int action; //消息动作
    private Long quoteId; //引用消息id
    private String quoteMessage; //引用消息内容

}
