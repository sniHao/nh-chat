package nh.chat.mapper;

import nh.chat.bean.po.Message;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends MPJBaseMapper<Message> {
}
