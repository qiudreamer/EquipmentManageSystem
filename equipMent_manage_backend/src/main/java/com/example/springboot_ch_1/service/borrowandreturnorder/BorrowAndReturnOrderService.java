package com.example.springboot_ch_1.service.borrowandreturnorder;

import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.ReturnStatus;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:05
 * Description:
 */
public interface BorrowAndReturnOrderService {

    ReturnStatus getAllWorkOrder(String data);

    ReturnStatus handleOrderTypeAboutEquipment(String data);

    ReturnStatus doMineOrderTypeAboutEquipment(String data);

    ReturnStatus showOrderData(String data);
}
