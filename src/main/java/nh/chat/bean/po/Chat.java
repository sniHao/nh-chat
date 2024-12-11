package nh.chat.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description:聊天内容
 * @Author: xph
 * @Date: 2024-09-25 15:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("c_chat")
public class Chat {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long sendUid; //发送用户id
    private Long receiveUid; //接收用户id
    private Long mid; //聊天内容id
    private int type; //消息类型
    private int sendState = 1; //发送用户消息状态
    private int receiveState = 1; //接收用户消息状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date; //发送时间
    private int action; //消息动作
    private Long quoteId; //引用消息id

    public Chat(Long id, Long sendUid, Long receiveUid, Long mid, int type, Date date, int action, Long quoteId) {
        this.id = id;
        this.sendUid = sendUid;
        this.receiveUid = receiveUid;
        this.mid = mid;
        this.type = type;
        this.date = date;
        this.action = action;
        this.quoteId = quoteId;
    }
}
