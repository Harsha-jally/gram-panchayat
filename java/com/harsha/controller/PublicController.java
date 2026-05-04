package com.harsha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/report")
    public String showReportForm() {
        return "main"; // alternative path to same template
    }
}