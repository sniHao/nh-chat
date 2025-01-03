package nh.chat.bean.dto;

import lombok.Data;

/**
 * @Description:聊天记录
 * @Author: xph
 * @Date: 2025-01-03 15:09
 */
@Data
public class ChatRecordDto {
    private Long receiveUid; //对方Id
    private Long page; // 页码
    private String search; // 搜索
    private int type; // 类型
    private Long date; // 时间戳
}
