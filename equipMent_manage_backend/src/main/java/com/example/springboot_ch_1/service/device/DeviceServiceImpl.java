package com.example.springboot_ch_1.service.device;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.config.DeviceStatusPublisher;
import com.example.springboot_ch_1.entity.*;
import com.example.springboot_ch_1.entity.msg.DeviceStatusMsg;
import com.example.springboot_ch_1.repository.*;
import com.example.springboot_ch_1.service.borrowandreturnorder.BorrowAndReturnOrderServiceImpl;
import com.example.springboot_ch_1.util.AllHref;
import com.example.springboot_ch_1.util.GetTime;
import com.example.springboot_ch_1.util.ImageFc;
import com.example.springboot_ch_1.util.ShortIdGenerator;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:06
 * Description:
 */
@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceStatusPublisher publisher;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private BorrowAndReturnOrderRepository borrowAndReturnOrderRepository;
    @Autowired
    private DeviceBorrowRepository deviceBorrowRepository;
    @Autowired
    private UserRootRepository userRootRepository;
    @Autowired
    private BorrowEquipmentCheckRepository borrowEquipmentCheckRepository;


    private JSONObject getAllWaitReturnEquipment(String data) {
        JSONObject getEquipJson = new JSONObject(data);
        System.out.println(data);
        int needCount = getEquipJson.getInt("needCount");
        int nowPage = getEquipJson.getInt("nowPage");
        String userAccount = getEquipJson.getStr("userAccount");


        PageRequest pageRequest = PageRequest.of(nowPage, needCount);
        List<DeviceBorrow> deviceBorrowList = deviceBorrowRepository.findAllByDeviceBorrowUserId(userAccount, pageRequest);

//        加上nowPage
        long totalPages = deviceBorrowRepository.countByDeviceBorrowUserId(userAccount); // 假设count()方法返回总记录数
        long page = ShortIdGenerator.getPage(totalPages, pageRequest);
        List<Device> returnDeviceList = new ArrayList<>();
        for (DeviceBorrow deviceBorrow : deviceBorrowList) {
            String deviceId = deviceBorrow.getDeviceBorrowDeviceId();
            Device device = deviceRepository.findByEquipmentId(deviceId);
            returnDeviceList.add(device);
        }
        JSONObject returnJson = new JSONObject();
        returnJson.set("allPage", page);
        returnJson.set("nowPage", nowPage + 1);
        returnJson.set("tyData", "waitNeedReturn");
        returnJson.set("totalCount", totalPages);
        returnJson.set("deviceList", new JSONArray(returnDeviceList));
        return returnJson;
    }

    @Override
    public ReturnStatus getWaitReturnEquipment(String data) {
        try {
            JSONObject returnJson = getAllWaitReturnEquipment(data);
            ReturnStatus returnStatus = new ReturnStatus("yes", "获取该用户待还设备数据成功!");
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
    public ReturnStatus getDetailEquipmentData(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String equipmentId = getEquipJson.getStr("equipmentId");
            Device returnDeviceInformation = deviceRepository.findByEquipmentId(equipmentId);

            ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
            returnStatus.setReturnData(new JSONObject(returnDeviceInformation));

            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus searchEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String searchInput = getEquipJson.getStr("searchInput");
            String selectedValue = getEquipJson.getStr("selectedValue");

            PageRequest pageRequest = PageRequest.of(nowPage, needCount);
            long totalPages;
            long page;
            List<Device> deviceList;
            System.out.println(searchInput);
            System.out.println(selectedValue);

            JSONObject returnJson = new JSONObject();

            if (searchInput!=null && !searchInput.trim().isEmpty()) {
                switch (selectedValue) {
                    case "equipmentName":
                        deviceList = deviceRepository.findAllByEquipmentNameLikeOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc("%" + searchInput + "%", pageRequest);
                        totalPages = deviceRepository.countByEquipmentNameLikeOrderByEquipmentCreateTimeDesc("%" + searchInput + "%");
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    case "equipmentCode":
                        deviceList = deviceRepository.findAllByEquipmentCodeLikeOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc("%" + searchInput + "%", pageRequest);
                        totalPages = deviceRepository.countByEquipmentCodeLikeOrderByEquipmentCreateTimeDesc("%" + searchInput + "%");
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    case "equipmentLabel":
                        deviceList = deviceRepository.findAllByEquipmentTagLikeOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc("%" + searchInput + "%", pageRequest);
                        totalPages = deviceRepository.countByEquipmentTagLikeOrderByEquipmentCreateTimeDesc("%" + searchInput + "%");
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    default:
                        deviceList = deviceRepository.findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(pageRequest);
                        totalPages = deviceRepository.count();
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                }
                returnJson.set("ifHaveSearch","yes");
                returnJson.set("searchName", searchInput);
                returnJson.set("searchLabel", selectedValue);
            } else {
                deviceList = deviceRepository.findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(pageRequest);
                totalPages = deviceRepository.count();
                page = ShortIdGenerator.getPage(totalPages, pageRequest);
                returnJson.set("ifHaveSearch","no");
                returnJson.set("tyData","equipmentPageData");
            }

            returnJson.set("allPage", page);
            returnJson.set("nowPage", nowPage + 1);
            List<Device> returnList = getModifyDevices(deviceList);
            returnJson.set("deviceList", new JSONArray(returnList));

            ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
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
    public ReturnStatus submitNewEquipmentData(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String equipmentName = getEquipJson.getStr("equipmentName");
            String equipmentCode = getEquipJson.getStr("equipmentCode");
            String equipmentDesc = getEquipJson.getStr("equipmentDesc");
            String equipmentTag = getEquipJson.getStr("equipmentTag");
            String equipmentImg = getEquipJson.getStr("equipmentImg");

            String newEquipmentImgHrefUrl = convertPicHref(equipmentImg);
            if (newEquipmentImgHrefUrl.equals("imgBase64Data-is-empty")) {
                return new ReturnStatus("no", "上传的设备图片异常");
            } else {
                Device device = new Device();
                device.setEquipmentId(ShortIdGenerator.generateTimeId());
                device.setEquipmentTag(equipmentTag);
                device.setEquipmentCode(equipmentCode);
                device.setEquipmentImg(newEquipmentImgHrefUrl);
                device.setEquipmentCreateTime(GetTime.GetSecondTime());
                device.setEquipmentStatus(Device.Status.available);
                device.setEquipmentDesc(equipmentDesc);
                device.setEquipmentName(equipmentName);
                device.setEquipmentOutOrOnStatus(Device.OnOrOutStatus.on);
                deviceRepository.save(device);


                int needCount = getEquipJson.getInt("needCount");
                int nowPage = getEquipJson.getInt("nowPage");

                PageRequest pageRequest = PageRequest.of(nowPage, needCount);
                List<Device> deviceList = deviceRepository.findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(pageRequest);
                long totalPages = deviceRepository.count(); // 假设count()方法返回总记录数
                long page = ShortIdGenerator.getPage(totalPages, pageRequest);

                JSONObject returnJson = new JSONObject();
                returnJson.set("allPage", page);
                returnJson.set("nowPage", nowPage + 1);
                returnJson.set("tyData", "equipmentPageData");
                List<Device> returnList = getModifyDevices(deviceList);
                returnJson.set("deviceList", new JSONArray(returnList));
                ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            }


        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus outEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String equipmentId = getEquipJson.getStr("equipmentId");
            String userAccount = getEquipJson.getStr("userAccount");

            User checkUser = userRepository.findByUserAccount(userAccount);
            Device device = deviceRepository.findByEquipmentId(equipmentId);

            if (checkUser != null && device != null) {
                device.setEquipmentOutOrOnStatus(Device.OnOrOutStatus.out);

                BorrowAndReturnOrder checkBorrow = borrowAndReturnOrderRepository.findByDeviceIdAndUserIdAndOrderTypeAndOpType(equipmentId, userAccount, BorrowAndReturnOrder.OrderType.Continue, "onOrPutProblem");
                if (checkBorrow == null) {
                    String orderId = ShortIdGenerator.generateTimeId();
                    BorrowAndReturnOrder borrowAndReturnOrder = new BorrowAndReturnOrder();
                    borrowAndReturnOrder.setBorrowTime(GetTime.GetSecondTime());
                    borrowAndReturnOrder.setReturnTime("设备下架中...");
                    borrowAndReturnOrder.setOrderId(orderId);
                    borrowAndReturnOrder.setDeviceId(equipmentId);
                    borrowAndReturnOrder.setUserId(userAccount);
                    borrowAndReturnOrder.setUserName(checkUser.getUserName());
                    borrowAndReturnOrder.setDeviceName(device.getEquipmentName());
                    borrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Continue);
                    borrowAndReturnOrder.setDeviceCode(device.getEquipmentCode());
                    borrowAndReturnOrder.setOpType("onOrPutProblem");

                    borrowAndReturnOrderRepository.save(borrowAndReturnOrder);
                    publisher.publish(equipmentId, Device.OnOrOutStatus.out);
                }
                return new ReturnStatus("yes", "设备下架成功!");
            }
            return new ReturnStatus("no", "账户或设备异常!请稍后再试。");
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus onEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String equipmentId = getEquipJson.getStr("equipmentId");
            String userAccount = getEquipJson.getStr("userAccount");

            User checkUser = userRepository.findByUserAccount(userAccount);
            Device device = deviceRepository.findByEquipmentId(equipmentId);

            if (checkUser != null && device != null) {
                device.setEquipmentOutOrOnStatus(Device.OnOrOutStatus.on);

                BorrowAndReturnOrder checkBorrow = borrowAndReturnOrderRepository.findByDeviceIdAndUserIdAndOrderTypeAndOpType(equipmentId, userAccount, BorrowAndReturnOrder.OrderType.Continue, "onOrPutProblem");
                if (checkBorrow != null) {

                    checkBorrow.setReturnTime(GetTime.GetSecondTime());
                    checkBorrow.setOrderType(BorrowAndReturnOrder.OrderType.Finish);

                    borrowAndReturnOrderRepository.save(checkBorrow);
                    publisher.publish(equipmentId, Device.OnOrOutStatus.on);
                }
                return new ReturnStatus("yes", "设备上架成功!");
            }
            return new ReturnStatus("no", "账户或设备异常!请稍后再试。");
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }


    @Override
    public ReturnStatus getAllEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String checkRootAccount = getEquipJson.getStr("checkRootAccount");
            UserRoot checkUserRoot = userRootRepository.findByUserAccount(checkRootAccount);

            if (checkUserRoot != null) {
                int needCount = getEquipJson.getInt("needCount");
                int nowPage = getEquipJson.getInt("nowPage");

                PageRequest pageRequest = PageRequest.of(nowPage, needCount);
                List<Device> deviceList = deviceRepository.findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(pageRequest);
                long totalPages = deviceRepository.count(); // 假设count()方法返回总记录数
                long page = ShortIdGenerator.getPage(totalPages, pageRequest);

                JSONObject returnJson = new JSONObject();
                returnJson.set("allPage", page);
                returnJson.set("nowPage", nowPage + 1);
                returnJson.set("tyData", "equipmentPageData");
                List<Device> returnList = getModifyDevices(deviceList);
                returnJson.set("deviceList", new JSONArray(returnList));
                ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
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

    private static List<Device> getModifyDevices(List<Device> deviceList) {
        List<Device> returnList = new ArrayList<>();
        if (deviceList.size() > 0) {
            for (int i = 0; i < deviceList.size(); i++) {
                Device needDevice = new Device();

                Device device = deviceList.get(i);
                needDevice.setEquipmentId(device.getEquipmentId());
                needDevice.setEquipmentImg(device.getEquipmentImg());
                needDevice.setEquipmentTag(device.getEquipmentTag());
                needDevice.setEquipmentName(device.getEquipmentName());
                needDevice.setEquipmentStatus(device.getEquipmentStatus());
                needDevice.setEquipmentCode(device.getEquipmentCode());
                needDevice.setEquipmentCreateTime(device.getEquipmentCreateTime());
                needDevice.setEquipmentOutOrOnStatus(device.getEquipmentOutOrOnStatus());

                returnList.add(needDevice);
            }
        }
        return returnList;
    }

    public static String convertPicHref(String imgBase64Data) {
        if (!imgBase64Data.equals("") && !imgBase64Data.isEmpty()) {
            String extension = ImageFc.getExtensionFromBase64Data(imgBase64Data);
            String type = ShortIdGenerator.generateShortId() + "." + extension;
            String newHref_local = AllHref.equipment_img_href + type;
            String newHref_url = AllHref.equipment_img_href_url + type;
            byte[] decodedImage = Base64.getDecoder().decode(imgBase64Data);
            BufferedImage compressedImage = ImageFc.compressImage(decodedImage, extension, 0.3, "avatar");
            // 将压缩后的二进制数据保存为图片文件
            try (FileOutputStream fos = new FileOutputStream(newHref_local)) {
                ImageIO.write(compressedImage, extension, fos);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("设备图片的存储路径是: " + newHref_local);
            System.out.println("设备图片返回的网络路径是: " + newHref_url);
            return newHref_url;
        } else {
            return "imgBase64Data-is-empty";
        }

    }


    @Override
    public ReturnStatus doDeleteEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String equipmentId = getEquipJson.getStr("equipmentId");

            Device ifDeviceIsBorrowed = deviceRepository.findByEquipmentId(equipmentId);

            if (ifDeviceIsBorrowed != null && ifDeviceIsBorrowed.getEquipmentStatus() != Device.Status.borrowed) {
                int needCount = getEquipJson.getInt("needCount");
                int nowPage = getEquipJson.getInt("nowPage");

                String userAccount = getEquipJson.getStr("userAccount");
                User user = userRepository.findByUserAccount(userAccount);
                if (user != null) {
                    BorrowAndReturnOrder borrowAndReturnOrder = new BorrowAndReturnOrder();
                    borrowAndReturnOrder.setDeviceId(ifDeviceIsBorrowed.getEquipmentId());
                    borrowAndReturnOrder.setOrderId(ShortIdGenerator.generateTimeId());
                    borrowAndReturnOrder.setUserId(user.getUserAccount());
                    borrowAndReturnOrder.setUserName(user.getUserName());
                    borrowAndReturnOrder.setDeviceName(user.getUserName());
                    borrowAndReturnOrder.setDeviceCode(ifDeviceIsBorrowed.getEquipmentCode());
                    borrowAndReturnOrder.setBorrowTime(GetTime.GetSecondTime());
                    borrowAndReturnOrder.setReturnTime(GetTime.GetSecondTime());
                    borrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Finish);
                    borrowAndReturnOrder.setOpType("deleteEquipment");

                    borrowAndReturnOrderRepository.save(borrowAndReturnOrder);

                    deleteEquipmentImg(ifDeviceIsBorrowed.getEquipmentImg());
                    deviceRepository.delete(ifDeviceIsBorrowed);

                    publisher.publishDeleteEquipment(equipmentId);

                    PageRequest pageRequest = PageRequest.of(nowPage, needCount);
                    List<Device> deviceList = deviceRepository.findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(pageRequest);
                    long totalPages = deviceRepository.count(); // 假设count()方法返回总记录数
                    long page = ShortIdGenerator.getPage(totalPages, pageRequest);

                    JSONObject returnJson = new JSONObject();
                    returnJson.set("allPage", page);
                    returnJson.set("nowPage", nowPage + 1);
                    returnJson.set("tyData", "equipmentPageData");
                    List<Device> returnList = getModifyDevices(deviceList);
                    returnJson.set("deviceList", new JSONArray(returnList));
                    ReturnStatus returnStatus = new ReturnStatus("yes", "设备删除成功");
                    returnStatus.setReturnData(returnJson);
                    return returnStatus;
                } else {
                    return new ReturnStatus("no", "账号异常，请重试");
                }
            } else if (ifDeviceIsBorrowed != null && ifDeviceIsBorrowed.getEquipmentStatus() != Device.Status.available) {
                return new ReturnStatus("no", "当前有用户使用中，请等用户归还后再删除设备");
            } else {
                return new ReturnStatus("yes", "设备删除成功");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus submitEditEquipmentData(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String equipmentId = getEquipJson.getStr("equipmentId");
            String equipmentName = getEquipJson.getStr("equipmentName");
            String equipmentCode = getEquipJson.getStr("equipmentCode");
            String equipmentDesc = getEquipJson.getStr("equipmentDesc");
            String equipmentImg = getEquipJson.getStr("equipmentImg");
            String equipmentTag = getEquipJson.getStr("equipmentTag");

            Device changeEquipment = deviceRepository.findByEquipmentId(equipmentId);

            if (changeEquipment != null) {
                if (!changeEquipment.getEquipmentName().equals(equipmentName)){
                    List<BorrowAndReturnOrder> borrowAndReturnOrderList = borrowAndReturnOrderRepository.findAllByDeviceId(equipmentId);
                    if (!borrowAndReturnOrderList.isEmpty()){
                        for (BorrowAndReturnOrder borrowAndReturnOrder : borrowAndReturnOrderList){
                            borrowAndReturnOrder.setDeviceName(equipmentName);
                        }
                    }
                }

                if (!changeEquipment.getEquipmentCode().equals(equipmentCode)){
                    List<BorrowAndReturnOrder> borrowAndReturnOrderList = borrowAndReturnOrderRepository.findAllByDeviceId(equipmentId);
                    if (!borrowAndReturnOrderList.isEmpty()){
                        for (BorrowAndReturnOrder borrowAndReturnOrder : borrowAndReturnOrderList){
                            borrowAndReturnOrder.setDeviceCode(equipmentCode);
                        }
                    }
                }

                changeEquipment.setEquipmentName(equipmentName);
                changeEquipment.setEquipmentCode(equipmentCode);
                changeEquipment.setEquipmentDesc(equipmentDesc);
                changeEquipment.setEquipmentTag(equipmentTag);

                JSONObject returnJson = new JSONObject();
                String newEquipmentImgHrefUrl = "";
                if (!equipmentImg.equals(changeEquipment.getEquipmentImg())) {
                    newEquipmentImgHrefUrl = convertPicHref(equipmentImg);
                    if (newEquipmentImgHrefUrl.equals("imgBase64Data-is-empty")) {
                        return new ReturnStatus("no", "上传的设备图片异常");
                    } else {
                        deleteEquipmentImg(changeEquipment.getEquipmentImg());
                        changeEquipment.setEquipmentImg(newEquipmentImgHrefUrl);
                    }
                } else {
                    newEquipmentImgHrefUrl = changeEquipment.getEquipmentImg();
                }
                deviceRepository.save(changeEquipment);


                publisher.publishChangeEquipment(equipmentId, equipmentName, equipmentCode, equipmentTag, newEquipmentImgHrefUrl);

                ReturnStatus returnStatus = new ReturnStatus("yes", "修改数据成功");
                returnJson.set("newImgSrc", newEquipmentImgHrefUrl);
                returnStatus.setReturnData(returnJson);

                return returnStatus;
            } else {
                return new ReturnStatus("no", "账号异常，请重试");
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus getAllCheckEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String checkRootAccount = getEquipJson.getStr("checkRootAccount");

            UserRoot userRoot = userRootRepository.findByUserAccount(checkRootAccount);

            if (userRoot != null) {
                PageRequest pageRequest = PageRequest.of(nowPage, needCount);
                List<BorrowEquipmentCheck> checkEquipmentList = borrowEquipmentCheckRepository.findAllByOrderByCheckTimeDesc(pageRequest);

                long totalPages = borrowEquipmentCheckRepository.count(); // 假设count()方法返回总记录数
                long page = ShortIdGenerator.getPage(totalPages, pageRequest);

                JSONObject returnJson = new JSONObject();
                returnJson.set("allPage", page);
                returnJson.set("nowPage", nowPage + 1);
                returnJson.set("tyData", "borrowCheckPageData");
                returnJson.set("checkEquipmentList", getModifyCheckDevices(checkEquipmentList));
                ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
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
    public ReturnStatus agreeEquipmentCheck(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String checkId = getEquipJson.getStr("checkId");

            BorrowEquipmentCheck borrowEquipmentCheck = borrowEquipmentCheckRepository.findByCheckId(checkId);
            if (borrowEquipmentCheck != null) {
                Device checkDevice = deviceRepository.findByEquipmentId(borrowEquipmentCheck.getEquipmentId());
                if (checkDevice.getEquipmentStatus().equals(Device.Status.available)) {
                    //                  调用方法同意工单DeviceProblemType
                    borrowEquipment(borrowEquipmentCheck.getEquipmentId(), borrowEquipmentCheck.getUserId());
                } else {
                    System.out.println("出现了异常数据,这条数据是因为借出后，尚未删除掉审核工单造成的");
                }
                //                  删掉所有借用该Equipment的工单(对于同意借走设备的用户，本来就应该删掉他的借出工单，对于其他所有未能借到设备的用户，该工单相当于被驳回)
                borrowEquipmentCheckRepository.deleteAllByEquipmentId(borrowEquipmentCheck.getEquipmentId());


                PageRequest pageRequest = PageRequest.of(nowPage, needCount);
                List<BorrowEquipmentCheck> checkEquipmentList = borrowEquipmentCheckRepository.findAllByOrderByCheckTimeDesc(pageRequest);

                long totalPages = borrowEquipmentCheckRepository.count(); // 假设count()方法返回总记录数
                long page = ShortIdGenerator.getPage(totalPages, pageRequest);

                JSONObject returnJson = new JSONObject();
                returnJson.set("allPage", page);
                returnJson.set("nowPage", nowPage + 1);
                returnJson.set("tyData", "borrowCheckPageData");
                returnJson.set("checkEquipmentList", getModifyCheckDevices(checkEquipmentList));
                ReturnStatus returnStatus = new ReturnStatus("yes", "驳回申请成功!");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            } else {
                return new ReturnStatus("no", "未检测:相应工单不存在");
            }
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus revokeEquipmentCheck(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String checkId = getEquipJson.getStr("checkId");

            BorrowEquipmentCheck borrowEquipmentCheck = borrowEquipmentCheckRepository.findByCheckId(checkId);

            if (borrowEquipmentCheck != null) {
                borrowEquipmentCheckRepository.delete(borrowEquipmentCheck);
            }

            PageRequest pageRequest = PageRequest.of(nowPage, needCount);
            List<BorrowEquipmentCheck> checkEquipmentList = borrowEquipmentCheckRepository.findAllByOrderByCheckTimeDesc(pageRequest);

            long totalPages = borrowEquipmentCheckRepository.count(); // 假设count()方法返回总记录数
            long page = ShortIdGenerator.getPage(totalPages, pageRequest);

            JSONObject returnJson = new JSONObject();
            returnJson.set("allPage", page);
            returnJson.set("nowPage", nowPage + 1);
            returnJson.set("tyData", "borrowCheckPageData");
            returnJson.set("checkEquipmentList", getModifyCheckDevices(checkEquipmentList));
            ReturnStatus returnStatus = new ReturnStatus("yes", "驳回申请成功!");
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
    public ReturnStatus searchCheck(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            System.out.println("needCount: "+needCount);
            System.out.println("nowPage: "+nowPage);
            String searchInput = getEquipJson.getStr("searchInput");
            String selectedValue = getEquipJson.getStr("selectedValue");

            PageRequest pageRequest = PageRequest.of(nowPage, needCount);
            long totalPages;
            long page;
            List<BorrowEquipmentCheck> deviceList = new ArrayList<>();
            System.out.println(searchInput);
            System.out.println(selectedValue);

            JSONObject returnJson = new JSONObject();
            List<BorrowEquipmentCheck> allBorrowEquipmentCheckList = new ArrayList<>();
            if (searchInput!=null && !searchInput.trim().isEmpty()) {
                switch (selectedValue) {
                    case "userName":
                        List<User> userListAllForUserName = userRepository.findAllByUserNameLikeOrderByUserAccountAsc("%" + searchInput + "%");
                        for (User user: userListAllForUserName){
                            List<BorrowEquipmentCheck> addCheckList = borrowEquipmentCheckRepository.findAllByUserIdOrderByCheckTimeDesc(user.getUserAccount());
                            allBorrowEquipmentCheckList.addAll(addCheckList);
                        }
                        totalPages = allBorrowEquipmentCheckList.size();
                        System.out.println("All.totalPages.count: "+totalPages);
                        // 提取当前页的数据
                        deviceList= ShortIdGenerator.getPaginatedList(allBorrowEquipmentCheckList, pageRequest);
                        System.out.println("Paginated Devices: " + deviceList);

                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    case "equipmentName":
                        List<Device> devicesListAllForEquipmentName = deviceRepository.findAllByEquipmentNameLikeOrderByEquipmentCreateTimeDesc("%" + searchInput + "%");
                        for (Device device : devicesListAllForEquipmentName){
                            List<BorrowEquipmentCheck> addCheckList = borrowEquipmentCheckRepository.findAllByEquipmentIdOrderByCheckTimeDesc(device.getEquipmentId());
                            allBorrowEquipmentCheckList.addAll(addCheckList);
                        }
                        totalPages = allBorrowEquipmentCheckList.size();
                        System.out.println("All.totalPages.count: "+totalPages);
                        // 提取当前页的数据
                        deviceList= ShortIdGenerator.getPaginatedList(allBorrowEquipmentCheckList, pageRequest);
                        System.out.println("Paginated Devices: " + deviceList);

                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    default:
                        deviceList = borrowEquipmentCheckRepository.findAllByOrderByCheckTimeDesc(pageRequest);
                        totalPages = borrowEquipmentCheckRepository.count();
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                }
                returnJson.set("ifHaveSearch","yes");
                returnJson.set("searchName", searchInput);
                returnJson.set("searchLabel", selectedValue);
            } else {
                deviceList = borrowEquipmentCheckRepository.findAllByOrderByCheckTimeDesc(pageRequest);
                totalPages = borrowEquipmentCheckRepository.count();
                page = ShortIdGenerator.getPage(totalPages, pageRequest);
                returnJson.set("ifHaveSearch","no");
                returnJson.set("tyData","borrowCheckPageData");
            }

            returnJson.set("allPage", page);
            returnJson.set("nowPage", nowPage + 1);
            List<BorrowEquipmentCheckDTO> returnList = getModifyCheckDevices(deviceList);
            returnJson.set("checkEquipmentList", new JSONArray(returnList));

            ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
            returnStatus.setReturnData(returnJson);
            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }


    public void borrowEquipment(String equipmentId, String userAccount) {
        User checkUser = userRepository.findByUserAccount(userAccount);
        Device device = deviceRepository.findByEquipmentId(equipmentId);
        if (checkUser != null && device != null) {

            device.setEquipmentStatus(Device.Status.borrowed);

//尽量不使用，排除错误工单用的，就目前的逻辑环节，除非暴力攻击服务器，不然无法产生错误工单
//                BorrowAndReturnOrder borrowAndReturnOrder;
//                List<BorrowAndReturnOrder> borrowAndReturnOrderCheckList = borrowAndReturnOrderRepository.findAllByDeviceIdAndUserIdAndOrderType(device.getEquipmentId(),checkUser.getUserAccount(),BorrowAndReturnOrder.OrderType.Continue);
//                if (!borrowAndReturnOrderCheckList.isEmpty()){
//                    BorrowAndReturnOrder needSaveBorrowAndReturnOrder = borrowAndReturnOrderCheckList.get(0);
//                    needSaveBorrowAndReturnOrder.setBorrowTime(GetTime.GetSecondTime());
//                    borrowAndReturnOrderRepository.save(needSaveBorrowAndReturnOrder);
//
//                    if (borrowAndReturnOrderCheckList.size() >= 2){
//                        for (int i = 1;i<borrowAndReturnOrderCheckList.size();i++){
//                            BorrowAndReturnOrder needDeleteBorrowAndReturnOrder = borrowAndReturnOrderCheckList.get(i);
//                            borrowAndReturnOrderRepository.deleteBorrowAndReturnOrderByOrderId(needDeleteBorrowAndReturnOrder.getOrderId());
//                        }
//                    }
//
//                }else{
//                    borrowAndReturnOrder = new BorrowAndReturnOrder();
//                    borrowAndReturnOrder.setBorrowTime(GetTime.GetSecondTime());
//                    borrowAndReturnOrder.setReturnTime("借出中...");
//                    borrowAndReturnOrder.setOrderId(ShortIdGenerator.generateTimeId());
//                    borrowAndReturnOrder.setDeviceId(equipmentId);
//                    borrowAndReturnOrder.setUserId(userAccount);
//                    borrowAndReturnOrder.setUserName(userName);
//                    borrowAndReturnOrder.setDeviceName(equipName);
//                    borrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Continue);
//                    borrowAndReturnOrder.setDeviceCode(needBorrowDevice.getEquipmentCode());
//                    borrowAndReturnOrder.setOpType("borrowAndReturnProblem");
//                    borrowAndReturnOrderRepository.save(borrowAndReturnOrder);
//                }
            String orderId = ShortIdGenerator.generateTimeId();
            BorrowAndReturnOrder borrowAndReturnOrder = new BorrowAndReturnOrder();
            borrowAndReturnOrder.setBorrowTime(GetTime.GetSecondTime());
            borrowAndReturnOrder.setReturnTime("设备借出中...");

            borrowAndReturnOrder.setOrderId(orderId);
            borrowAndReturnOrder.setDeviceId(equipmentId);
            borrowAndReturnOrder.setUserId(userAccount);
            borrowAndReturnOrder.setUserName(checkUser.getUserName());
            borrowAndReturnOrder.setDeviceName(device.getEquipmentName());
            borrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Continue);
            borrowAndReturnOrder.setDeviceCode(device.getEquipmentCode());
            borrowAndReturnOrder.setOpType("borrowAndReturnProblem");
            borrowAndReturnOrderRepository.save(borrowAndReturnOrder);


            DeviceBorrow deviceBorrow = new DeviceBorrow();

            // 转换为时间戳（毫秒）
            deviceBorrow.setDeviceBorrowDeviceId(equipmentId);
            deviceBorrow.setDeviceBorrowUserId(userAccount);
            deviceBorrow.setOrderId(orderId);
            deviceBorrowRepository.save(deviceBorrow);

            publisher.publishBorrowBorrowStatus(equipmentId, Device.Status.borrowed);
        }
    }

    public void deleteEquipmentImg(String picHref) {
        System.out.println("数据库拿来的删除路径是: " + picHref);
        String deleteSrc = picHref.replace(AllHref.equipment_img_href_url, AllHref.equipment_img_href);
        System.out.println("服务器上实际删除路径是: " + deleteSrc);
        File file = new File(deleteSrc);
        try {
            if (file.delete()) {
                System.out.println("文件删除成功");
            } else {
                System.out.println("文件删除失败");
            }
        } catch (SecurityException e) {
            System.out.println("没有足够的权限来删除文件");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("删除文件时发生错误");
            e.printStackTrace();
        }
    }

    private List<BorrowEquipmentCheckDTO> getModifyCheckDevices(List<BorrowEquipmentCheck> deviceList) {
        List<BorrowEquipmentCheckDTO> returnList = new ArrayList<>();
        if (!deviceList.isEmpty()) {
            for (int i = 0; i < deviceList.size(); i++) {
                BorrowEquipmentCheck borrowEquipmentCheck = deviceList.get(i);
                String equipmentId = borrowEquipmentCheck.getEquipmentId();
                String userAccount = borrowEquipmentCheck.getUserId();
                Device device = deviceRepository.findByEquipmentId(equipmentId);
                User user = userRepository.findByUserAccount(userAccount);

                BorrowEquipmentCheckDTO borrowEquipmentCheckDTO = new BorrowEquipmentCheckDTO();
                borrowEquipmentCheckDTO.setEquipmentCode(device.getEquipmentCode());
                borrowEquipmentCheckDTO.setUserName(user.getUserName());
                borrowEquipmentCheckDTO.setEquipmentImg(device.getEquipmentImg());
                borrowEquipmentCheckDTO.setEquipmentName(device.getEquipmentName());
                borrowEquipmentCheckDTO.setCheckId(borrowEquipmentCheck.getCheckId());
                borrowEquipmentCheckDTO.setEquipmentCheckTime(borrowEquipmentCheck.getCheckTime());
                borrowEquipmentCheckDTO.setEquipmentReason(borrowEquipmentCheck.getEquipmentReason());

                returnList.add(borrowEquipmentCheckDTO);
            }
        }
        return returnList;
    }
}
