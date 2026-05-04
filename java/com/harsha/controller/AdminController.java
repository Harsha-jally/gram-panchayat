package com.harsha.controller;

import com.harsha.entity.Report;
import com.harsha.entity.User;
import com.harsha.repository.ReportRepository;
import com.harsha.service.ReportService;
import com.harsha.service.UserService;
import com.harsha.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository; // ← added

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // Extract pincode from admin email: admin@575602.com → 575602
    private String getPincode(Principal principal) {
        return principal.getName()
                .replace("admin@", "")
                .replace(".com", "");
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String phone,
                               @RequestParam String email, @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        try {
            User newUser = new User();
            newUser.setName(name);
            newUser.setPhone(phone);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole("USER");
            userService.saveUser(newUser);
            redirectAttributes.addFlashAttribute("success", "Account created! Please sign in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("regError", e.getMessage());
            return "redirect:/login?tab=signup";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        String pincode = getPincode(principal); // ← extract pincode from login email
        List<Report> reports = reportRepository.findByPincode(pincode); // ← only their pincode
        model.addAttribute("reports", reports);
        model.addAttribute("pincode", pincode);
        return "dashboard";
    }

    @GetMapping("/admin/report/{id}")
    public String viewReportDetails(@PathVariable Long id, Model model, Principal principal) {
        String pincode = getPincode(principal);
        reportService.getReportById(id)
                .filter(r -> pincode.equals(r.getPincode())) // ← block access to other pincodes
                .ifPresent(report -> model.addAttribute("report", report));
        return "complaint-details";
    }

    @PostMapping("/admin/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               @RequestParam(required = false) String actionTaken,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        String pincode = getPincode(principal);

        // Security: only update if report belongs to admin's pincode
        reportService.getReportById(id)
                .filter(r -> pincode.equals(r.getPincode())) // ← guard
                .ifPresent(report -> {
                    reportService.updateStatus(id, status, actionTaken);
                    try {
                        emailService.sendStatusUpdateEmail(report.getEmail(), report.getName(), status);
                    } catch (Exception e) {
                        System.err.println("Email notification failed: " + e.getMessage());
                    }
                });

        redirectAttributes.addFlashAttribute("message", "Status updated successfully");
        return "redirect:/admin/report/" + id;
    }
}