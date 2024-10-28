package nh.chat.utils;

import nh.chat.common.AsyncCom;
import nh.chat.constant.ResultCode;
import nh.chat.exception.ChatException;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:私信消息推送
 * @Author: xph
 * @Date: 2024-09-25 17:16
 */
@Component
@Slf4j
@ServerEndpoint("/socket.chat/{token}")
public class ChatSocket {
    private static final Logger logger = LoggerFactory.getLogger(ChatSocket.class);
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象
     */
    private static final ConcurrentHashMap<Long, ChatSocket> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    private Long userId;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 会话
     * @param token   用户token
     * @return boolean
     */
    @OnOpen
    public boolean onOpen(Session session, @PathParam("token") String token) throws ChatException {
        RedisUtil redisUtil = BeanUtil.getBean(RedisUtil.class);
//        Long uid = TokenUtil.userTokenNotException(token, redisUtil);
        Long uid = 21L;
        if (Objects.isNull(uid)) throw new ChatException(ResultCode.NO_LOGIN.tips(), ResultCode.NO_LOGIN.value());
        this.session = session;
        this.userId = uid;
        webSocketMap.put(this.userId, this);
        return true;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            if (!Objects.isNull(this.userId)) webSocketMap.remove(this.userId);
        } catch (Exception e) {
            logger.error("关闭连接异常: {}", this.userId);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息体
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        AsyncCom asyncCom = BeanUtil.getBean(AsyncCom.class);
        asyncCom.clearNotRead(Long.valueOf(message));
    }

    /**
     * 发送消息
     *
     * @param uid     用户Id
     * @param message 消息
     * @throws IOException e
     */
    public void sendMessage(Long uid,
                            String message) throws IOException {
        if (webSocketMap.containsKey(uid))
            webSocketMap.get(uid).session.getBasicRemote().sendText(message);
    }

    @OnError
    public void onError(@PathParam("token") String token, Session session, Throwable t) {
//        logger.error("连接错误，{}", (t.getMessage() == null ? ResultCode.NO_LOGIN.tips() : null));
    }
}
