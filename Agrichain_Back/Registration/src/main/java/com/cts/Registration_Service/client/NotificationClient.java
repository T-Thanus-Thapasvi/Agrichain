package com.cts.Registration_Service.client;

import com.cts.Registration_Service.config.FeignClientConfig;
import com.cts.Registration_Service.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE", configuration = FeignClientConfig.class)
public interface NotificationClient {
    @PostMapping("/api/notification/create")
    void createNotification(@RequestBody NotificationDTO requestDTO);
}
