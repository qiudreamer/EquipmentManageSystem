package com.example.springboot_ch_1.util;

import java.time.LocalDate;


/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 16:37
 * Description:
 * user_profile_href:是存放用户头像用的
 * user_textContent_href:是存放用户提交的blog中包含的图片用的
 * user_article_href:是存放用户blog的封面用的
 * check_source:是帖子文件，存放图片用的
 * 加与不加"_url"的区别是：加了"_url"的路径是返回的文件的url路径(输入网址访问用的),不加"_url"的路径是服务器的本地路径(存储用的)
 */

public class AllHref {

//  管理员数量最多几位
    public static int max_steward_count = 3;


   public static String front_url1 = "http://localhost:9998";
   public static String front_url2 = "http://localhost:9999";
   public static String front_url3 = "http://localhost:7778";

   //本地用的
   public static String equipment_img_href = "C:/Users/12431/Desktop/equipmentljhmanageImg/";
   public static String equipment_img_href_url = "http://localhost:9996/";



    public static String user_root_name = "none_user";
    public static String steward_root_name = "steward";
    public static String super_steward_root_name = "super_steward";








}
