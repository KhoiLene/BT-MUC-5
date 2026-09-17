package com.example.demo.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/admin/categories")
    public String categoryAjaxPage() {
        return "category-ajax";
    }

    @GetMapping("/admin/products")
    public String productAjaxPage() {
        return "product-ajax";
    }
}

