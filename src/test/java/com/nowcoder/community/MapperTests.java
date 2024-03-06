package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;



    @Test
    public void testselectbyid()
    {
        int lis = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(lis);
    }

    @Test
    public void test111()
    {
        List<DiscussPost> lis = discussPostMapper.selectDiscussPosts(0,10,10);

        for(DiscussPost li:lis)
        {
            System.out.println(li);
        }
    }

    @Test
    public void test121()
    {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test1@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        user.setActivationCode("asgsaassgsgasg");
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));


        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Autowired
    CommentService commentService;

    @Test
    public void tes12()
    {
        DiscussPost discussPost = new DiscussPost();
//        discussPost.setTitle("assag");
//        discussPost.setContent("sagasg");
//        discussPostMapper.insertDiscussPost(discussPost);

        List<Comment> commentList =  commentService.
                findCommentsEneity(CommunityConstant.ENTITY_TYPE_COMMENT,12,0, 5);


        System.out.println(commentList);

    }

    @Autowired
    MessageService messageService;
    @Test
    public void tes21512()
    {
        int a = messageService.findLetterUnreadCount(111,null);
        List<Message> lsit1 =  messageService.findConversations(111,0,5);
        List<Message> lsit12 =  messageService.findLetters("111_145",0,5);
        messageService.findConversationCount(111);
        messageService.findLetterCount("111_145");

        System.out.println(a);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdate() {
        userMapper.updateStatus(1924665350, 1);

    }



}
