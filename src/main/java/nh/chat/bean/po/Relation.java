package nh.chat.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description:通讯关系
 * @Author: xph
 * @Date: 2024-09-25 15:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("c_relation")
public class Relation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long uid;//用户id
    private Long relationUid; //通讯列表用户id
    private int state = 1; //关系健康状态(0:不存在 1:存在)
    private String lastMessage; // 最后聊天消息
    private int notRead; // 未读数
    private int top; // 置顶(0:不置顶  1:置顶)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastMessageDate; //最后一条消息时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date initDate; //关系首次创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date; //关系后续创建时间

    public Relation(Long uid, Long relationUid, Date initDate, Date date) {
        this.uid = uid;
        this.relationUid = relationUid;
        this.initDate = initDate;
        this.date = date;
    }

    public Relation(Long uid, Long relationUid, String lastMessage, int notRead, Date lastMessageDate, Date initDate, Date date) {
        this.uid = uid;
        this.relationUid = relationUid;
        this.lastMessage = lastMessage;
        this.notRead = notRead;
        this.lastMessageDate = lastMessageDate;
        this.initDate = initDate;
        this.date = date;
    }

    public Relation(Long uid, Long relationUid, String lastMessage, int notRead, Date lastMessageDate, Date date) {
        this.uid = uid;
        this.relationUid = relationUid;
        this.lastMessage = lastMessage;
        this.notRead = notRead;
        this.lastMessageDate = lastMessageDate;
        this.date = date;
    }
}
