package nh.chat.bean.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:发送消息参数
 * @Author: xph
 * @Date: 2024-09-29 15:37
 */
@Data
public class SendMessageDto {
    private Long receiveUid; //接收用户id
    private String message; //内容
    private int type; //消息类型
    private int action; //消息动作
    private Long quoteId; //引用消息id
    MultipartFile file; //文件
}
