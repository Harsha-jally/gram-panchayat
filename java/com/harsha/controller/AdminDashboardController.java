package com.harsha.controller;

import com.harsha.entity.Report;
import com.harsha.repository.ReportRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class AdminDashboardController {

    private final ReportRepository reportRepo;

    public AdminDashboardController(ReportRepository reportRepo) {
        this.reportRepo = reportRepo;
    }

    // Extract pincode from admin email: admin@575602.com → 575602
    private String getPincodeFromPrincipal(Principal principal) {
        return principal.getName()
                .replace("admin@", "")
                .replace(".com", "");
    }

    // Dashboard — shows all reports for admin's pincode
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        String pincode = getPincodeFromPrincipal(principal);
        List<Report> reports = reportRepo.findByPincode(pincode);

        long pending  = reports.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long resolved = reports.stream().filter(r -> "RESOLVED".equals(r.getStatus())).count();
        long rejected = reports.stream().filter(r -> "REJECTED".equals(r.getStatus())).count();

        model.addAttribute("reports", reports);
        model.addAttribute("pincode", pincode);
        model.addAttribute("totalReports", reports.size());
        model.addAttribute("pendingCount", pending);
        model.addAttribute("resolvedCount", resolved);
        model.addAttribute("rejectedCount", rejected);

        return "dashboard";
    }

    // View single report
    @GetMapping("/dashboard/report/{id}")
    public String viewReport(@PathVariable Long id, Model model, Principal principal) {
        String pincode = getPincodeFromPrincipal(principal);
        Report report = reportRepo.findById(id)
                .filter(r -> pincode.equals(r.getPincode())) // security: only own pincode
                .orElseThrow(() -> new RuntimeException("Report not found or access denied"));

        model.addAttribute("report", report);
        return "report-detail";
    }

    // Resolve a report
    @PostMapping("/dashboard/report/{id}/resolve")
    public String resolveReport(@PathVariable Long id,
                                @RequestParam String actionTaken,
                                Principal principal) {
        String pincode = getPincodeFromPrincipal(principal);
        Report report = reportRepo.findById(id)
                .filter(r -> pincode.equals(r.getPincode()))
                .orElseThrow(() -> new RuntimeException("Access denied"));

        report.setStatus("RESOLVED");
        report.setActionTaken(actionTaken);
        reportRepo.save(report);
        return "redirect:/dashboard";
    }

    // Reject a report
    @PostMapping("/dashboard/report/{id}/reject")
    public String rejectReport(@PathVariable Long id,
                               @RequestParam String actionTaken,
                               Principal principal) {
        String pincode = getPincodeFromPrincipal(principal);
        Report report = reportRepo.findById(id)
                .filter(r -> pincode.equals(r.getPincode()))
                .orElseThrow(() -> new RuntimeException("Access denied"));

        report.setStatus("REJECTED");
        report.setActionTaken(actionTaken);
        reportRepo.save(report);
        return "redirect:/dashboard";
    }
}