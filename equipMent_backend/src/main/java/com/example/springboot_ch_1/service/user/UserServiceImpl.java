package com.example.springboot_ch_1.service.user;

import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.entity.User;
import com.example.springboot_ch_1.repository.UserRepository;
import com.example.springboot_ch_1.util.GetTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:06
 * Description:
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ReturnStatus login(String data) {
        System.out.println("nihao");
        try {
            // 1. 解析 JSON
            JSONObject getDataJson = new JSONObject(data);
            System.out.println(getDataJson);
            String userAccount = getDataJson.getStr("userAccount");
            String userPassword = getDataJson.getStr("userPassword");

            // 2. 查询用户
            User checkUser = userRepository.findByUserAccountAndPassword(userAccount, userPassword);
            System.out.println(checkUser);
            // 3. 返回登录结果
            if (checkUser != null) {
                ReturnStatus returnStatus = new ReturnStatus("yes", "登录成功!");
                JSONObject returnJson = new JSONObject();
                returnJson.set("userName",checkUser.getUserName());
                returnJson.set("userAccount",checkUser.getUserAccount());
                if (checkUser.getLoginTime() == null){
                    String time = GetTime.GetSecondTime();
                    checkUser.setLoginTime(time);
                    userRepository.save(checkUser);
                    returnJson.set("loginTime",time);
                }else{
                    returnJson.set("loginTime",checkUser.getLoginTime());
                }
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            } else {
                return new ReturnStatus("no", "账号或者密码错误，请重新输入!");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }


    @Override
    public ReturnStatus changePassword(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String userAccount = jsonObject.getStr("userAccount");
            User changeUser = userRepository.findByUserAccount(userAccount);
            if (changeUser != null){
                changeUser.setPassword(jsonObject.getStr("newPassword"));
                userRepository.save(changeUser);
                System.out.println(changeUser);
                return new ReturnStatus("yes", "修改密码成功!");
            }else{
                return new ReturnStatus("no", "账号已不存在!");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus changeLoginTime(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String userAccount = jsonObject.getStr("userAccount");
            User changeUser = userRepository.findByUserAccount(userAccount);
            String nowTime = GetTime.GetSecondTime();
            changeUser.setLoginTime(nowTime);
            System.out.println(nowTime);
            ReturnStatus returnStatus = new ReturnStatus("yes", "修改登录时间成功!");
            JSONObject returnJson = new JSONObject();
            returnJson.put("nowTime", nowTime);
            returnStatus.setReturnData(returnJson);
            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }


}
