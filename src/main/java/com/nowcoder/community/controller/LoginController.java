package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.config.KaptchaConfig;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String context;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private Producer producer;

    @RequestMapping(path="/register",method = RequestMethod.GET)
    public String getRegisterPage()
    {
        return "/site/register";
    }

    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage()
    {
        return "/site/login";
    }

    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String register(Model model, User user)
    {
        Map<String ,Object> map = userService.registerUser(user);
        if(map.isEmpty() || map==null)
        {
            model.addAttribute("msg","注册成功,我们已向您的邮箱发送了一份激活邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result.html";
        }else
        {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String register(Model model, @PathVariable("userId")int userId,@PathVariable("code")String code)
    {

        int resultcode = userService.activation(userId,code);
        if(resultcode== ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号可以正常使用啦！");
            model.addAttribute("target", "/login");
        }else if(resultcode==ACTIVATION_REPAEAT)
        {
            model.addAttribute("msg", "您已激活，请勿重复点击链接！");
            model.addAttribute("target", "/index");
        }else
        {
            model.addAttribute("msg", "激活失败，请点击正确的激活链接！");
            model.addAttribute("target", "/index");
        }

            return "/site/operate-result.html";
    }

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session)
    {
        String text = producer.createText();
        BufferedImage bufferedImage = producer.createImage(text);
        session.setAttribute("kaptcha",text);

        response.setContentType("image/png");

        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage,"png",outputStream);
        } catch (IOException e) {
            logger.error("图片出错啦！");
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String loginPage(HttpServletResponse response ,Model model, String username, String password, String code, boolean rememberme, HttpSession session)
    {
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code) )
        {
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        int tickettime = rememberme ? CommunityConstant.REMEMBER_EXPIRED_SECONDS : CommunityConstant.DEFAULT_EXPIRED_SECONDS;


        Map<String,Object> map = userService.login(username,password,tickettime);

        if(map.containsKey("ticket"))
        {
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(context);
            cookie.setMaxAge(tickettime);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket)
    {
        userService.logout(ticket);
        return "redirect:/login";
    }



}
