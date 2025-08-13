package com.example.springboot_ch_1.service.borrowandreturnorder;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.BorrowAndReturnOrder;
import com.example.springboot_ch_1.entity.Device;
import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.repository.BorrowAndReturnOrderRepository;
import com.example.springboot_ch_1.repository.DeviceProblemRepository;
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

    @Override
    public ReturnStatus getAllWorkOrder(String data) {
        try {
            JSONObject borrowData = new JSONObject(data);

            int needCount = borrowData.getInt("needCount");
            int nowPage = borrowData.getInt("nowPage");
            String userAccount = borrowData.getStr("userAccount");
            return getAllWorkOrderData(needCount, nowPage, userAccount);
        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    @Override
    public ReturnStatus killMineOrderTypeAboutEquipment(String data) {
        try {
            JSONObject borrowData = new JSONObject(data);

            int needCount = borrowData.getInt("needCount");
            int nowPage = borrowData.getInt("nowPage");
            String userAccount = borrowData.getStr("userAccount");
            String equipmentId = borrowData.getStr("equipmentId");
            List<BorrowAndReturnOrder> borrowAndReturnOrder = borrowAndReturnOrderRepository.findAllByDeviceIdAndUserIdAndOrderTypeAndOpType(equipmentId, userAccount, BorrowAndReturnOrder.OrderType.Continue,"equipmentProblem");
            if (borrowAndReturnOrder != null && borrowAndReturnOrder.size() == 1) {
                BorrowAndReturnOrder waitReturnBorrowAndReturnOrder = borrowAndReturnOrder.get(0);
                waitReturnBorrowAndReturnOrder.setOrderType(BorrowAndReturnOrder.OrderType.Kill);
                waitReturnBorrowAndReturnOrder.setReturnTime(GetTime.GetSecondTime());
                borrowAndReturnOrderRepository.save(waitReturnBorrowAndReturnOrder);
                deviceProblemRepository.deleteByUserAccountAndEquipmentId(userAccount,equipmentId);
                ReturnStatus returnStatus = getAllWorkOrderData(needCount, nowPage, userAccount);
                returnStatus.setReason("获取设备异常工单撤销成功!");
                return returnStatus;
            }else if (borrowAndReturnOrder != null && borrowAndReturnOrder.size() >= 2) {
                return new ReturnStatus("no", "工单撤销异常,出现错误工单,请联系管理人员");
            } else {
                deviceProblemRepository.deleteByUserAccountAndEquipmentId(userAccount,equipmentId);

                ReturnStatus returnStatus = getAllWorkOrderData(needCount, nowPage, userAccount);
                returnStatus.setReason("获取设备异常工单撤销成功!");
                return returnStatus;
            }

        } catch (Exception e) {
            // 4. 记录异常日志
            e.printStackTrace();
            // 5. 返回一个安全、统一的错误结果
            return new ReturnStatus("no", "服务器内部异常，请稍后再试");
        }
    }

    public ReturnStatus getAllWorkOrderData(int needCount, int nowPage, String userAccount) {

        PageRequest pageRequest = PageRequest.of(nowPage, needCount);

        List<BorrowAndReturnOrder> list = borrowAndReturnOrderRepository.findAllByUserIdOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(userAccount, pageRequest);

        long totalPages = borrowAndReturnOrderRepository.countBorrowAndReturnOrderByUserId(userAccount); // 假设count()方法返回总记录数
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
