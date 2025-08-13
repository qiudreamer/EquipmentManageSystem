package com.example.springboot_ch_1.service.user;

import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.entity.User;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:06
 * Description:
 */
public interface UserService {
    ReturnStatus login(String data);

    ReturnStatus changePassword(String data);

    ReturnStatus changeLoginTime(String data);

}
