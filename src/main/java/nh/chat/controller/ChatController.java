package nh.chat.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import nh.chat.bean.dto.SendMessageDto;
import nh.chat.constant.ChatCode;
import nh.chat.exception.ChatException;
import nh.chat.limiter.LimiterType;
import nh.chat.limiter.RateLimiter;
import nh.chat.service.ChatService;
import nh.chat.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
@Tag(name = "Chat-Controller", description = "关于聊天接口")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "获取用户通讯列表【返回id，需要自定义接口再去查头像和昵称】")
    @GetMapping("eqRelation")
    public Result<?> eqRelation(@RequestAttribute("uid") Long uid) {
        return Result.success("ok", chatService.eqRelation(uid));
    }

    @Operation(summary = "发起私信-通讯关系调整")
    @Parameter(name = "receiveUid", description = "接收用户")
    @GetMapping("goChat/{receiveUid}")
    public Result<?> goChat(@RequestAttribute("uid") Long uid, @PathVariable("receiveUid") Long receiveUid) {
        return Result.success("ok", chatService.goChat(uid, receiveUid));
    }

    @Operation(summary = "聊天数据")
    @Parameter(name = "receiveUid", description = "接收用户")
    @Parameter(name = "page", description = "页码")
    @GetMapping("eqChat/{receiveUid}/{page}")
    public Result<?> eqChat(@RequestAttribute("uid") Long uid, @PathVariable("receiveUid") Long receiveUid,
                            @PathVariable("page") Long page) {
        return Result.success("ok", chatService.eqChat(uid, receiveUid, page));
    }

    @RateLimiter(time = 50, count = 99, limitType = LimiterType.IP)
    @Operation(summary = "发送普通消息【*：需要校验对方用户是否存在】")
    @PostMapping("sendMessage")
    public Result<?> sendMessage(@RequestAttribute("uid") Long uid, @RequestBody SendMessageDto sendMessageDto) throws ChatException {
        if (Objects.isNull(sendMessageDto.getReceiveUid())) throw new ChatException("参数异常");
        if (StringUtils.isEmpty(sendMessageDto.getMessage()) && sendMessageDto.getType() == ChatCode.MESSAGE_TYPE_NORMAL.value())
            throw new ChatException("消息不能为空");
        return Result.success("发送成功", chatService.sendMessage(uid, sendMessageDto));
    }

    @RateLimiter(time = 50, count = 30, limitType = LimiterType.IP)
    @Operation(summary = "发送图片消息【*：需要校验对方用户是否存在】")
    @PostMapping("sendMessageImage")
    public Result<?> sendMessageImage(@RequestAttribute("uid") Long uid, SendMessageDto sendMessageDto) throws ChatException {
        if (Objects.isNull(sendMessageDto.getReceiveUid()) || Objects.isNull(sendMessageDto.getFile()))
            throw new ChatException("参数异常");
        return Result.success("发送成功", chatService.sendMessageImage(uid, sendMessageDto));
    }

    @Operation(summary = "通讯录置顶")
    @Parameter(name = "relationId", description = "通讯录ID")
    @Parameter(name = "state", description = "置顶还是取消")
    @PutMapping("topChat/{relationId}/{state}")
    public Result<?> topChat(@RequestAttribute("uid") Long uid, @PathVariable Long relationId, @PathVariable int state) throws ChatException {
        if (state != ChatCode.CHAT_TOP.value() && state != ChatCode.CHAT_NO_TOP.value())
            throw new ChatException("参数异常");
        chatService.topChat(uid, relationId, state);
        return Result.success(state == ChatCode.CHAT_TOP.value() ? "已置顶" : "已取消置顶");
    }

    @Operation(summary = "通讯录删除")
    @Parameter(name = "relationId", description = "通讯录ID")
    @PutMapping("delChat/{relationId}")
    public Result<?> delChat(@RequestAttribute("uid") Long uid, @PathVariable Long relationId) throws ChatException {
        chatService.delChat(uid, relationId);
        return Result.success("已删除");
    }

    @Operation(summary = "撤回消息")
    @Parameter(name = "mid", description = "消息id")
    @PutMapping("revocationMessage/{mid}")
    public Result<?> revocationMessage(@RequestAttribute("uid") Long uid, @PathVariable Long mid) throws ChatException {
        chatService.revocationMessage(uid, mid);
        return Result.success("已撤回");
    }

    @Operation(summary = "删除消息")
    @Parameter(name = "mIds", description = "消息id集合")
    @PostMapping("delMessage")
    public Result<?> delMessage(@RequestAttribute("uid") Long uid, @RequestBody Long[] mIds) throws ChatException {
        return Result.success("已删除", chatService.delMessage(uid, mIds));
    }

    @Operation(summary = "清除消息未读数")
    @Parameter(name = "receiveUid", description = "通讯录用户")
    @PutMapping("clearNotRead/{receiveUid}")
    public Result<?> clearNotRead(@RequestAttribute("uid") Long uid, @PathVariable("receiveUid") Long receiveUid) {
        chatService.clearNotRead(uid, receiveUid);
        return Result.success("ok");
    }
}
