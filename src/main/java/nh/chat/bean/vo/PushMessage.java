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

    private PushUserState pushUserStates; // 用户状态集

    // 消息推送
    public PushMessage(Long mid, Long receiveUid, int state, int type, String message) {
        this.mid = mid;
        this.receiveUid = receiveUid;
        this.state = state;
        this.type = type;
        this.message = message;
    }

    // 用户状态推送
    public PushMessage(PushUserState pushUserStates) {
        this.pushUserStates = pushUserStates;
    }
}
