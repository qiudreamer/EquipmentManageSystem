package com.example.springboot_ch_1.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:15
 * Description:
 */
@Data
//code有两种类型，yes和no，yes即为操作成功，no为操作失败，reason为操作失败的原因，returnData为操作成功时返回的数据。
public class ReturnStatus {
    public String code;
    public String reason;
    public JSONObject returnData;
    public ReturnStatus(String code,String reason){
        this.code = code;
        this.reason = reason;
    }
}
