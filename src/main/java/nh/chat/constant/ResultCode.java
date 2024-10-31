package nh.chat.constant;

public enum ResultCode {
    SUCCESS("成功响应", 200),
    FAIL("失败响应", 400),
    OFTEN("访问过于频繁，请稍候再试", 408),
    NO_LOGIN("未登录", 403);
    private final String tips;
    private final Integer value;

    ResultCode(String tips, Integer value) {
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
