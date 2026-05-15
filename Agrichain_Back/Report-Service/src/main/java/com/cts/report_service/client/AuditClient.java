package com.cts.report_service.client;

import com.cts.report_service.config.FeignClientConfig;
import com.cts.report_service.dto.AuditLogRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "IAM-FARMERREGISTRATION-SERVICE", configuration = FeignClientConfig.class)
public interface AuditClient {
    @PostMapping("/api/logs/CreateLog")
    void createLog(@RequestBody AuditLogRequestDTO requestDTO);
}
