package com.cts.report_service;

import com.cts.report_service.client.TransactionClient;
import com.cts.report_service.dao.ReportRepo;
import com.cts.report_service.dto.TransactionReportDTO;
import com.cts.report_service.entity.Report;
import com.cts.report_service.service.ReportService;
import com.cts.report_service.exception.NullDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ReportServiceApplicationTests {

	@Mock
	private ReportRepo reportRepo;

	@InjectMocks
	private ReportService reportService;

	@Mock
	private TransactionClient transactionClient;

	@Test
	void contextLoads() {
	}

	@Test
	void createReportTest() {
		Report report = new Report(1L, "Audit", "Pass", LocalDate.now());
		Mockito.when(reportRepo.save(any(Report.class))).thenReturn(report);

		Report created = reportService.create(report);

		assertNotNull(created);
		assertEquals("Audit", created.getScope());
		verify(reportRepo, times(1)).save(report);
	}

	@Test
	void findReportTest() {
		Report report = new Report(3L, "Login", "Success", LocalDate.now());

		Mockito.when(reportRepo.findById(3L)).thenReturn(Optional.of(report));

		Report foundReport = reportService.findReport(3L);

		assertNotNull(foundReport);
		assertEquals(report, foundReport);
		assertEquals("Login", foundReport.getScope());

		verify(reportRepo, times(1)).findById(3L);
	}

	@Test
	void getAllReportsTest() {
		List<Report> reports = Arrays.asList(
				new Report(1L, "Scope1", "Metrics1", LocalDate.now()),
				new Report(2L, "Scope2", "Metrics2", LocalDate.now())
		);
		Mockito.when(reportRepo.findAll()).thenReturn(reports);

		List<Report> result = reportService.getAllReports();

		assertEquals(2, result.size());
		verify(reportRepo, times(1)).findAll();
	}

	@Test
	void findReportBetweenDatesTest() {
		Report report = new Report(3L, "Login", "Success", LocalDate.of(2026, 4, 1));
		Report report2 = new Report(3L, "Login", "Success", LocalDate.of(2026, 4, 2));
		Report report3 = new Report(3L, "Login", "Success", LocalDate.of(2026, 4, 4));

		Mockito.when(reportRepo.findByGeneratedDateBetween(LocalDate.of(2026, 4, 1),  LocalDate.of(2026, 4, 3))).thenReturn(List.of(report, report2));

		List<Report> foundReports = reportService.findByGeneratedDateBetween(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3));

		assertEquals(2, foundReports.size());
		assertEquals(report, foundReports.getFirst());
		assertEquals(report2, foundReports.get(1));
	}

	@Test
	void getReportsByScopeTest() {
		Report report = new Report(3L, "Login", "Success", LocalDate.now());
		Report report1 = new Report(3L, "Signup", "Success", LocalDate.now());
		Report report2 = new Report(3L, "Login", "Success", LocalDate.now());

		Mockito.when(reportRepo.findByScope("Login")).thenReturn(List.of(report, report2));

		List<Report> foundReports = reportService.getReportsByScope("Login");

		assertEquals(2, foundReports.size());
		assertEquals(report, foundReports.getFirst());
		assertEquals(report2, foundReports.get(1));
	}

	@Test
	void updateReportTest() {
		Report report = new Report(3L, "Login", "Success", LocalDate.now());
		Report newReport = new Report(3L, "Signup", "Failed", LocalDate.now());

		Mockito.when(reportRepo.findById(3L)).thenReturn(Optional.of(report));
		Mockito.when(reportRepo.save(any(Report.class))).thenAnswer(i -> i.getArguments()[0]);

		Report result = reportService.update(3L, newReport);

		assertEquals("Signup", result.getScope());
		assertEquals("Failed", result.getMetrics());
	}

	@Test
	void deleteReportTest() {
		Long id = 1L;
		doNothing().when(reportRepo).deleteById(id);
		when(reportRepo.existsById(id)).thenReturn(true);
		reportService.delete(id);

		verify(reportRepo, times(1)).deleteById(id);
	}

	// Transaction Linked Tests
	@Test
	void generateTransactionReportTest() {
		// Arrange
		TransactionReportDTO tx1 = new TransactionReportDTO();
		tx1.setTransactionAmount(100.0);
		TransactionReportDTO tx2 = new TransactionReportDTO();
		tx2.setTransactionAmount(200.0);

		List<TransactionReportDTO> transactions = Arrays.asList(tx1, tx2);

		when(transactionClient.getTransactionsByStatus("SUCCESS")).thenReturn(transactions);
		when(reportRepo.save(any(Report.class))).thenAnswer(i -> i.getArguments()[0]);

		// Act
		Report result = reportService.generateTransactionReport("SUCCESS");

		// Assert
		assertNotNull(result);
		assertTrue(result.getScope().contains("SUCCESS"));
		assertTrue(result.getMetrics().contains("Amount:300.0"));
		assertTrue(result.getMetrics().contains("Total:2"));
		verify(transactionClient, times(1)).getTransactionsByStatus("SUCCESS");
	}

	@Test
	void generateTimedTransactionReport_QuarterlyTest() {
		// Arrange: 3 months span (April to June)
		LocalDate start = LocalDate.of(2026, 4, 1);
		LocalDate end = LocalDate.of(2026, 6, 30);

		TransactionReportDTO tx = new TransactionReportDTO();
		tx.setTransactionAmount(500.0);

		when(transactionClient.getFilteredTransactions("FAILED", start, end))
				.thenReturn(List.of(tx));
		when(reportRepo.save(any(Report.class))).thenAnswer(i -> i.getArguments()[0]);

		// Act
		Report result = reportService.generateTimedTransactionReport("FAILED", start, end);

		// Assert
		assertEquals("Transactions - FAILED - Quarterly", result.getScope());
		assertTrue(result.getMetrics().contains("Amount:500.0"));
		verify(transactionClient, times(1)).getFilteredTransactions("FAILED", start, end);
	}

	@Test
	void generateTransactionReport_ThrowsExceptionWhenNull() {
		// Arrange
		when(transactionClient.getTransactionsByStatus("PENDING")).thenReturn(null);

		// Act & Assert
		assertThrows(NullDataException.class, () -> {
			reportService.generateTransactionReport("PENDING");
		});
	}

}
