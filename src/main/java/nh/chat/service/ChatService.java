package nh.chat.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import nh.chat.bean.dto.SendMessageDto;
import nh.chat.bean.po.Chat;
import nh.chat.bean.po.Message;
import nh.chat.bean.po.Relation;
import nh.chat.bean.vo.MessageVo;
import nh.chat.bean.vo.PushMessage;
import nh.chat.bean.vo.RelationVo;
import nh.chat.common.AsyncCom;
import nh.chat.constant.ChatCode;
import nh.chat.exception.ChatException;
import nh.chat.mapper.ChatMapper;
import nh.chat.mapper.MessageMapper;
import nh.chat.mapper.RelationMapper;
import nh.chat.utils.NhBeanCopy;
import nh.chat.utils.ChatSocket;
import nh.chat.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final RelationMapper relationMapper;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final ChatSocket chatSocket;
    private final AsyncCom asyncCom;
    private final FileUtil fileUtil;


    public List<RelationVo> eqRelation(Long uid) {
        LambdaQueryWrapper<Relation> qw = new LambdaQueryWrapper<Relation>()
                .eq(Relation::getUid, uid).eq(Relation::getState, ChatCode.MESSAGE_HEALTH.value())
                .orderByDesc(Relation::getLastMessageDate);
        return NhBeanCopy.BEANCOPY.relationVos(relationMapper.selectList(qw));
    }

    public RelationVo goChat(Long uid, Long receiveUid) throws ChatException {
        LambdaQueryWrapper<Relation> qw = new LambdaQueryWrapper<Relation>()
                .eq(Relation::getUid, uid).eq(Relation::getRelationUid, receiveUid);
        Relation relation = relationMapper.selectOne(qw);
        Date date = new Date();
        if (Objects.isNull(relation)) {
            Relation relationNew = new Relation(uid, receiveUid, date, date);
            relationMapper.insert(relationNew);
            return NhBeanCopy.BEANCOPY.relationVo(relationNew);
        }
        LambdaUpdateWrapper<Relation> uw = new LambdaUpdateWrapper<Relation>()
                .eq(Relation::getUid, uid).eq(Relation::getRelationUid, receiveUid);
        if (relation.getState() == ChatCode.MESSAGE_HEALTH.value()) {
            RelationVo relationVo = NhBeanCopy.BEANCOPY.relationVo(relation);
            relationVo.setNotRead(0);
            if (relation.getNotRead() == 0) return relationVo;
            uw.set(Relation::getNotRead, 0);
            relationMapper.update(uw);
            return relationVo;
        }
        uw.set(Relation::getState, ChatCode.MESSAGE_HEALTH.value()).set(Relation::getDate, date);
        relationMapper.update(uw);
        return NhBeanCopy.BEANCOPY.relationVo(relation);
    }

    /**
     * 消息查询公共
     *
     * @param uid        用户
     * @param receiveUid 接收用户
     * @return MPJLambdaWrapper<Chat>
     */
    public MPJLambdaWrapper<Chat> setEqChatCom(Long uid, Long receiveUid) {
        return new MPJLambdaWrapper<Chat>().selectAll(Chat.class)
                .select(Message::getMessage).leftJoin(Message.class, Message::getId, Chat::getMid)
                .and(i -> i.eq(Chat::getSendState, ChatCode.MESSAGE_HEALTH.value())
                        .eq(Chat::getSendUid, uid).eq(Chat::getReceiveUid, receiveUid))
                .or(i -> i.eq(Chat::getReceiveState, ChatCode.MESSAGE_HEALTH.value())
                        .ne(Chat::getSendState, ChatCode.MESSAGE_REVOCATION.value())
                        .eq(Chat::getSendUid, receiveUid).eq(Chat::getReceiveUid, uid))
                .orderByDesc(Chat::getDate);
    }

    public Map<String, Object> eqChat(Long uid, Long receiveUid, Long page) {
        asyncCom.clearRead(uid, receiveUid);
        MPJLambdaWrapper<Chat> qw = setEqChatCom(uid, receiveUid);
        Page<MessageVo> messageVoPage = chatMapper.selectJoinPage(new Page<>(page, 20), MessageVo.class, qw);
        List<MessageVo> list = new ArrayList<>(messageVoPage.getRecords().stream().filter(item -> {
            if (item.getSendState() == ChatCode.MESSAGE_REVOCATION.value() ||
                    item.getReceiveState() == ChatCode.MESSAGE_REVOCATION.value()) item.setMessage("撤回了一条消息");
            return true;
        }).toList());
        HashMap<String, Object> map = new HashMap<>();
        Collections.reverse(list);
        map.put("data", list);
        boolean next = false;
        if (list.size() > 0) {
            if (page > 1) next = (page - 1) * 20 + list.size() < messageVoPage.getTotal();
            else next = list.size() < messageVoPage.getTotal();
        }
        map.put("next", next);
        return map;
    }

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

    public Long sendMessageCom(Long uid, Long receiveUid, String message, int type) {
        // 通讯录最后消息
        asyncCom.messageRelation(uid, receiveUid, message, type, false, 1);
        asyncCom.messageRelation(receiveUid, uid, message, type, true, 0);
        // 存消息数据
        Long mId = Long.valueOf(RandomUtil.randomNumbers(12));
        messageMapper.insert(new Message(mId, message));
        Long cid = Long.valueOf(RandomUtil.randomNumbers(12));
        chatMapper.insert(new Chat(cid, uid, receiveUid, mId, type, new Date()));
        // 推消息
        if (!uid.equals(receiveUid)) pushCom(cid, uid, receiveUid, ChatCode.MESSAGE_HEALTH.value(), message, type);
        return cid;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(Long uid, SendMessageDto sendMessageDto) {
        return sendMessageCom(uid, sendMessageDto.getReceiveUid(), sendMessageDto.getMessage(), sendMessageDto.getType());
    }

    public Long sendMessageImage(Long uid, MultipartFile file, Long receiveUid) throws ChatException {
        String url = fileUtil.uploadImg(file);
        return sendMessageCom(uid, receiveUid, url, ChatCode.MESSAGE_TYPE_IMAGE.value());
    }

    /**
     * 设置通讯录操作公共查询
     *
     * @param uid        用户id
     * @param relationId 通讯录id
     * @return uw
     */
    public LambdaUpdateWrapper<Relation> setEqConditions(Long uid, Long relationId) {
        return new LambdaUpdateWrapper<Relation>()
                .eq(Relation::getUid, uid).eq(Relation::getId, relationId).eq(Relation::getState, ChatCode.MESSAGE_HEALTH.value());
    }

    public void topChat(Long uid, Long relationId, int state) throws ChatException {
        int upResult = relationMapper.update(setEqConditions(uid, relationId).set(Relation::getTop, state));
        if (upResult == 0) throw new ChatException("状态异常，设置失败");
    }

    public void delChat(Long uid, Long relationId) throws ChatException {
        int upResult = relationMapper.update(setEqConditions(uid, relationId).set(Relation::getState, ChatCode.MESSAGE_DEL.value()));
        if (upResult == 0) throw new ChatException("状态异常，设置失败");
    }

    /**
     * 设置消息操作公共查询
     *
     * @param uid 用户id
     * @param mid 消息id
     * @return uw
     */
    public LambdaUpdateWrapper<Chat> setEqChat(Long uid, Long mid) {
        return new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getSendUid, uid).eq(Chat::getId, mid).eq(Chat::getSendState, ChatCode.MESSAGE_HEALTH.value());
    }

    public Chat eqChatReceiveUid(Long uid, Long mid) throws ChatException {
        LambdaQueryWrapper<Chat> qw = new LambdaQueryWrapper<Chat>().select(Chat::getReceiveUid, Chat::getReceiveState)
                .eq(Chat::getId, mid).eq(Chat::getSendUid, uid).eq(Chat::getSendState, ChatCode.MESSAGE_HEALTH.value());
        Chat chat = chatMapper.selectOne(qw);
        if (Objects.isNull(chat)) throw new ChatException("消息不存在");
        return chat;
    }

    @Transactional(rollbackFor = Exception.class)
    public void revocationMessage(Long uid, Long mid) throws ChatException, IOException {
        Chat chat = eqChatReceiveUid(uid, mid);
        int upResult = chatMapper.update(setEqChat(uid, mid).set(Chat::getSendState, ChatCode.MESSAGE_REVOCATION.value()));
        if (upResult == 0) throw new ChatException("消息异常，撤回失败");
        asyncCom.messageRelation(chat.getReceiveUid(), uid, "您撤回了一条消息", ChatCode.MESSAGE_TYPE_NORMAL.value(), true, 0);
        if (chat.getReceiveState() == ChatCode.MESSAGE_HEALTH.value()) {
            asyncCom.messageRelation(uid, chat.getReceiveUid(), "对方撤回了一条消息", ChatCode.MESSAGE_TYPE_NORMAL.value(), false, 0);
            if (!uid.equals(chat.getReceiveUid()))
                pushCom(mid, uid, chat.getReceiveUid(), ChatCode.MESSAGE_REVOCATION.value(), "对方撤回了一条消息", ChatCode.MESSAGE_TYPE_NORMAL.value());
        }
    }

    public String eqLastMessage(Long uid, Long receiveUid) {
        MPJLambdaWrapper<Chat> qw = setEqChatCom(uid, receiveUid);
        Page<MessageVo> messageVoPage = chatMapper.selectJoinPage(new Page<>(1, 1), MessageVo.class, qw);
        if (messageVoPage.getRecords().size() == 0) return "消息被删除";
        return messageVoPage.getRecords().get(0).getMessage();
    }

    @Transactional(rollbackFor = Exception.class)
    public String delMessage(Long uid, Long[] mIds) throws ChatException {
        LambdaQueryWrapper<Chat> qw = new LambdaQueryWrapper<Chat>().in(Chat::getId, Arrays.asList(mIds).toArray())
                .and(i -> i.eq(Chat::getSendUid, uid).or(ii -> ii.eq(Chat::getReceiveUid, uid))).orderByDesc(Chat::getDate);
        List<Chat> chats = chatMapper.selectList(qw);
        if (chats.isEmpty()) throw new ChatException("消息不存在");
        List<Long> self = chats.stream().filter(item -> item.getSendUid().equals(uid)).map(Chat::getId).toList();
        List<Long> receive = chats.stream().filter(item -> !item.getSendUid().equals(uid)).map(Chat::getId).toList();
        if (!self.isEmpty()) asyncCom.delMessage(uid, self, true);
        if (!receive.isEmpty()) asyncCom.delMessage(uid, receive, false);
        Chat chat = chats.get(chats.size() - 1);
        Long newUid = uid;
        Long receiveUid = chat.getReceiveUid();
        if (!chat.getSendUid().equals(uid)) {
            newUid = chat.getReceiveUid();
            receiveUid = chat.getSendUid();
        }
        String message = eqLastMessage(newUid, receiveUid);
        asyncCom.messageRelation(receiveUid, newUid, message, ChatCode.MESSAGE_TYPE_NORMAL.value(), true, 0);
        return "message";
    }
}