package com.harsha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {
    
    @GetMapping("/security")
    public Map<String, Object> securityInfo(Authentication authentication) {
        Map<String, Object> info = new HashMap<>();
        
        if (authentication != null) {
            info.put("authenticated", authentication.isAuthenticated());
            info.put("name", authentication.getName());
            info.put("authorities", authentication.getAuthorities());
            info.put("details", authentication.getDetails());
        } else {
            info.put("authenticated", false);
        }
        
        return info;
    }
    
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    @GetMapping("/mappings")
    public Map<String, Object> showMappings() {
        Map<String, Object> mappings = new HashMap<>();
        
        requestMappingHandlerMapping.getHandlerMethods().forEach((key, value) -> {
            mappings.put(key.toString(), value.toString());
        });
        
        return mappings;
    }
}