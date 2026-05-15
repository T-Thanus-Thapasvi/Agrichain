package com.example.audit_compliance_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient   // ✅ this makes it a Eureka client
public class AuditComplianceServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuditComplianceServiceApplication.class, args);
	}
}
