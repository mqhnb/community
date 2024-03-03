package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page)
    {
        //MODEL 自动封装参数
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> listdi = discussPostService.findDiscussPosts(0,page.getOffest(),page.getLimit());

        List<Map<String,Object>>  discussPosts = new ArrayList<>();

        for(DiscussPost post:listdi)
        {
            Map<String,Object> ma = new HashMap<>();
            ma.put("post",post);
            ma.put("user",userService.findUserById(post.getUserId()));
            discussPosts.add(ma);
        }

        model.addAttribute("discussPosts",discussPosts);

        return "/index";
    }



}
