package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    RedisTemplate redisTemplate;

    // 点赞
    public void like(int userId,int entityType,int postId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,postId);
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        if(isMember)
        {
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else
        {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int postId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,postId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }


    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int postId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,postId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

}
