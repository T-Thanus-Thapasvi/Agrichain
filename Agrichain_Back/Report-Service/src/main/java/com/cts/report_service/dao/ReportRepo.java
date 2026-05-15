package com.cts.report_service.dao;

import com.cts.report_service.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepo extends JpaRepository<Report, Long> {
    List<Report> findByScope(String scope);

    List<Report> findByGeneratedDateBetween(LocalDate start, LocalDate end);
}
