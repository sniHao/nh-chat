package nh.chat.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import nh.chat.bean.po.Chat;
import nh.chat.bean.po.Relation;
import nh.chat.constant.ChatCode;
import nh.chat.exception.ChatException;
import nh.chat.mapper.ChatMapper;
import nh.chat.mapper.RelationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Description:异步方法
 * @Author: xph
 * @Date: 2024-09-29 15:45
 */
@Component
@RequiredArgsConstructor
public class AsyncCom {
    private static final Logger logger = LoggerFactory.getLogger(AsyncCom.class);

    private final ChatMapper chatMapper;
    private final RelationMapper relationMapper;

    /**
     * 消息长度截取
     *
     * @param message 消息
     * @return s
     */
    public static String truncate(String message) {
        if (message == null) return null;
        if (message.length() > 30) return message.substring(0, 30) + "...";
        return message;
    }

    /**
     * 清除未读数
     *
     * @param id 通讯录id
     */
    @Async
    public void clearNotRead(Long id) {
        relationMapper.update(new LambdaUpdateWrapper<Relation>().eq(Relation::getId, id).set(Relation::getNotRead, 0));
    }

    /**
     * 消息处理
     *
     * @param sendUid    发送id
     * @param receiveUid 接收id
     * @param message    消息
     * @param type       类型
     * @param clearRead  是否清除未读数
     */
    @Async
    public void messageRelation(Long sendUid, Long receiveUid, String message, int type, Boolean clearRead, int addRead) {
        Relation relation = relationMapper.selectOne(new LambdaQueryWrapper<Relation>()
                .eq(Relation::getUid, receiveUid).eq(Relation::getRelationUid, sendUid));
        Date date = new Date();
        String truncateMessage = truncate(message);
        if (type == ChatCode.MESSAGE_TYPE_IMAGE.value())
            truncateMessage = "[" + ChatCode.MESSAGE_TYPE_IMAGE.tips() + "]";
        if (Objects.isNull(relation)) {
            relationMapper.insert(new Relation(receiveUid, sendUid, truncateMessage, 1, date, date, date));
            return;
        }
        LambdaUpdateWrapper<Relation> uw = new LambdaUpdateWrapper<Relation>()
                .eq(Relation::getUid, receiveUid).eq(Relation::getRelationUid, sendUid)
                .set(Relation::getLastMessage, truncateMessage).set(Relation::getLastMessageDate, date)
                .setSql(clearRead ? "not_read=0" : "not_read=not_read+" + addRead);
        if (relation.getState() != ChatCode.MESSAGE_HEALTH.value())
            uw.set(Relation::getState, ChatCode.MESSAGE_HEALTH.value());
        relationMapper.update(uw);
    }

    /**
     * 清除未读数
     *
     * @param uid        发送
     * @param receiveUid 接收
     */
    @Async
    public void clearRead(Long uid, Long receiveUid) {
        LambdaUpdateWrapper<Relation> uw = new LambdaUpdateWrapper<Relation>()
                .set(Relation::getNotRead, 0)
                .eq(Relation::getUid, uid).eq(Relation::getRelationUid, receiveUid);
        relationMapper.update(uw);
    }

    @Async
    public void delMessage(Long uid, List<Long> mIds, Boolean isSelf) throws ChatException {
        LambdaUpdateWrapper<Chat> uw = new LambdaUpdateWrapper<>();
        uw.eq(Chat::getSendState, ChatCode.MESSAGE_HEALTH.value())
                .in(Chat::getId, mIds)
                .and(i -> i.eq(Chat::getSendUid, uid).or(ii -> ii.eq(Chat::getReceiveUid, uid)))
                .set(isSelf ? Chat::getSendState : Chat::getReceiveState, ChatCode.MESSAGE_DEL.value());
        int upResult = chatMapper.update(uw);
        if (upResult == 0) throw new ChatException("消息异常，删除失败");
    }

}
