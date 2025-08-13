package com.example.springboot_ch_1.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * 栾俊豪 2020012422
 *
 * @author 栾俊豪
 * @Date 2023/11/13 11:33
 */

public class GetTime {
    public static String GetSSTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        String data = sdf.format(new Date());
        return data;
    }
    public static String GetSecondTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        String data = sdf.format(new Date());
        System.out.println("获得的时间是: " + data);
        return data;
    }

    public static long getDifferenceDays(LocalDate givenDate) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 计算两个日期之间的差异
        return ChronoUnit.DAYS.between(givenDate, currentDate);
    }

}
