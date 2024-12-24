package nh.chat.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description:通讯关系Vo
 * @Author: xph
 * @Date: 2024-09-27 16:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationVo {
    private Long id;
    private Long uid;
    private Long relationUid;
    private String lastMessage;
    private int notRead;
    private int top; // 置顶(0:不置顶  1:置顶)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastMessageDate; //最后一条消息时间

    private int wsState = 0; //是否在线 (0:不在线  1:在线)
}
