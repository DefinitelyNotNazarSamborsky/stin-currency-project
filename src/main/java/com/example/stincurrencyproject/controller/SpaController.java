package com.example.stincurrencyproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {
    @RequestMapping(value = {"/", "/login", "/dashboard"})
    public String forward() {
        return "forward:/index.html";
    }
}