package com.example.Marketplace.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/app")
public class AppController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the JavaMarketplace";
    }

}
