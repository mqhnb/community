package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,@Param("offset")int offset,@Param("limit")int limit);

    // 如果只有动态语句的if里，只有一个参数，那么必须给参数取别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(@Param("id") int id);

    int updateCommentCount(@Param("id") int id,@Param("commentCount") int commentCount);

}
