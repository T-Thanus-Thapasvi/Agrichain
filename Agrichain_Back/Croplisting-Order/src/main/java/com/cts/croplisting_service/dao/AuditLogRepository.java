package com.cts.croplisting_service.dao;

import com.cts.croplisting_service.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<Log, Long> {

    /**
     * Fetches all logs sorted by timestamp in descending order.
     * This ensures the most recent officer actions appear at the top of the UI.
     */
    List<Log> findAllByOrderByTimestampDesc();

    /**
     * Optional: Fetch logs specific to a certain type (e.g., only CROP or only DOCUMENT)
     */
    List<Log> findByTargetTypeOrderByTimestampDesc(String targetType);


    // Fetch notifications for a specific listing
    List<Log> findByTargetIdAndAction(Long targetId, String action);
}