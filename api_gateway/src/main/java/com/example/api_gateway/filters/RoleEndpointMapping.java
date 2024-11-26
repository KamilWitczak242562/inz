package com.example.api_gateway.filters;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RoleEndpointMapping {

    private final Map<String, String> endpointRoles = new HashMap<>();


    /*
    TODO MAPPING ALL ENDPOINTS
     */
    public RoleEndpointMapping() {
        endpointRoles.put("/api/v1/admin", "ADMIN");
        endpointRoles.put("/api/v1/manager", "MANAGER");
    }

    public String getRequiredRole(String path) {
        return endpointRoles.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}