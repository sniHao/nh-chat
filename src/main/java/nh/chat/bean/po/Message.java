package nh.chat.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:聊天内容详情
 * @Author: xph
 * @Date: 2024-09-25 15:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("c_message")
public class Message {
    @TableId(value = "id")
    private Long id;
    private String message; // 内容
}
