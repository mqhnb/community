package com.nowcoder.community.controller;

import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.entity.User;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int postId)
    {
        User user = hostHolder.getUsers();

        likeService.like(user.getId(),entityType,postId);

        long likecount = likeService.findEntityLikeCount(entityType,postId);

        int status = likeService.findEntityLikeStatus(user.getId(),entityType,postId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likecount);
        map.put("likeStatus",status);


        return CommunityUtil.getJSONString(0,null,map);
    }
}
