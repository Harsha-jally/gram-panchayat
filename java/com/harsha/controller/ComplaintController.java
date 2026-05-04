package com.harsha.controller;

import com.harsha.dto.ComplaintDTO;
import com.harsha.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // This handles the form submission from main.html
    @PostMapping("/submitReport")
    public String submitReportForm(@ModelAttribute ComplaintDTO complaintDTO, Model model) {
        complaintService.createComplaint(complaintDTO);
        return "redirect:/thank-you"; // after submission, redirect here
    }

    @GetMapping("/thank-you")
    public String thankYouPage() {
        return "thank-you";
    }
}
