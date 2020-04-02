package com.company.project.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.utils.ImageCodeUtil;
import com.company.project.utils.JwtUtils;
import com.company.project.utils.MD5Utils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.company.project.service.IUserService;
import com.company.project.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author project
 * @since 2020-01-08
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Resource
    private IUserService userService;

    @ApiOperation("登陆")
    @PostMapping("/login")
    public Result login(@RequestBody @Valid User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User userO = userService.getOne(queryWrapper);
        if (userO == null) {
            return ResultGenerator.genFailResult("账号未找到");
        }
        if (!MD5Utils.Encrypt(user.getPassword(),true).equals(userO.getPassword())) {
            return ResultGenerator.genFailResult("密码错误");
        }
        String token = JwtUtils.geneJsonWebToken(user);
        Map<String, String> resultMap = new HashMap<>(2);
        resultMap.put("username", user.getUsername());
        resultMap.put("token", token);
        return ResultGenerator.genSuccessResult(resultMap);
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody @Valid User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User userO = userService.getOne(queryWrapper);
        if (userO != null) {
            return ResultGenerator.genFailResult("账号已存在");
        }
        user.setPassword(MD5Utils.Encrypt(user.getPassword(),true));
        userService.save(user);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "删除")
    @PostMapping("delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        userService.removeById(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "更新")
    @PostMapping("update")
    public Result update(@RequestBody User user){
        //密码不更新
        user.setPassword(null);
        userService.updateById(user);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "currentPage", value = "页码"),
        @ApiImplicitParam(name = "pageCount", value = "每页条数")
    })
    @GetMapping("listByPage")
    public Result findListByPage(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                    @RequestParam(required = false) String username){
        Page pageParam = new Page(page, limit);
        QueryWrapper queryWrapper = new QueryWrapper();
        if (StringUtils.isNotEmpty(username)) {
            queryWrapper.like("username", username);
        }
        IPage<User> iPage = userService.page(pageParam, queryWrapper);
        return ResultGenerator.genSuccessResult(iPage);
    }

    @ApiOperation(value = "id查询")
    @GetMapping("getById/{id}")
    public Result findById(@PathVariable Long id){
        return ResultGenerator.genSuccessResult(userService.getById(id));
    }

    @ApiOperation(value = "生成验证码")
    @GetMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        try {
            ImageCodeUtil randomValidateCode = new ImageCodeUtil();
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            log.error("生成验证码失败");
        }
    }

    @ApiOperation(value = "校验验证码")
    @PostMapping(value = "/checkVerify")
    public Result checkVerify(@RequestParam String imageCode, HttpSession session) {
        //从session中获取随机数
        Object random = session.getAttribute(ImageCodeUtil.IMAGE_RANDOM_CODEKEY);
        if (random != null && String.valueOf(random).equals(imageCode)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("验证码输入有误");
    }

}
