package com.cts.report_service.client;

import com.cts.report_service.dto.TransactionReportDTO;
import com.cts.report_service.enums.TransactionStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "transaction-payment", path = "/transactions")
public interface TransactionClient {

    @GetMapping("/status/{status}")
    List<TransactionReportDTO> getTransactionsByStatus(@PathVariable("status") String status);

    @GetMapping("/filter")
    List<TransactionReportDTO> getFilteredTransactions(
            @RequestParam("status") String status,
            @RequestParam("start") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate end
    );
}
