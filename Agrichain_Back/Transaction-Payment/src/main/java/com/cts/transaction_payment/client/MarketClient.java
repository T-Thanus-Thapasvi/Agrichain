package com.cts.transaction_payment.client;

import com.cts.transaction_payment.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "market-service", url = "https://${market.service.url}"
@FeignClient(name = "CROPLISTING-SERVICE")
public interface MarketClient {
    // To get the details of the order (including which listing and how much quantity)
    @GetMapping("/market/orders/{id}")
    OrderDTO getOrderById(@PathVariable("id") Long id);

    // To tell the Crop Service to reduce stock for a SPECIFIC listing
    @PutMapping("/market/listings/{listingId}/reduce-quantity")
    void reduceQuantity(@PathVariable("listingId") Long listingId, @RequestParam("quantity") int quantity);

    @PutMapping("/market/orders/{id}/status")
    void updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}