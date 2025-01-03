package nh.chat.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description:记录
 * @Author: xph
 * @Date: 2025-01-03 16:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordVo {
    private Long id;
    private Long sendUid; //发送用户id
    private Long receiveUid; //接收用户id
    private int type; //消息类型
    private int sendState; //发送用户消息状态
    private int receiveState; //接收用户消息状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date; //发送时间

    private String message; //内容
}
