package com.harsha.controller;

import com.harsha.entity.Report;
import com.harsha.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/submit")
    public String submitReport(@ModelAttribute Report report,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               @RequestParam(value = "audioFile", required = false) MultipartFile audioFile) {
        try {
            reportService.saveReport(report, audioFile, imageFile);
            return "redirect:/view-status";
        } catch (IOException e) {
            return "redirect:/main?error=upload_failed";
        }
    }
}