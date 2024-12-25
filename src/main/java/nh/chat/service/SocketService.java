package nh.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import nh.chat.bean.po.Relation;
import nh.chat.bean.vo.PushMessage;
import nh.chat.common.SocketCom;
import nh.chat.constant.ChatCode;
import nh.chat.mapper.RelationMapper;
import nh.chat.utils.ChatSocket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: xph
 * @Date: 2024-12-25 9:59
 */
@Service
@RequiredArgsConstructor
public class SocketService {
    private final SocketCom socketCom;
    private final ChatSocket chatSocket;
    private final RelationMapper relationMapper;


    public void userWsState(Long uid, int state) {
        LambdaQueryWrapper<Relation> qw = new LambdaQueryWrapper<Relation>().select(Relation::getUid)
                .eq(Relation::getRelationUid, uid)
                .eq(Relation::getState, ChatCode.MESSAGE_HEALTH.value());
        List<Relation> relations = relationMapper.selectList(qw);
        if (Objects.isNull(relations)) return;
        for (Relation relation : relations) {
            socketCom.pushUserState(relation.getUid(), uid, state);
        }
    }

}
