package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content)
    {
        User user = hostHolder.getUsers();

        if(user==null)
        {
            return CommunityUtil.getJSONString(403,"你还没有填好内容！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0,"发布完毕！");

    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDisscussPost(@PathVariable("discussPostId")int id, Model model,Page page)
    {
        //帖子
        DiscussPost discussPost = discussPostService.selectDiscussPostById(id);

        model.addAttribute("post",discussPost);
        //作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(discussPost.getCommentCount());

        //点赞数
        long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST,id);
        model.addAttribute("likeCount",likeCount);
        //状态
        long likeStatus = hostHolder.getUsers()==null ? 0:
                likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),CommunityConstant.ENTITY_TYPE_POST,id);
        model.addAttribute("likeStatus",likeStatus);

        List<Comment> commentList =  commentService.
                findCommentsEneity(CommunityConstant.ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(), page.getLimit());

        // 评论区
        List<Map<String,Object>> commentsResult = new ArrayList<>();
        if(commentList != null)
        {
            for(Comment commentOV:commentList)
            {
                //一条评论，评论下有回复，回复也是一个List合集，合集里放着map，map存的是回复
                Map<String,Object> tempcomment = new HashMap<>();

                //用户名
                tempcomment.put("user",userService.findUserById(commentOV.getUserId()));
                //评论
                tempcomment.put("comment",commentOV);

                //得到本评论所有回复
                List<Comment> replyList =  commentService.
                        findCommentsEneity(CommunityConstant.ENTITY_TYPE_COMMENT,commentOV.getId(),0, Integer.MAX_VALUE);

                List<Map<String,Object>> replyResult = new ArrayList<>();
                if(replyList!=null)
                {
                    for(Comment reply:replyList)
                    {
                        //遍历回复列表,把回复方，被回复方，回复数遍历出来存到map里
                        Map<String,Object> replyVo = new HashMap<>();
                        //被回复方
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        //回复方
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复内容
                        replyVo.put("reply",reply);

                        //点赞数
                        long likeCount2 = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount2);
                        //状态
                        long likeStatus2 = hostHolder.getUsers()==null ? 0:
                                likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),CommunityConstant.ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus2);

                        //把map存进去
                        replyResult.add(replyVo);
                    }
                }

                //回复
                tempcomment.put("replys",replyResult);

                //点赞数
                long likeCount3 = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT,commentOV.getId());
                tempcomment.put("likeCount",likeCount3);
                //状态
                long likeStatus3 = hostHolder.getUsers()==null ? 0:
                        likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),CommunityConstant.ENTITY_TYPE_COMMENT,commentOV.getId());
                tempcomment.put("likeStatus",likeStatus3);

                // 回复数量
                int replyCount = commentService.findCommentsCount(ENTITY_TYPE_COMMENT, commentOV.getId());
                tempcomment.put("replyCount", replyCount);

                commentsResult.add(tempcomment);
            }
        }

        model.addAttribute("comments",commentsResult);

        return "/site/discuss-detail";
    }



}

