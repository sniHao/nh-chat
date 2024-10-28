package nh.chat.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PushMessage {
    private Long mid; // 消息ID
    private Long receiveUid; // 接收用户ID
    private int state; // 消息状态(0:删除  1:存在   2:撤回)
    private int type; // 消息类型
    private String message; // 消息内容
}
