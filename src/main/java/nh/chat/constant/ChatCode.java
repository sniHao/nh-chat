package nh.chat.constant;

public enum ChatCode {
    MESSAGE_DEL("删除", 0),
    MESSAGE_HEALTH("存在", 1),
    MESSAGE_REVOCATION("消息撤回", 2),
    MESSAGE_TYPE_NORMAL("普通消息", 0),
    MESSAGE_TYPE_IMAGE("图片", 1),
    MESSAGE_ACTION_NULL("无动作", 0),
    MESSAGE_ACTION_TRANSMIT("转发", 1),
    MESSAGE_ACTION_QUOTE("引用", 2),
    CHAT_NO_TOP("不置顶", 0),
    CHAT_TOP("置顶", 1),
    USER_STATE_OFFLINE("离线", 0),
    USER_STATE_ONLINE("在线", 1),
    USER_STATE_WRITE("用户输入中", 2);

    private final String tips;
    private final Integer value;

    ChatCode(String tips, Integer value) {
        this.tips = tips;
        this.value = value;
    }

    public String tips() {
        return tips;
    }

    public Integer value() {
        return value;
    }
}
