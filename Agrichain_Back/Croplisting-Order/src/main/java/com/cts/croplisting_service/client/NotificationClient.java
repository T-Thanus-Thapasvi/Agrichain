package com.cts.croplisting_service.client;

import com.cts.croplisting_service.config.FeignClientConfig;
import com.cts.croplisting_service.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE", configuration = FeignClientConfig.class)
public interface NotificationClient {
    @PostMapping("/api/notification/create")
    void createNotification(@RequestBody NotificationDTO requestDTO);
}
