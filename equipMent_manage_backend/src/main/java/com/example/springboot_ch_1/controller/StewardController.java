package com.example.springboot_ch_1.controller;

import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.service.userroot.UserRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 5:19
 * Description:
 */
@RestController
@RequestMapping("/steward")
public class StewardController {
    @Autowired
    private UserRootService userRootService;

    @PostMapping("/login")
    public ReturnStatus login(@RequestBody String data) {
        return userRootService.login(data);
    }
    @PostMapping("/changeLoginTime")
    public ReturnStatus changeLoginTime(@RequestBody String data) {
        return userRootService.changeLoginTime(data);
    }
    @PostMapping("/getAllUser")
    public ReturnStatus getAllUser(@RequestBody String data) {
        return userRootService.getAllUser(data);
    }
    @PostMapping("/setAdmin")
    public ReturnStatus setAdmin(@RequestBody String data) {
        return userRootService.setAdmin(data);
    }
    @PostMapping("/revokeAdmin")
    public ReturnStatus revokeAdmin(@RequestBody String data) {
        return userRootService.revokeAdmin(data);
    }
    @PostMapping("/viewPwd")
    public ReturnStatus viewPwd(@RequestBody String data) {
        return userRootService.viewPwd(data);
    }
    @PostMapping("/delUser")
    public ReturnStatus delUser(@RequestBody String data) {
        return userRootService.delUser(data);
    }

    @PostMapping("/searchUser")
    public ReturnStatus searchUser(@RequestBody String data) {
        return userRootService.searchUser(data);
    }

    @PostMapping("/getPinYin")
    public ReturnStatus getPinYin(@RequestBody String data) {
        return userRootService.getPinYin(data);
    }

    @PostMapping("/submitNewUserData")
    public ReturnStatus submitNewUserData(@RequestBody String data) {
        return userRootService.submitNewUserData(data);
    }
}
