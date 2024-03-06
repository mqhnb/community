package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework. web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String upload;

    @Value("${server.servlet.context-path}")
    private String context;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;


    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage()
    {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model)
    {
            if(headerImage==null)
            {
                model.addAttribute("error","未选择图片");
                return "/site/setting";
            }

            String filepath = headerImage.getOriginalFilename();
            String suffix = filepath.substring(filepath.lastIndexOf("."));

            String filename = CommunityUtil.generateUUID() + suffix;

            File dest = new File(upload + "/" + filename);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+ e.getMessage());
            throw new RuntimeException("上传文件失败",e);
        }

        User user = hostHolder.getUsers();
        String headerUrl = domain + context + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void showHeader(@PathVariable("filename")String filename, HttpServletResponse response)
    {

        filename = upload + "/" + filename;

        String suffix = filename.substring(filename.lastIndexOf(".")+1);

        response.setContentType("image/" + suffix);

        try (
                FileInputStream fis = new FileInputStream(filename); //自己创建的输入流需要手动关闭

                ){
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int b =0;
            while ((b = fis.read(buffer))!=-1)
            {
                    os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:" + e.getMessage());
        }


    }



}
