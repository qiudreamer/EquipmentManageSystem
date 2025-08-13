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
        return new ModelAndView("base/home");
    }
    @GetMapping({"/login",""})
    public ModelAndView Login(){
        return new ModelAndView("base/login");
    }
    @GetMapping("/profile")
    public ModelAndView Profile(){
        return new ModelAndView("base/profile");
    }
    @GetMapping("/test")
    public ModelAndView Test(){
        return new ModelAndView("base/test");
    }

}
