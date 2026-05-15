package com.cts.croplisting_service.controller;

import com.cts.croplisting_service.client.FarmerClient;
import com.cts.croplisting_service.client.NotificationClient;
import com.cts.croplisting_service.dao.AuditLogRepository;
import com.cts.croplisting_service.dao.CropListingRepo;
import com.cts.croplisting_service.dto.CropListingDTO;
import com.cts.croplisting_service.dto.NotificationDTO;
import com.cts.croplisting_service.dto.OrderDTO;
import com.cts.croplisting_service.entity.CropListing;
import com.cts.croplisting_service.entity.Log;
import com.cts.croplisting_service.enums.CropListingStatus;
import com.cts.croplisting_service.enums.NotificationCategory;
import com.cts.croplisting_service.enums.NotificationStatus;
import com.cts.croplisting_service.exception.ResourceNotFoundException;
import com.cts.croplisting_service.service.CropMarketService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/market")
public class CropMarketController {

    @Autowired
    private CropMarketService cropMarketService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogRepository logRepo;

    @Autowired
    private CropListingRepo cropListingRepo;

    @Autowired
    private FarmerClient farmerClient;

    @Autowired
    private NotificationClient notificationClient;

    private void sendNotification(Long userId, String subject, String message, NotificationCategory category) {
        try {
            NotificationDTO newNotification = new NotificationDTO();

            newNotification.setUserId(userId);
            newNotification.setSubject(subject);
            newNotification.setMessage(message);
            newNotification.setCategory(category);

            notificationClient.createNotification(newNotification);
        } catch (Exception e) {
            System.err.println("Sending Notification failed: " + e.getMessage());
        }
    }

    @PatchMapping("/listings/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id, @RequestParam String status, @RequestParam String reason) {
        // 1. Perform the actual approval logic...
        // 2. Save to History
        Log log = new Log();
        log.setAction(status);
        log.setTargetType("CROP");
        log.setTargetId(id);
        log.setReason(reason);
        log.setTimestamp(LocalDateTime.now());
        logRepo.save(log);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/audit-logs")
    public List<Log> getAllLogs() {
        // Uses the custom sorted method we created above
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    @GetMapping("/test-proxy")
    public void checkProxy() {
        System.out.println("The class is: " + cropMarketService.getClass().getName());
    }


    @PostMapping("/createlisting")
    public ResponseEntity<CropListingDTO> createListing(@Valid @RequestBody CropListingDTO listingDto) {
        return ResponseEntity.ok(cropMarketService.createListing(listingDto));
    }

    @GetMapping("/notifications/{listingId}")
    public ResponseEntity<List<Log>> getFarmerNotifications(@PathVariable Long listingId) {
        // Returns all "NOTIFICATION" logs for this specific crop listing
        return ResponseEntity.ok(auditLogRepository.findByTargetIdAndAction(listingId, "NOTIFICATION"));
    }


//    @PutMapping("/listings/validate/{id}")
//    public ResponseEntity<CropListingDTO> validateListing(@PathVariable Long id) {
//        return ResponseEntity.ok(cropMarketService.validateListing(id));
//    }

    @PatchMapping("/listings/validate/{id}")
    public ResponseEntity<CropListingDTO> validateListing(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String reason) {

        // Pass the ID, the new status (APPROVED/REJECTED), and the reason to the service
        CropListingDTO updated = cropMarketService.validateListing(id, status, reason);

        CropListing listing = cropListingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + id));

        Long userId = farmerClient.getFarmerDetails(listing.getFarmerId()).getUserId();

        if(status.equals("VALIDATED")) {
            sendNotification(userId, "Crop listing Approved", "Your crop listing got approved because " + reason, NotificationCategory.VERIFICATION);
        } else if(status.equals("REJECTED")) {
            sendNotification(userId, "Crop listing Rejected", "Your crop listing got rejected because " + reason, NotificationCategory.ALERT);
        }
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/listings/status/{status}")
    public ResponseEntity<List<CropListingDTO>> getListingsByStatus(@PathVariable CropListingStatus status) {
        return ResponseEntity.ok(cropMarketService.getListingsByStatus(status));
    }


    @PostMapping("/placeorder")
    public ResponseEntity<OrderDTO> placeOrder(@Valid @RequestBody OrderDTO orderDto) {
        return ResponseEntity.ok(cropMarketService.placeOrder(orderDto));
    }


    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(cropMarketService.updateOrderStatus(id, status));
    }


//    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/orders/trader/{traderId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByTrader(@PathVariable Long traderId) {
        return ResponseEntity.ok(cropMarketService.getOrdersByTrader(traderId));
    }


    @GetMapping("/listings")
    public ResponseEntity<List<CropListingDTO>> getAllListings() {
        return ResponseEntity.ok(cropMarketService.getAllListings());
    }

    // Inside com.cts.croplisting_service.controller.CropMarketController
    @GetMapping("/listings/farmer/{farmerId}")
    public ResponseEntity<List<CropListingDTO>> getListingsByFarmer(@PathVariable Long farmerId) {
        return ResponseEntity.ok(cropMarketService.getListingsByFarmer(farmerId));
    }


    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(cropMarketService.getAllOrders());
    }

    @PutMapping("/listings/{listingId}/reduce-quantity")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable Long listingId,
            @RequestParam int quantity) {

        // This line is what actually triggers the reduction logic!
        cropMarketService.reduceQuantity(listingId, quantity);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        // You'll need to implement this method in your CropMarketService
        return ResponseEntity.ok(cropMarketService.getOrderById(id));
    }
}
