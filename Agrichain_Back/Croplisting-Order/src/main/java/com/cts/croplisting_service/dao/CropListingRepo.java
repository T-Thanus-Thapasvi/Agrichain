package com.cts.croplisting_service.dao;

import com.cts.croplisting_service.entity.CropListing;
import com.cts.croplisting_service.enums.CropListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Important import
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Spring's transactional

import java.util.List;

@Repository
public interface CropListingRepo extends JpaRepository<CropListing, Long> {

    List<CropListing> findByStatus(CropListingStatus status);

    List<CropListing> findByFarmerId(Long farmerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true) // Add these!
    @Transactional
    @Query("UPDATE CropListing c SET c.quantity = c.quantity - :qty WHERE c.listingId = :id AND c.quantity >= :qty")
    int decrementQuantity(@Param("id") Long id, @Param("qty") int qty);
}