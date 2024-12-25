package nh.chat.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PushUserState {
    private Long uid; // 用户Id
    private int state; // 用户状态
}
