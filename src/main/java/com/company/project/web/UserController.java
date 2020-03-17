package com.company.project.web;

import com.company.project.model.User;
import com.company.project.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class UserController {

    @Resource
    private IUserService userService;

    @GetMapping(value = "/login")
    public ModelAndView test() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/user/login.html");
        return mv;
    }

    @GetMapping(value = "/userList")
    public ModelAndView userList() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/user/userList.html");
        return mv;
    }

    @GetMapping(value = "/userEdit")
    public ModelAndView formEdit(Integer userId) {
        User user = userService.getById(userId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("user", user);
        mv.setViewName("/user/userEdit.html");
        return mv;
    }

    @GetMapping(value = "/userAdd")
    public ModelAndView formAdd() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/user/userAdd.html");
        return mv;
    }

}
