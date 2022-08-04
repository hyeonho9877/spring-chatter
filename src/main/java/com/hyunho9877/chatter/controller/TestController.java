package com.hyunho9877.chatter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/security")
    public String security() {
        return "security";
    }

}
