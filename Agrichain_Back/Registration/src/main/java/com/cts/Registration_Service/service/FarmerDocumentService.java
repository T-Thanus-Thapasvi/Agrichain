package com.cts.Registration_Service.service;

import com.cts.Registration_Service.dao.AuditLogRepo;
import com.cts.Registration_Service.dao.FarmerDocumentRepo;
import com.cts.Registration_Service.dao.FarmerRepo;
import com.cts.Registration_Service.entity.AuditLog;
import com.cts.Registration_Service.entity.Farmer;
import com.cts.Registration_Service.entity.FarmerDocument;
import com.cts.Registration_Service.enums.DocType;
import com.cts.Registration_Service.enums.FarmerStatus;
import com.cts.Registration_Service.enums.VerificationStatus;
import com.cts.Registration_Service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class FarmerDocumentService {

    @Autowired
    private FarmerDocumentRepo documentRepo;

    @Autowired
    private FarmerRepo farmerRepo;

    @Autowired
    private AuditLogRepo auditLogRepo;

    private final String BASE_PATH = "C:/Registration_FarmerDocument/";

    @Transactional
    public FarmerDocument uploadBlobFile(Long farmerId, MultipartFile file, DocType docType) throws IOException {
        if (isGuestUser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Guests are not permitted to upload documents.");
        }

        Farmer farmer = authorizeAndGetFarmer(farmerId);

        String originalName = file.getOriginalFilename();
        String fileName = (originalName != null ? originalName.replaceAll("\\s+", "_") : "document_" + System.currentTimeMillis());

        FarmerDocument document = new FarmerDocument();
        document.setFarmer(farmer);
        document.setDocType(docType);
        document.setFileName(fileName);

        document.setContent(file.getBytes());

        document.setVerificationStatus(VerificationStatus.PENDING);

        return documentRepo.save(document);
    }

    @Transactional(readOnly = true)
    public List<FarmerDocument> getDocumentsByFarmer(Long farmerId) {
        if (isGuestUser()) {
            return documentRepo.findByFarmer_FarmerIdAndDocType(farmerId, DocType.LAND_RECORD);
        }
        authorizeAndGetFarmer(farmerId);
        return documentRepo.findByFarmer_FarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public List<FarmerDocument> getAllDocuments() {
        if (!isStaffUser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied.");
        }
        return documentRepo.findAll();
    }

    @Transactional
    public FarmerDocument verifyDocument(Long documentId, VerificationStatus status) {
        if (!isStaffUser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied.");
        }
        FarmerDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new UserNotFoundException("Document not found.", HttpStatus.NOT_FOUND));
        Farmer farmer = doc.getFarmer();
        farmer.setStatus(FarmerStatus.APPROVED);
        doc.setVerificationStatus(status);
        return documentRepo.save(doc);
    }

    private Farmer authorizeAndGetFarmer(Long farmerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new UserNotFoundException("Farmer not found.", HttpStatus.NOT_FOUND));

        if (!isStaffUser() && !farmer.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access.");
        }
        return farmer;
    }

    private boolean isStaffUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_OFFICER"));
    }

    private boolean isGuestUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"));
    }
}