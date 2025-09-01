package com.example.springboot_ch_1.service.userroot;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.config.DeviceStatusPublisher;
import com.example.springboot_ch_1.entity.*;
import com.example.springboot_ch_1.repository.DeviceBorrowRepository;
import com.example.springboot_ch_1.repository.UserRepository;
import com.example.springboot_ch_1.repository.UserRootRepository;
import com.example.springboot_ch_1.util.AllHref;
import com.example.springboot_ch_1.util.GetTime;
import com.example.springboot_ch_1.util.ShortIdGenerator;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 5:22
 * Description:
 */
@Service
@Transactional
public class UserRootServiceImpl implements UserRootService {
    @Autowired
    private UserRootRepository userRootRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceBorrowRepository deviceBorrowRepository;
    @Autowired
    private DeviceStatusPublisher publisher;

    @Override
    public ReturnStatus login(String data) {
        try {
            // 1. 解析 JSON
            JSONObject getDataJson = new JSONObject(data);
            System.out.println(getDataJson);
            String userAccount = getDataJson.getStr("userAccount");
            String userPassword = getDataJson.getStr("userPassword");

            // 2. 查询用户
            UserRoot checkUser = userRootRepository.findByUserAccountAndUserPassword(userAccount, userPassword);

            // 3. 返回登录结果
            if (checkUser != null) {
                ReturnStatus returnStatus = new ReturnStatus("yes", "登录成功!");
                JSONObject returnJson = new JSONObject();
                returnJson.set("userName", checkUser.getUserName());
                returnJson.set("userAccount", checkUser.getUserAccount());
                returnJson.set("rootType", checkUser.getRootType());
                if (checkUser.getLoginTime() == null) {
                    String time = GetTime.GetSecondTime();
                    checkUser.setLoginTime(time);
                    userRootRepository.save(checkUser);
                    returnJson.set("loginTime", time);
                } else {
                    returnJson.set("loginTime", checkUser.getLoginTime());
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
    public ReturnStatus changeLoginTime(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String userAccount = jsonObject.getStr("userAccount");
            UserRoot changeUser = userRootRepository.findByUserAccount(userAccount);
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

    @Override
    public ReturnStatus getAllUser(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String checkRootAccount = getEquipJson.getStr("checkRootAccount");
            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");

            UserRoot userRoot = userRootRepository.findByUserAccount(checkRootAccount);

            if (userRoot != null) {
                JSONObject returnJson = new JSONObject();
                PageRequest pageRequest = PageRequest.of(nowPage, needCount);
                long totalPages;
                long page;
                List<User> userList;
                if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                    userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name), pageRequest);
                    totalPages = userRepository.countByRootTypeNotIn(Arrays.asList(AllHref.super_steward_root_name));
                    page = ShortIdGenerator.getPage(totalPages, pageRequest);
                    returnJson.set("rootType", "yes");
                    returnJson.set("allPage", page);
                    returnJson.set("nowPage", nowPage + 1);
                    returnJson.set("tyData", "userPageData");
                    returnJson.set("userList", getModifyUserListSuperSteward(userList));
                } else if (userRoot.getRootType().equals(AllHref.steward_root_name)) {
                    userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name, AllHref.steward_root_name), pageRequest);
                    totalPages = userRepository.countByRootTypeNotIn(Arrays.asList(AllHref.super_steward_root_name, AllHref.steward_root_name));
                    page = ShortIdGenerator.getPage(totalPages, pageRequest);
                    returnJson.set("rootType", "no");
                    returnJson.set("allPage", page);
                    returnJson.set("nowPage", nowPage + 1);
                    returnJson.set("tyData", "userPageData");
                    returnJson.set("userList", getModifyUserListSteward(userList));
                } else {
                    return new ReturnStatus("no", "获取用户数据失败!");
                }

                ReturnStatus returnStatus = new ReturnStatus("yes", "获取用户数据成功!");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            } else {
                return new ReturnStatus("kill", "当前没有管理员权限!");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus revokeAdmin(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String userAccount = getEquipJson.getStr("userAccount");
            String setAdminAccount = getEquipJson.getStr("setAdminAccount");

            UserRoot userRoot = userRootRepository.findByUserAccount(userAccount);
            if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                User user = userRepository.findByUserAccount(setAdminAccount);
                user.setRootType(AllHref.user_root_name);

                UserRoot needKillRootAccount = userRootRepository.findByUserAccount(setAdminAccount);
                userRootRepository.delete(needKillRootAccount);

                ReturnStatus returnStatus = new ReturnStatus("yes", "取消管理员权限成功");
                JSONObject returnJson = new JSONObject();
                returnJson.set("userAccount", setAdminAccount);
                returnStatus.setReturnData(returnJson);

                publisher.publishStewardStatus(setAdminAccount);

                return returnStatus;
            } else {
                return new ReturnStatus("no", "您大人有大量，别攻击我们的服务器了qaq");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus setAdmin(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String userAccount = getEquipJson.getStr("userAccount");
            String setAdminAccount = getEquipJson.getStr("setAdminAccount");

            UserRoot userRoot = userRootRepository.findByUserAccount(userAccount);
            if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                List<UserRoot> checkRootCount = userRootRepository.findAllByRootType(AllHref.steward_root_name);
                if (checkRootCount.size() < AllHref.max_steward_count) {
                    User user = userRepository.findByUserAccount(setAdminAccount);
                    user.setRootType(AllHref.steward_root_name);

                    UserRoot needAddRootAccount = new UserRoot();

                    needAddRootAccount.setRootType(AllHref.steward_root_name);
                    needAddRootAccount.setLoginTime("");
                    needAddRootAccount.setUserAccount(user.getUserAccount());
                    needAddRootAccount.setUserPassword(user.getPassword());
                    needAddRootAccount.setUserName(user.getUserName());

                    userRootRepository.save(needAddRootAccount);

                    ReturnStatus returnStatus = new ReturnStatus("yes", "设置管理员权限成功");
                    JSONObject returnJson = new JSONObject();
                    returnJson.set("userAccount", setAdminAccount);
                    returnStatus.setReturnData(returnJson);
                    return returnStatus;
                } else {
                    return new ReturnStatus("bo", "最多设置" + AllHref.max_steward_count + "位管理员");
                }

            } else {
                return new ReturnStatus("no", "您大人有大量，别攻击我们的服务器了qaq");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus viewPwd(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String userAccount = getEquipJson.getStr("userAccount");
            String getPasswordAccount = getEquipJson.getStr("getPasswordAccount");

            UserRoot userRoot = userRootRepository.findByUserAccount(userAccount);
            if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                User user = userRepository.findByUserAccount(getPasswordAccount);

                ReturnStatus returnStatus = new ReturnStatus("yes", "查询密码成功");
                JSONObject returnJson = new JSONObject();
                returnJson.set("password", user.getPassword());
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            } else {
                return new ReturnStatus("no", "您大人有大量，别攻击我们的服务器了qaq");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
//  三点：
//  第一是检测该账号有没有未归还的设备，如果有，则还不能删除
//  第二是检测是否是管理员，如果是，删除的时候同时时删除管理员账号，并发送socket到后台的后端，同时，发送socket到页面后端，双端登出账号。
//  第三是如果不是管理员，直接删除账号即可
    public ReturnStatus delUser(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
//          操作人
            String userAccount = getEquipJson.getStr("userAccount");
//          被操作的
            String getPasswordAccount = getEquipJson.getStr("getPasswordAccount");

            long deviceBorrowListSize = deviceBorrowRepository.countAllByDeviceBorrowUserId(getPasswordAccount);
            System.out.println("需要用户被删除时，当前账号: " + getPasswordAccount + ",借出的设备数量为: " + deviceBorrowListSize);
            if (deviceBorrowListSize == 0) {
                UserRoot userRoot = userRootRepository.findByUserAccount(userAccount);
                if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                    User user = userRepository.findByUserAccount(getPasswordAccount);
                    if (user != null) {
                        if (Objects.equals(user.getRootType(), AllHref.steward_root_name)) {
                            UserRoot needDeleteRoot = userRootRepository.findByUserAccount(getPasswordAccount);
                            if (needDeleteRoot != null) {
                                userRootRepository.delete(needDeleteRoot);
                            }
                            userRepository.delete(user);
                            //                      管理员前端通告
                            publisher.publishStewardStatus(getPasswordAccount);
                            //                      服务端前端通告
                            publisher.publishUserStatus(getPasswordAccount);
                            return new ReturnStatus("yes", "删除账号成功");
                        } else if (Objects.equals(user.getRootType(), AllHref.super_steward_root_name)) {
                            return new ReturnStatus("no", "权限越级，您无权删除");
                        } else {
                            userRepository.delete(user);
                            //                      服务端前端通告
                            publisher.publishUserStatus(getPasswordAccount);
                            return new ReturnStatus("yes", "删除账号成功");
                        }

                    } else {
                        //                      管理员前端通告
                        publisher.publishStewardStatus(getPasswordAccount);
                        //                      服务端前端通告
                        publisher.publishUserStatus(getPasswordAccount);
                        return new ReturnStatus("yes", "删除账号成功");
                    }
                } else if (userRoot.getRootType().equals(AllHref.steward_root_name)) {
                    User user = userRepository.findByUserAccount(getPasswordAccount);
                    if (user != null) {
                        if (Objects.equals(user.getRootType(), AllHref.steward_root_name)) {
                            return new ReturnStatus("no", "权限平级，您无权删除");
                        } else if (Objects.equals(user.getRootType(), AllHref.super_steward_root_name)) {
                            return new ReturnStatus("no", "权限越级，您无权删除");
                        } else {
                            userRepository.delete(user);
                            //                      服务端前端通告
                            publisher.publishUserStatus(getPasswordAccount);
                            return new ReturnStatus("yes", "删除账号成功");
                        }
                    } else {
                        //                      服务端前端通告
                        publisher.publishUserStatus(getPasswordAccount);
                        return new ReturnStatus("yes", "删除账号成功");
                    }
                } else {
                    return new ReturnStatus("no", "您大人有大量，别攻击我们的服务器了qaq");
                }
            } else {
                return new ReturnStatus("no", "当前用户有未归还的设备，请等归还后再删除账号");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus searchUser(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String searchInput = getEquipJson.getStr("searchInput");
            String selectedValue = getEquipJson.getStr("selectedValue");
            String checkUserAccount = getEquipJson.getStr("checkUserAccount");

            PageRequest pageRequest = PageRequest.of(nowPage, needCount);
            long totalPages;
            long page;
            List<User> userList;
            System.out.println(searchInput);
            System.out.println(selectedValue);
            System.out.println("checkUserAccount: " + checkUserAccount);
            UserRoot userRoot = userRootRepository.findByUserAccount(checkUserAccount);

            JSONObject returnJson = new JSONObject();

            if (userRoot != null) {
                if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                    returnJson.set("rootType", "yes");
                } else {
                    returnJson.set("rootType", "no");
                }
            }


            if (userRoot != null && userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                if (!searchInput.trim().isEmpty()) {
                    switch (selectedValue) {
                        case "userName":
                            userList = userRepository.findAllByUserNameLikeAndRootTypeNotInOrderByRootTypeDescUserAccountAsc("%" + searchInput + "%", Arrays.asList(AllHref.super_steward_root_name), pageRequest);
                            totalPages = userRepository.countByUserNameLikeAndRootTypeNotIn("%" + searchInput + "%", Arrays.asList(AllHref.super_steward_root_name));
                            page = ShortIdGenerator.getPage(totalPages, pageRequest);
                            break;
                        case "userAccount":
                            userList = userRepository.findAllByUserAccountLikeAndRootTypeNotInOrderByRootTypeDescUserAccountAsc("%" + searchInput + "%", Arrays.asList(AllHref.super_steward_root_name), pageRequest);
                            totalPages = userRepository.countByUserAccountLikeAndRootTypeNotIn("%" + searchInput + "%", Arrays.asList(AllHref.super_steward_root_name));
                            page = ShortIdGenerator.getPage(totalPages, pageRequest);
                            break;
                        default:
                            userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name), pageRequest);
                            totalPages = userRepository.count();
                            page = ShortIdGenerator.getPage(totalPages, pageRequest);
                            break;
                    }
                    returnJson.set("ifHaveSearch", "yes");
                    returnJson.set("searchName", searchInput);
                    returnJson.set("searchLabel", selectedValue);
                } else {
                    userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name), pageRequest);
                    totalPages = userRepository.count();
                    page = ShortIdGenerator.getPage(totalPages, pageRequest);

                    returnJson.set("ifHaveSearch", "no");
                    returnJson.set("tyData", "userPageData");
                }


                returnJson.set("allPage", page);
                returnJson.set("nowPage", nowPage + 1);
                List<User> returnList = getModifyUserListSuperSteward(userList);
                returnJson.set("userList", new JSONArray(returnList));

                ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            } else if (userRoot != null && userRoot.getRootType().equals(AllHref.steward_root_name)) {
                if (!searchInput.trim().isEmpty()) {
                    switch (selectedValue) {
                        case "userName":
                            userList = userRepository.findAllByUserNameLikeAndRootTypeNotInOrderByRootTypeDescUserAccountAsc("%" + searchInput + "%", Arrays.asList(AllHref.steward_root_name, AllHref.super_steward_root_name), pageRequest);
                            totalPages = userRepository.countByUserNameLikeAndRootTypeNotIn("%" + searchInput + "%", Arrays.asList(AllHref.steward_root_name, AllHref.super_steward_root_name));
                            page = ShortIdGenerator.getPage(totalPages, pageRequest);
                            break;
                        case "userAccount":
                            userList = userRepository.findAllByUserAccountLikeAndRootTypeNotInOrderByRootTypeDescUserAccountAsc("%" + searchInput + "%", Arrays.asList(AllHref.steward_root_name, AllHref.super_steward_root_name), pageRequest);
                            totalPages = userRepository.countByUserAccountLikeAndRootTypeNotIn("%" + searchInput + "%", Arrays.asList(AllHref.steward_root_name, AllHref.super_steward_root_name));
                            page = ShortIdGenerator.getPage(totalPages, pageRequest);
                            break;
                        default:
                            userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name, AllHref.steward_root_name), pageRequest);
                            totalPages = userRepository.count();
                            page = ShortIdGenerator.getPage(totalPages, pageRequest);
                            break;

                    }

                    returnJson.set("ifHaveSearch", "yes");
                    returnJson.set("searchName", searchInput);
                    returnJson.set("searchLabel", selectedValue);
                } else {
                    userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name, AllHref.steward_root_name), pageRequest);
                    totalPages = userRepository.count();
                    page = ShortIdGenerator.getPage(totalPages, pageRequest);

                    returnJson.set("ifHaveSearch", "no");
                    returnJson.set("tyData", "userPageData");
                }


                returnJson.set("allPage", page);
                returnJson.set("nowPage", nowPage + 1);
                List<User> returnList = getModifyUserListSuperSteward(userList);
                returnJson.set("userList", new JSONArray(returnList));

                ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            } else {
                return new ReturnStatus("no", "获取权限异常，您当前无权限");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }

    }


    @Override
    public ReturnStatus getPinYin(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String userName = getEquipJson.getStr("userName");
            if (userName == null || userName.trim().isEmpty()) {
                return new ReturnStatus("no", "用户名不能为空");
            }

            StringBuilder sb = new StringBuilder();
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 无音调

            for (char c : userName.toCharArray()) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]")) { // 汉字
                    try {
                        sb.append(PinyinHelper.toHanyuPinyinStringArray(c, format)[0]);
                    } catch (Exception e) {
                        sb.append(c); // 异常时保留原字符
                    }
                } else {
                    sb.append(c); // 非汉字保留
                }
            }
            System.out.println("新建用户的名称拼音为: " + sb);
            JSONObject returnJson = new JSONObject();
            returnJson.set("userAccount", sb);
            returnJson.set("userPassword", sb.toString() + 123);
            System.out.println(returnJson);
            ReturnStatus returnStatus = new ReturnStatus("yes", "拼音获取成功!");
            returnStatus.setReturnData(returnJson);
            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus submitNewUserData(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String userName = getEquipJson.getStr("userName");
            String userAccount = getEquipJson.getStr("userAccount");
            String userPassword = getEquipJson.getStr("userPassword");
            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String rootUserAccount = getEquipJson.getStr("rootUserAccount");

            UserRoot userRoot = userRootRepository.findByUserAccount(rootUserAccount);

            PageRequest pageRequest = PageRequest.of(nowPage, needCount);

            User user = userRepository.findByUserAccount(userAccount);
            if (user == null && userRoot != null) {
                User addUser = new User();
                addUser.setUserName(userName);
                addUser.setUserAccount(userAccount);
                addUser.setPassword(userPassword);
                addUser.setRootType(AllHref.user_root_name);
                addUser.setLoginTime("none");
                userRepository.save(addUser);

                System.out.println(userRoot);

                JSONObject returnJson = new JSONObject();
                long totalPages;
                long page;
                List<User> userList;
                if (userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                    userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.super_steward_root_name), pageRequest);
                    totalPages = userRepository.countByRootTypeNotIn(Arrays.asList(AllHref.super_steward_root_name));
                    page = ShortIdGenerator.getPage(totalPages, pageRequest);

                    returnJson.set("rootType", "yes");
                    returnJson.set("allPage", page);
                    returnJson.set("nowPage", nowPage + 1);
                    returnJson.set("userList", getModifyUserListSuperSteward(userList));
                } else if (userRoot.getRootType().equals(AllHref.steward_root_name)) {
                    userList = userRepository.findAllByRootTypeNotInOrderByRootTypeDescUserAccountAsc(Arrays.asList(AllHref.steward_root_name, AllHref.super_steward_root_name), pageRequest);
                    totalPages = userRepository.countByRootTypeNotIn(Arrays.asList(AllHref.steward_root_name, AllHref.super_steward_root_name));
                    page = ShortIdGenerator.getPage(totalPages, pageRequest);

                    returnJson.set("rootType", "no");
                    returnJson.set("allPage", page);
                    returnJson.set("nowPage", nowPage + 1);
                    returnJson.set("userList", getModifyUserListSteward(userList));
                } else {
                    return new ReturnStatus("no", "添加新用户失败!");
                }

                ReturnStatus returnStatus = new ReturnStatus("yes", "添加新用户成功!");
                returnStatus.setReturnData(returnJson);
                return returnStatus;

            } else if (user != null) {
                return new ReturnStatus("no", "该用户已经存在了!");
            } else {
                return new ReturnStatus("no", "当前账号没有权限!");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    private List<User> getModifyUserListSuperSteward(List<User> list) {
        List<User> returnList = new ArrayList<>();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                User needUser = new User();
                User user = list.get(i);
                needUser.setUserAccount(user.getUserAccount());
                needUser.setUserName(user.getUserName());
                needUser.setLoginTime(user.getLoginTime());
                UserRoot userRoot = userRootRepository.findByUserAccount(user.getUserAccount());
                if (userRoot != null && userRoot.getRootType().equals(AllHref.steward_root_name)) {
                    needUser.setRootType("yes");
                    returnList.add(needUser);
                } else if (userRoot != null && userRoot.getRootType().equals(AllHref.super_steward_root_name)) {
                    System.out.println("超管被放到数据里了");
                } else {
                    needUser.setRootType("no_root");
                    returnList.add(needUser);
                }

            }
        }
        return returnList;
    }

    private List<User> getModifyUserListSteward(List<User> list) {
        List<User> returnList = new ArrayList<>();

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                User needUser = new User();
                User user = list.get(i);
                needUser.setUserAccount(user.getUserAccount());
                needUser.setUserName(user.getUserName());
                needUser.setLoginTime(user.getLoginTime());
                returnList.add(needUser);
            }
        }
        return returnList;
    }

}
