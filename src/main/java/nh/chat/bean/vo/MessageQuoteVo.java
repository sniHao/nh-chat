package nh.chat.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 消息引用Vo
 * @Author: xph
 * @Date: 2024-12-13 17:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageQuoteVo {
    private int action; //消息动作
    private Long quoteId; //引用消息id
    private int quoteType; //引用消息类型
    private String quoteMessage; //引用消息内容
}
