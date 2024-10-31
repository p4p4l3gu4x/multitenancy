package com.hudson.multitenancy.controller;

import java.util.List;
import com.hudson.multitenancy.model.TenantEntity;
import com.hudson.multitenancy.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping(value = "/tenant")
    public List<TenantEntity> getAllTenants() {
        return tenantService.listAll();
    }

    @PostMapping(value = "/tenant")
    public TenantEntity createTenant(@RequestBody String tenantName){
        return tenantService.createTenant(tenantName);
    }
}
