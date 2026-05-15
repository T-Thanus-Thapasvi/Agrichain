package com.cts.croplisting_service.dao;

import com.cts.croplisting_service.entity.Order;
import com.cts.croplisting_service.enums.OrderStatus;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    // Find all orders placed by a specific Trader
    List<Order> findByTraderId(Long traderId);

    // Find all orders for a specific Crop Listing
    List<Order> findByCropListing_ListingId(Long listingId);

    // Filter orders by their current status (CONFIRMED, COMPLETED, etc.)
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // 1. Add this to OrderRepo.java
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Order o SET o.orderStatus = :status WHERE o.orderId = :id")
    int updateStatusOnly(@Param("id") Long id, @Param("status") OrderStatus status);
}