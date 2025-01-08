package nh.chat.core;

import lombok.RequiredArgsConstructor;
import nh.chat.utils.ChatSocket;
import org.springframework.stereotype.Component;

/**
 * @Description: ws核心工具类
 * @Author: xph
 * @Date: 2025-01-06 11:21
 */
@Component
@RequiredArgsConstructor
public class WsTool {
    private final ChatSocket chatSocket;

    /**
     * 判断用户是否在线
     *
     * @param uid 用户id
     * @return 是否在线
     */
    public Boolean ofUserWsState(Long uid) {
        return chatSocket.wsState(uid);
    }

}
