package com.example.springboot_ch_1.controller;

import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.entity.User;
import com.example.springboot_ch_1.repository.UserRepository;
import com.example.springboot_ch_1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:58
 * Description:
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ReturnStatus login(@RequestBody String data) {
        return userService.login(data);
    }

    @PostMapping("/changePassword")
    public ReturnStatus changePassword(@RequestBody String data){
        return userService.changePassword(data);
    }

    @PostMapping("/changeLoginTime")
    public ReturnStatus changeLoginTime(@RequestBody String data){
        return userService.changeLoginTime(data);
    }


}