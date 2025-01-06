package nh.chat.utils;

import lombok.extern.slf4j.Slf4j;
import nh.chat.constant.ChatCode;
import nh.chat.service.SocketService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:私信消息推送
 * @Author: xph
 * @Date: 2024-09-25 17:16
 */
@Component
@Slf4j
public class ChatSocket extends TextWebSocketHandler {
    private static final ConcurrentHashMap<Long, WebSocketSession> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 判断用户是否在线
     *
     * @param uid 用户id
     * @return 是否在线
     */
    public Boolean wsState(Long uid) {
        return webSocketMap.containsKey(uid);
    }

    /**
     * 获取用户id
     *
     * @param session 会话
     * @return 用户id
     */
    public Long getUid(WebSocketSession session) {
        HttpHeaders headers = session.getHandshakeHeaders();
        List<String> author = headers.get("Authorization");
        if (Objects.isNull(author) || author.isEmpty()) return null;
        return Long.valueOf(author.get(0));
    }

    /**
     * 建立连接 @OnOpen
     *
     * @param session 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long uid = getUid(session);
        if (Objects.isNull(uid)) return;
        WebSocketSession state = webSocketMap.get(uid);
        if (!Objects.isNull(state)) webSocketMap.remove(uid);
        webSocketMap.put(uid, session);
        SocketService socketService = NhBeanUtil.getBean(SocketService.class);
        socketService.userWsState(uid, ChatCode.USER_STATE_ONLINE.value());
    }

    /**
     * 接收消息事件 @OnMessage
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {

    }

    /**
     * 发消息
     *
     * @param uid     用户id
     * @param message 消息体
     * @throws IOException e
     */
    public void sendMessage(Long uid, String message) throws IOException {
        if (webSocketMap.containsKey(uid))
            webSocketMap.get(uid).sendMessage(new TextMessage(message));
    }

    /**
     * socket 断开连接时 @OnClose
     *
     * @param session 会话
     * @param status  状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long uid = getUid(session);
        if (Objects.isNull(uid)) return;
        webSocketMap.remove(uid);
        SocketService socketService = NhBeanUtil.getBean(SocketService.class);
        socketService.userWsState(uid, ChatCode.USER_STATE_OFFLINE.value());
    }
}
