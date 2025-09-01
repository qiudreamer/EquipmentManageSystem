package com.example.springboot_ch_1.service.borrowandreturnorder;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.*;
import com.example.springboot_ch_1.repository.BorrowAndReturnOrderRepository;
import com.example.springboot_ch_1.repository.DeviceProblemRepository;
import com.example.springboot_ch_1.repository.DeviceRepository;
import com.example.springboot_ch_1.repository.UserRootRepository;
import com.example.springboot_ch_1.util.GetTime;
import com.example.springboot_ch_1.util.ShortIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
public class BorrowAndReturnOrderServiceImpl implements BorrowAndReturnOrderService {

    @Autowired
    private BorrowAndReturnOrderRepository borrowAndReturnOrderRepository;
    @Autowired
    private DeviceProblemRepository deviceProblemRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private UserRootRepository userRootRepository;

    @Override
    public ReturnStatus getAllWorkOrder(String data) {
        try {
            JSONObject borrowData = new JSONObject(data);
            String checkRootAccount = borrowData.getStr("checkRootAccount");
            UserRoot checkUserRoot = userRootRepository.findByUserAccount(checkRootAccount);

            if (checkUserRoot!=null) {
                userRootRepository.findByUserAccount(checkRootAccount);
                int needCount = borrowData.getInt("needCount");
                int nowPage = borrowData.getInt("nowPage");
                return getAllWorkOrderData(needCount, nowPage);
            }else{
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
    public ReturnStatus handleOrderTypeAboutEquipment(String data) {
        try {
            JSONObject borrowData = new JSONObject(data);

            int needCount = borrowData.getInt("needCount");
            int nowPage = borrowData.getInt("nowPage");
            String userAccount = borrowData.getStr("userAccount");
            String equipmentId = borrowData.getStr("equipmentId");

            List<BorrowAndReturnOrder> borrowAndReturnOrder = borrowAndReturnOrderRepository.findAllByDeviceIdAndUserIdAndOrderTypeAndOpType(equipmentId, userAccount, BorrowAndReturnOrder.OrderType.Continue,"equipmentProblem");
            if (borrowAndReturnOrder != null && borrowAndReturnOrder.size() == 1) {

                BorrowAndReturnOrder waitReturnBorrowAndReturnOrder = borrowAndReturnOrder.get(0);
                waitReturnBorrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Finish);
                waitReturnBorrowAndReturnOrder.setReturnTime(GetTime.GetSecondTime());

                borrowAndReturnOrderRepository.save(waitReturnBorrowAndReturnOrder);
                deviceProblemRepository.deleteByUserAccountAndEquipmentId(userAccount,equipmentId);
                ReturnStatus returnStatus = getAllWorkOrderData(needCount, nowPage);
                returnStatus.setReason("设备异常工单处理成功!");
                return returnStatus;
            }else if (borrowAndReturnOrder != null && borrowAndReturnOrder.size() >= 2) {
                return new ReturnStatus("no", "工单处理异常,出现错误工单,请排查");
            } else {
                deviceProblemRepository.deleteByUserAccountAndEquipmentId(userAccount,equipmentId);

                ReturnStatus returnStatus = getAllWorkOrderData(needCount, nowPage);
                returnStatus.setReason("设备异常工单处理成功!");
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
    public ReturnStatus doMineOrderTypeAboutEquipment(String data) {
        try {
            JSONObject getJson = new JSONObject(data);
            String orderId = getJson.getStr("orderId");
            BorrowAndReturnOrder borrowAndReturnOrder = borrowAndReturnOrderRepository.findByOrderId(orderId);
            if(borrowAndReturnOrder.getOrderType() == BorrowAndReturnOrder.OrderType.Continue){
                borrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Finish);
                borrowAndReturnOrder.setReturnTime(GetTime.GetSecondTime());
                deviceProblemRepository.deleteByUserAccountAndEquipmentId(borrowAndReturnOrder.getUserId(),borrowAndReturnOrder.getDeviceId());
            }
            ReturnStatus returnStatus = new ReturnStatus("yes","处理工单成功!");
            returnStatus.setReturnData(new JSONObject(borrowAndReturnOrder));
            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus showOrderData(String data) {
        try {
            ReturnStatus returnStatus = new ReturnStatus("yes", "获取详细工单成功!");
            BorrowAndReturnOrder borrowAndReturnOrder = borrowAndReturnOrderRepository.findByOrderId(new JSONObject(data).getStr("orderId"));
            Device device = deviceRepository.findByEquipmentId(borrowAndReturnOrder.getDeviceId());
            JSONObject setJson = new JSONObject();
            if (borrowAndReturnOrder.getOpType().equals("equipmentProblem")){
                DeviceProblem deviceProblem = deviceProblemRepository.findByUserAccountAndEquipmentId(borrowAndReturnOrder.getUserId(), borrowAndReturnOrder.getDeviceId());
                if (deviceProblem!=null){
                    setJson.set("deviceProblemData",deviceProblem);
                    setJson.set("ifDeviceProblemType","yes");
                }else{
                    setJson.set("ifDeviceProblemType","no");
                }
            }else{
                setJson.set("ifDeviceProblemType","no");
            }
            setJson.set("orderData",borrowAndReturnOrder);
            setJson.set("deviceData",device);
            returnStatus.setReturnData(setJson);
            return returnStatus;
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus searchOrder(String data) {
        try {
            JSONObject getEquipJson = new JSONObject(data);

            int needCount = getEquipJson.getInt("needCount");
            int nowPage = getEquipJson.getInt("nowPage");
            String searchInput = getEquipJson.getStr("searchInput");
            String searchType = getEquipJson.getStr("searchType");
            String searchOrderType = getEquipJson.getStr("searchOrderType");

            PageRequest pageRequest = PageRequest.of(nowPage, needCount);

            long totalPages;
            long page;
            List<BorrowAndReturnOrder> userBorrowData;

            System.out.println("searchInput: "+searchInput);
            System.out.println("searchType: "+searchType);
            System.out.println("searchOrderType: "+searchOrderType);

            JSONObject returnJson = new JSONObject();

            if (searchInput!=null && !searchInput.trim().isEmpty()) {
                switch (searchType) {
                    case "userName":
                        if (searchOrderType.equals("allSearchOrderType")) {
                            userBorrowData = borrowAndReturnOrderRepository.findAllByUserNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc("%" + searchInput + "%", pageRequest);
                            totalPages = borrowAndReturnOrderRepository.countByUserNameLike("%" + searchInput + "%");
                        }else{
                            userBorrowData = borrowAndReturnOrderRepository.findAllByOpTypeAndUserNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(searchOrderType,"%" + searchInput + "%", pageRequest);
                            totalPages = borrowAndReturnOrderRepository.countByOpTypeAndUserNameLike(searchOrderType,"%" + searchInput + "%");
                        }
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    case "equipmentName":
                        if (searchOrderType.equals("allSearchOrderType")) {
                            userBorrowData = borrowAndReturnOrderRepository.findAllByDeviceNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc("%" + searchInput + "%", pageRequest);
                            totalPages = borrowAndReturnOrderRepository.countByDeviceNameLike("%" + searchInput + "%");
                        }else{
                            userBorrowData = borrowAndReturnOrderRepository.findAllByOpTypeAndDeviceNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(searchOrderType,"%" + searchInput + "%", pageRequest);
                            totalPages = borrowAndReturnOrderRepository.countByOpTypeAndDeviceNameLike(searchOrderType,"%" + searchInput + "%");
                        }
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                    default:
                        if (searchOrderType.equals("allSearchOrderType")) {
                            userBorrowData = borrowAndReturnOrderRepository.findAllByOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(pageRequest);
                            totalPages = borrowAndReturnOrderRepository.count();
                            System.out.println("userBorrowData：  "+userBorrowData);
                            System.out.println("totalPages：  "+totalPages);
                        }else{
                            userBorrowData = borrowAndReturnOrderRepository.findAllByOpTypeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(searchOrderType, pageRequest);
                            totalPages = borrowAndReturnOrderRepository.countByOpType(searchOrderType);
                        }
                        page = ShortIdGenerator.getPage(totalPages, pageRequest);
                        break;
                }
                returnJson.set("ifHaveSearch","yes");
                returnJson.set("searchName", searchInput);

                JSONObject orderJson = new JSONObject();

                orderJson.set("searchType",searchType);
                orderJson.set("searchOrderType",searchOrderType);

                returnJson.set("searchLabel", orderJson.toString());

            } else {
                if (searchOrderType.equals("allSearchOrderType")) {
                    userBorrowData = borrowAndReturnOrderRepository.findAllByOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(pageRequest);
                    totalPages = borrowAndReturnOrderRepository.count();
                }else{
                    userBorrowData = borrowAndReturnOrderRepository.findAllByOpTypeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(searchOrderType, pageRequest);
                    totalPages = borrowAndReturnOrderRepository.countByOpType(searchOrderType);
                }
                page = ShortIdGenerator.getPage(totalPages, pageRequest);
                returnJson.set("ifHaveSearch","no");
                returnJson.set("tyData","orderPageData");
            }

            returnJson.set("allPage", page);
            returnJson.set("nowPage", nowPage + 1);
            returnJson.set("userBorrowData", userBorrowData);

            ReturnStatus returnStatus = new ReturnStatus("yes", "获取成功");
            returnStatus.setReturnData(returnJson);
            System.out.println(returnStatus);
            return returnStatus;

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    public ReturnStatus getAllWorkOrderData(int needCount, int nowPage) {

        PageRequest pageRequest = PageRequest.of(nowPage, needCount);

        List<BorrowAndReturnOrder> list = borrowAndReturnOrderRepository.findAllByOrderByOrderTypeAscOpTypeDescBorrowTimeDesc( pageRequest);

        long totalPages = borrowAndReturnOrderRepository.count(); // 假设count()方法返回总记录数
        long page = ShortIdGenerator.getPage(totalPages, pageRequest);

        JSONObject returnJson = new JSONObject();
        returnJson.set("userBorrowData", new JSONArray(list));
        returnJson.set("allPage", page);
        returnJson.set("nowPage", nowPage + 1);
        returnJson.set("tyData", "workOrder");

        ReturnStatus returnStatus = new ReturnStatus("yes", "获取用户工单数据成功!");
        returnStatus.setReturnData(returnJson);
        return returnStatus;

    }
}
