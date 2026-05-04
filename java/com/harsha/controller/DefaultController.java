package com.harsha.controller;

import com.harsha.entity.Report;
import com.harsha.entity.User;
import com.harsha.service.ReportService;
import com.harsha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    // Helper: always resolve to email regardless of auth type
    private String resolveEmail(Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            return (String) oauthToken.getPrincipal().getAttributes().get("email");
        }
        return auth.getName(); // For JWT/form login, getName() already returns email
    }

    // Helper: resolve display name for both OAuth2 and form/JWT login
    private String resolveName(Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // Directly get name from Google's attributes — no DB lookup needed
            String name = (String) oauthToken.getPrincipal().getAttributes().get("name");
            return (name != null) ? name : (String) oauthToken.getPrincipal().getAttributes().get("email");
        }
        // Form/JWT login — look up by email
        User user = userService.findUserByEmail(auth.getName());
        return (user != null) ? user.getName() : auth.getName();
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String index(Authentication auth, Model model) {
        if (auth != null) {
            model.addAttribute("loggedInName", resolveName(auth));
        }
        return "index";
    }

    @GetMapping("/main")
    public String mainPage(Authentication auth, Model model) {
        Report report = new Report();
        if (auth != null) {
            String email = resolveEmail(auth);
            User user = userService.findUserByEmail(email);
            if (user != null) {
                report.setName(resolveName(auth));
                report.setEmail(user.getEmail());
            } else {
                report.setName(resolveName(auth));
                report.setEmail(email);
            }
        }
        model.addAttribute("report", report);
        return "main";
    }

    @GetMapping("/view-status")
    public String viewStatus(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        String email = resolveEmail(auth);
        model.addAttribute("reports", reportService.getReportsByUser(email));
        return "status";
    }
}