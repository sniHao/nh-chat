package nh.chat.constant;

public enum ChatCode {
    MESSAGE_DEL("删除", 0),
    MESSAGE_HEALTH("存在", 1),
    MESSAGE_REVOCATION("消息撤回", 2),
    MESSAGE_TYPE_NORMAL("普通消息", 0),
    MESSAGE_TYPE_IMAGE("图片", 1),
    CHAT_NO_TOP("不置顶", 0),
    CHAT_TOP("置顶", 1);

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
