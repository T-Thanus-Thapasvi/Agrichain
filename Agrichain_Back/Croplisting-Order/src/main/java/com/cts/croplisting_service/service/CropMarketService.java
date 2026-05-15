package com.cts.croplisting_service.service;

import com.cts.croplisting_service.client.FarmerClient;
import com.cts.croplisting_service.dao.AuditLogRepository;
import com.cts.croplisting_service.dao.CropListingRepo;
import com.cts.croplisting_service.dao.OrderRepo;
import com.cts.croplisting_service.dto.CropListingDTO;
import com.cts.croplisting_service.dto.FarmerDTO;
import com.cts.croplisting_service.dto.OrderDTO;
import com.cts.croplisting_service.entity.CropListing;
import com.cts.croplisting_service.entity.Log;
import com.cts.croplisting_service.entity.Order;
import com.cts.croplisting_service.enums.CropListingStatus;
import com.cts.croplisting_service.enums.OrderStatus;
import com.cts.croplisting_service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropMarketService {

    @Autowired
    private CropListingRepo cropListingRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private FarmerClient farmerClient; // For Microservice Communication

    @Autowired
    private AuditLogRepository logRepo;

    // 1. Create a new crop listing
    public CropListingDTO createListing(CropListingDTO dto) {
        // Validation: Ensure Farmer exists in the Farmer Microservice
        try {
            farmerClient.getFarmerDetails(dto.getFarmerId());
        } catch (Exception e) {
            // PRINT THE ERROR TO YOUR CONSOLE
            System.err.println("Feign Call Failed! Reason: " + e.getMessage());
            e.printStackTrace();

            throw new ResourceNotFoundException("Farmer with ID " + dto.getFarmerId() + " not found in Farmer Service");
        }

        CropListing listing = new CropListing();
        listing.setFarmerId(dto.getFarmerId());
        listing.setCropType(dto.getCropType());
        listing.setQuantity(dto.getQuantity());
        listing.setPrice(dto.getPrice());
        listing.setLocation(dto.getLocation());
        listing.setStatus(CropListingStatus.PENDING);

        CropListing saved = cropListingRepo.save(listing);
        return toListingDTO(saved);
    }

    // 2. Validate a crop listing (Officer Action)
//    public CropListingDTO validateListing(Long listingId) {
//        CropListing listing = cropListingRepo.findById(listingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + listingId));
//        listing.setStatus(CropListingStatus.VALIDATED);
//        return toListingDTO(cropListingRepo.save(listing));
//    }

    // 2. Validate a crop listing (Officer Action)
    @Transactional
    public CropListingDTO validateListing(Long listingId, String status, String reason) {
        // 1. Find the listing
        CropListing listing = cropListingRepo.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + listingId));

        // 2. Update the status based on officer input
        // Convert String status (APPROVED/REJECTED) to your Enum
        listing.setStatus(CropListingStatus.valueOf(status.toUpperCase()));
        CropListing savedListing = cropListingRepo.save(listing);

        // 3. CRITICAL: Save to Audit Log History
        Log log = new Log();
        log.setAction(status.toUpperCase());
        log.setTargetType("CROP");
        log.setTargetId(listingId);
        log.setReason(reason);
        log.setTimestamp(LocalDateTime.now());
        //log.setOfficerName("Market_Officer"); // You can hardcode this or get from Auth

        logRepo.save(log); // This is what makes it appear in your History tab!

        // 3. Create a Notification (Log Entry)
        Log notification = new Log();
        notification.setAction("NOTIFICATION"); // Tag it as a notification
        notification.setTargetType("CROP_APPROVAL");
        notification.setTargetId(listingId); // The Listing ID

        // Construct a friendly message for the farmer
        String message = String.format("Your listing for %s (ID: %d) has been %s. Reason: %s",
                listing.getCropType(), listingId, status.toUpperCase(), reason);
        notification.setReason(message);

        notification.setTimestamp(LocalDateTime.now());

        logRepo.save(notification);

        //return toListingDTO(savedListing);
        return toListingDTO(savedListing);
    }

    // 3. Place a new order
    public OrderDTO placeOrder(OrderDTO dto) {
        CropListing listing = cropListingRepo.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        if (listing.getQuantity() < dto.getQuantity()) {
            throw new RuntimeException("Not enough stock available for this listing");
        }

        // Logic for Inventory deduction can be added here or in Transaction Service

        Order order = new Order();
        order.setCropListing(listing);
        order.setTraderId(dto.getTraderId());
        order.setQuantity(dto.getQuantity());
        order.setOrderDate(dto.getOrderDate());
        order.setOrderStatus(OrderStatus.PENDING);

        Order saved = orderRepo.save(order);
        return toOrderDTO(saved);
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toOrderDTO(order);
    }

    // 4. Update order status
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        System.out.println(">>> RECEIVED REQUEST TO UPDATE STATUS FOR ORDER: " + orderId);

        // Perform update via query to avoid "dirty" entity overwrite
        int updated = orderRepo.updateStatusOnly(orderId, OrderStatus.valueOf(status.toUpperCase()));

        if (updated == 0) throw new ResourceNotFoundException("Order not found");

        // Return the updated DTO
        Order order = orderRepo.findById(orderId).get();
        return toOrderDTO(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reduceQuantity(Long listingId, int quantityToReduce) {
        System.out.println(">>> ATTEMPTING ATOMIC REDUCTION for Listing: " + listingId);

        int rowsUpdated = cropListingRepo.decrementQuantity(listingId, quantityToReduce);

        if (rowsUpdated == 0) {
            throw new RuntimeException("Failed to reduce quantity. Either listing not found or insufficient stock.");
        }

        System.out.println(">>> ATOMIC REDUCTION SUCCESSFUL");
    }

    // --- Query Methods ---

    public List<CropListingDTO> getListingsByStatus(CropListingStatus status) {
        return cropListingRepo.findByStatus(status)
                .stream().map(this::toListingDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByTrader(Long traderId) {
        return orderRepo.findByTraderId(traderId)
                .stream().map(this::toOrderDTO).collect(Collectors.toList());
    }

    public List<CropListingDTO> getAllListings() {
        return cropListingRepo.findAll()
                .stream().map(this::toListingDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepo.findAll()
                .stream().map(this::toOrderDTO).collect(Collectors.toList());
    }

    // Inside com.cts.croplisting_service.service.CropMarketService
    public List<CropListingDTO> getListingsByFarmer(Long farmerId) {
        return cropListingRepo.findByFarmerId(farmerId)
                .stream()
                .map(this::toListingDTO) // Using your existing mapping helper
                .collect(Collectors.toList());
    }
    // --- Mapping Helpers (Corrected for Microservice Logic) ---

    private CropListingDTO toListingDTO(CropListing listing) {
        CropListingDTO dto = new CropListingDTO();
        dto.setListingId(listing.getListingId());
        dto.setFarmerId(listing.getFarmerId()); // Using ID directly
        dto.setCropType(listing.getCropType());
        dto.setQuantity(listing.getQuantity());
        dto.setPrice(listing.getPrice());
        dto.setLocation(listing.getLocation());
        dto.setStatus(listing.getStatus().name());
        return dto;
    }

    private OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setListingId(order.getCropListing().getListingId());
        dto.setTraderId(order.getTraderId());
        dto.setQuantity(order.getQuantity());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus().name());
        return dto;
    }
}