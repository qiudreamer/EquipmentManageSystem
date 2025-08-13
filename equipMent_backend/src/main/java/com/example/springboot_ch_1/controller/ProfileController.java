package com.example.springboot_ch_1.controller;

import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.service.borrowandreturnorder.BorrowAndReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:07
 * Description:
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private BorrowAndReturnOrderService borrowAndReturnOrderService;

    @PostMapping("/getAllWorkOrder")
    public ReturnStatus getAllWorkOrder(@RequestBody String data) {
        return borrowAndReturnOrderService.getAllWorkOrder(data);
    }

    @PostMapping("/killMineOrderTypeAboutEquipment")
    private ReturnStatus killMineOrderTypeAboutEquipment(@RequestBody String data){
        return borrowAndReturnOrderService.killMineOrderTypeAboutEquipment(data);
    }

}