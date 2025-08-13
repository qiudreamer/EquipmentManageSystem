package com.example.springboot_ch_1.controller;

/**
 * 栾俊豪 2020012422
 *
 * @author 栾俊豪
 * @Date 2023/12/31 12:30
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping({"/"})
public class HrefController {

    @GetMapping("/home")
    public ModelAndView Main(){
        return new ModelAndView("base/manage");
    }
    @GetMapping({"/login",""})
    public ModelAndView Login(){
        return new ModelAndView("base/login");
    }
    @GetMapping("/user")
    public ModelAndView User(){
        return new ModelAndView("base/user");
    }
    @GetMapping("/order")
    public ModelAndView Order(){
        return new ModelAndView("base/order");
    }
    @GetMapping("/check")
    public ModelAndView Check(){
        return new ModelAndView("base/check");
    }

}
