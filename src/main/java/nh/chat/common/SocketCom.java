package nh.chat.common;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import nh.chat.bean.vo.PushMessage;
import nh.chat.bean.vo.PushUserState;
import nh.chat.utils.ChatSocket;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:消息公共
 * @Author: xph
 * @Date: 2024-12-25 10:01
 */
@Component
@RequiredArgsConstructor
public class SocketCom {

    private final ChatSocket chatSocket;


    /**
     * 推送消息公共方法
     *
     * @param cid        消息id
     * @param uid        用户id
     * @param receiveUid 接收用户id
     * @param state      消息状态
     * @param message    消息内容
     * @param type       类型
     */
    public void pushCom(Long cid, Long uid, Long receiveUid, int state, String message, int type) {
        PushMessage pushMessage = new PushMessage(cid, uid, state, type, message);
        try {
            chatSocket.sendMessage(receiveUid, JSONObject.toJSONString(pushMessage));
        } catch (Exception ignored) {
        }
    }

    /**
     * 推送用户状态公共方法
     *
     * @param receiveUid 推送用户
     * @param uid        用户ID
     * @param state      状态
     */
    public void pushUserState(Long receiveUid, Long uid, int state) {
        PushMessage pushMessage = new PushMessage(new PushUserState(uid, state));
        try {
            chatSocket.sendMessage(receiveUid, JSONObject.toJSONString(pushMessage));
        } catch (Exception ignored) {
        }
    }
}
