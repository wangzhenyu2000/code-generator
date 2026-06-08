package com.example.codegenerator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/scaffold")
    public String scaffold() {
        return "scaffold";
    }

    @GetMapping("/reverse")
    public String reverse() {
        return "reverse";
    }
}
