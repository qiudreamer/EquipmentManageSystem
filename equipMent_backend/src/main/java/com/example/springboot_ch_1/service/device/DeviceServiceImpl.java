package com.example.springboot_ch_1.service.device;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.config.DeviceStatusPublisher;
import com.example.springboot_ch_1.entity.*;
import com.example.springboot_ch_1.repository.*;
import com.example.springboot_ch_1.service.borrowandreturnorder.BorrowAndReturnOrderServiceImpl;
import com.example.springboot_ch_1.util.GetTime;
import com.example.springboot_ch_1.util.ShortIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    private DeviceProblemRepository deviceProblemRepository;
    @Autowired
    private BorrowAndReturnOrderServiceImpl borrowAndReturnOrderService;
    @Autowired
    private BorrowEquipmentCheckRepository borrowEquipmentCheckRepository;

    @Override
    public ReturnStatus borrowEquipment(String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String equipmentId = dataJson.getStr("equipmentId");
            String userAccount = dataJson.getStr("userAccount");

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
                BorrowAndReturnOrder borrowAndReturnOrder  = new BorrowAndReturnOrder();
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



                publisher.publish(equipmentId, Device.Status.borrowed);
                return new ReturnStatus("yes", "设备归还成功!");
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
    public ReturnStatus returnEquipment(String data) {
        System.out.println(data);
        try {
            JSONObject dataJson = new JSONObject(data);
            String equipmentId = dataJson.getStr("equipmentId");
            String userAccount = dataJson.getStr("userAccount");

            User checkUser = userRepository.findByUserAccount(userAccount);
            Device needReturnDevice = deviceRepository.findByEquipmentId(equipmentId);

            if (checkUser != null && needReturnDevice != null) {

                deviceBorrowRepository.deleteByDeviceBorrowDeviceIdAndDeviceBorrowUserId(equipmentId, userAccount);
                needReturnDevice.setEquipmentStatus(Device.Status.available);

                List<BorrowAndReturnOrder> waitReturnBorrowAndReturnOrderList = borrowAndReturnOrderRepository.findAllByDeviceIdAndUserIdAndOrderTypeAndOpType(equipmentId, userAccount, BorrowAndReturnOrder.OrderType.Continue,"borrowAndReturnProblem");
                if (waitReturnBorrowAndReturnOrderList != null && waitReturnBorrowAndReturnOrderList.size() == 1) {
                    BorrowAndReturnOrder waitReturnBorrowAndReturnOrder = waitReturnBorrowAndReturnOrderList.get(0);
                    waitReturnBorrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Finish);
                    waitReturnBorrowAndReturnOrder.setReturnTime(GetTime.GetSecondTime());
                    borrowAndReturnOrderRepository.save(waitReturnBorrowAndReturnOrder);

                    publisher.publish(equipmentId, Device.Status.available);

                    ReturnStatus returnStatus = new ReturnStatus("yes", "设备归还成功!");
                    JSONObject jsonObject = getAllWaitReturnEquipment(data);

                    int needCount = dataJson.getInt("workOrderPageSize");
                    int nowPage = dataJson.getInt("MineWorkOrderNowPage");

                    jsonObject.set("flushWorkOrder", borrowAndReturnOrderService.getAllWorkOrderData(needCount,nowPage,userAccount));
                    returnStatus.setReturnData(jsonObject);

                    System.out.println(returnStatus);
                    return returnStatus;
                } else if (waitReturnBorrowAndReturnOrderList != null && waitReturnBorrowAndReturnOrderList.size() >= 2) {
                    return new ReturnStatus("no", "设备归还异常,出现错误工单,请联系管理人员");
                } else {
                    publisher.publish(equipmentId, Device.Status.available);

                    ReturnStatus returnStatus = new ReturnStatus("yes", "设备归还成功!");
                    JSONObject jsonObject = getAllWaitReturnEquipment(data);
                    returnStatus.setReturnData(jsonObject);
                    return returnStatus;
                }
            }
            return new ReturnStatus("no", "设备或用户数据异常");
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

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
            JSONObject checkUserAccount = new JSONObject(data);
            String userAccount = checkUserAccount.getStr("userAccount");

            User user = userRepository.findByUserAccount(userAccount);
            if (user !=null){
                ReturnStatus returnStatus = new ReturnStatus("yes", "获取该用户待还设备数据成功!");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            }else {
                return new ReturnStatus("no_user", "用户账号不存在");
            }

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
    public ReturnStatus homeNervousNeed(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String equipmentId = getEquipJson.getStr("equipmentId");
            BorrowAndReturnOrder borrowAndReturnOrder = borrowAndReturnOrderRepository.findByDeviceIdAndOrderType(equipmentId, BorrowAndReturnOrder.OrderType.Continue);

            ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
            returnStatus.setReturnData(new JSONObject(borrowAndReturnOrder));
            System.out.println(returnStatus);
            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }

    }

    @Override
    public ReturnStatus setDialogAboutEquipment(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);
            String equipmentId = getEquipJson.getStr("equipmentId");
            String userAccount = getEquipJson.getStr("userAccount");
            JSONArray reasons = getEquipJson.getJSONArray("reasons");
            String detailedReason = getEquipJson.getStr("detailedReason");

            if (reasons.isEmpty()){
                return new ReturnStatus("no", "请选择问题类型");
            }else{
                DeviceProblem deviceProblemCheck = deviceProblemRepository.findByUserAccountAndEquipmentId(userAccount,equipmentId);
                if (deviceProblemCheck!=null){
                    StringBuilder needProblemLabel = new StringBuilder();

                    for (int i=0;i<reasons.size();i++){
                        String linkStr = "";
                        if (i == reasons.size() - 1){
                            linkStr = reasons.getStr(i);
                        }else{
                            linkStr = reasons.getStr(i)+",";
                        }
                        needProblemLabel.append(linkStr);
                    }

                    deviceProblemCheck.setReasons(needProblemLabel.toString());
                    deviceProblemCheck.setDetailedReason(detailedReason);

                    deviceProblemRepository.save(deviceProblemCheck);


                    ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
                    System.out.println(returnStatus);
                    return returnStatus;
                }else{
                    Device device = deviceRepository.findByEquipmentId(equipmentId);
                    User user = userRepository.findByUserAccount(userAccount);

                    String orderId = ShortIdGenerator.generateTimeId();

                    BorrowAndReturnOrder borrowAndReturnOrder  = new BorrowAndReturnOrder();
                    borrowAndReturnOrder.setBorrowTime(GetTime.GetSecondTime());
                    borrowAndReturnOrder.setReturnTime("设备处理中...");
                    borrowAndReturnOrder.setOrderId(orderId);
                    borrowAndReturnOrder.setDeviceId(equipmentId);
                    borrowAndReturnOrder.setUserId(userAccount);
                    borrowAndReturnOrder.setUserName(user.getUserName());
                    borrowAndReturnOrder.setDeviceName(device.getEquipmentName());
                    borrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Continue);
                    borrowAndReturnOrder.setDeviceCode(device.getEquipmentCode());
                    borrowAndReturnOrder.setOpType("equipmentProblem");
                    borrowAndReturnOrderRepository.save(borrowAndReturnOrder);


                    DeviceProblem deviceProblem = new DeviceProblem();
                    deviceProblem.setProblemId(ShortIdGenerator.generateTimeId());
                    deviceProblem.setUserAccount(userAccount);
                    deviceProblem.setEquipmentId(equipmentId);
                    deviceProblem.setOrderId(orderId);
                    StringBuilder needProblemLabel = new StringBuilder();

                    for (int i=0;i<reasons.size();i++){
                        String linkStr = "";
                        if (i == reasons.size() - 1){
                            linkStr = reasons.getStr(i);
                        }else{
                            linkStr = reasons.getStr(i)+",";
                        }
                        needProblemLabel.append(linkStr);
                    }

                    deviceProblem.setReasons(needProblemLabel.toString());
                    deviceProblem.setDetailedReason(detailedReason);
                    deviceProblemRepository.save(deviceProblem);


                    ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
                    System.out.println(returnStatus);
                    return returnStatus;
                }

            }

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
            if (!searchInput.trim().isEmpty()){
                switch (selectedValue){
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
            }else{
                deviceList = deviceRepository.findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(pageRequest);
                totalPages = deviceRepository.count();
                page = ShortIdGenerator.getPage(totalPages, pageRequest);
            }

            JSONObject returnJson = new JSONObject();
            returnJson.set("allPage", page);
            returnJson.set("nowPage", nowPage + 1);
            List<Device> returnList = getModifyDevices(deviceList);
            returnJson.set("deviceList", new JSONArray(returnList));
            returnJson.set("searchName", searchInput);
            returnJson.set("searchLabel", selectedValue);
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
    public ReturnStatus borrowEquipmentRequest(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            String equipmentId = getEquipJson.getStr("equipmentId");
            String userAccount = getEquipJson.getStr("userAccount");
            String borrowReason = getEquipJson.getStr("borrowReason");
            User user = userRepository.findByUserAccount(userAccount);
            if (user!=null){
                Device device = deviceRepository.findByEquipmentId(equipmentId);
                if (device!=null){
                    BorrowEquipmentCheck checkBorrowEquipmentCheck = borrowEquipmentCheckRepository.findByUserIdAndEquipmentId(userAccount,equipmentId);
                    BorrowEquipmentCheck borrowEquipmentCheck = new BorrowEquipmentCheck();
                    if (checkBorrowEquipmentCheck == null){
                        borrowEquipmentCheck.setCheckId(ShortIdGenerator.generateTimeId());
                        borrowEquipmentCheck.setEquipmentId(equipmentId);
                        borrowEquipmentCheck.setUserId(userAccount);

                        borrowEquipmentCheck.setEquipmentReason(borrowReason);
                        borrowEquipmentCheck.setCheckTime(GetTime.GetSecondTime());

                        borrowEquipmentCheckRepository.save(borrowEquipmentCheck);
                    }else{
                        checkBorrowEquipmentCheck.setEquipmentReason(borrowReason);
                        checkBorrowEquipmentCheck.setCheckTime(GetTime.GetSecondTime());

                        borrowEquipmentCheckRepository.save(checkBorrowEquipmentCheck);
                    }

                    return new ReturnStatus("yes", "申请成功!");
                }else{
                    return new ReturnStatus("no", "设备不存在!");
                }
            }else{
                return new ReturnStatus("no", "用户不存在!");
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
            String userAccount = getEquipJson.getStr("userAccount");
            User user = userRepository.findByUserAccount(userAccount);

            if (user!=null){
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
            }else{
                return new ReturnStatus("no_user", "用户不存在!");
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
            String userAccount = getEquipJson.getStr("userAccount");
            String checkId = getEquipJson.getStr("checkId");

            User user = userRepository.findByUserAccount(userAccount);
            if (user!=null){
                BorrowEquipmentCheck borrowEquipmentCheck = borrowEquipmentCheckRepository.findByCheckId(checkId);

                if (borrowEquipmentCheck!=null){
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
                ReturnStatus returnStatus = new ReturnStatus("yes", "撤销申请成功!");
                returnStatus.setReturnData(returnJson);
                return returnStatus;
            }else{
                return new ReturnStatus("no_user", "用户不存在!");
            }
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

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String userAccount = getEquipJson.getStr("userAccount");
            User user = userRepository.findByUserAccount(userAccount);
            if (user!=null){
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
            }else{
                return new ReturnStatus("no_user", "没有用户!");
            }


        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }

    }

    private List<BorrowEquipmentCheckDTO> getModifyCheckDevices(List<BorrowEquipmentCheck> deviceList){
        List<BorrowEquipmentCheckDTO> returnList= new ArrayList<>();
        if (!deviceList.isEmpty()){
            for (int i=0;i<deviceList.size();i++){
                BorrowEquipmentCheck borrowEquipmentCheck = deviceList.get(i);
                String equipmentId = borrowEquipmentCheck.getEquipmentId();
                Device device = deviceRepository.findByEquipmentId(equipmentId);

                BorrowEquipmentCheckDTO borrowEquipmentCheckDTO = new BorrowEquipmentCheckDTO();
                borrowEquipmentCheckDTO.setEquipmentCode(device.getEquipmentCode());
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
    private static List<Device> getModifyDevices(List<Device> deviceList) {
        List<Device> returnList = new ArrayList<>();
        if (!deviceList.isEmpty()) {
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


}
