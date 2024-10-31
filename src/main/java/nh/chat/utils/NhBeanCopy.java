package nh.chat.utils;

import nh.chat.bean.po.Relation;
import nh.chat.bean.vo.RelationVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface NhBeanCopy {
    NhBeanCopy BEANCOPY = Mappers.getMapper(NhBeanCopy.class);

    RelationVo relationVo(Relation relation);

    List<RelationVo> relationVos(List<Relation> relations);
}
