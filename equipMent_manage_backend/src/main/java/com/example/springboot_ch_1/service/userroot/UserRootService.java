package com.example.springboot_ch_1.service.userroot;

import com.example.springboot_ch_1.entity.ReturnStatus;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 5:22
 * Description:
 */
public interface UserRootService {
    ReturnStatus login(String data);

    ReturnStatus changeLoginTime(String data);

    ReturnStatus getAllUser(String data);

    ReturnStatus revokeAdmin(String data);

    ReturnStatus setAdmin(String data);

    ReturnStatus viewPwd(String data);

    ReturnStatus delUser(String data);

    ReturnStatus searchUser(String data);


    ReturnStatus getPinYin(String data);

    ReturnStatus submitNewUserData(String data);
}
