package com.cts.Registration_Service.controller;

import com.cts.Registration_Service.client.NotificationClient;
import com.cts.Registration_Service.dao.FarmerDocumentRepo;
import com.cts.Registration_Service.dto.NotificationDTO;
import com.cts.Registration_Service.dto.response.FarmerDocumentResponseDTO;
import com.cts.Registration_Service.dto.response.FarmerResponseDTO;
import com.cts.Registration_Service.entity.FarmerDocument;
import com.cts.Registration_Service.enums.DocType;
import com.cts.Registration_Service.enums.NotificationCategory;
import com.cts.Registration_Service.enums.NotificationStatus;
import com.cts.Registration_Service.enums.VerificationStatus;
import com.cts.Registration_Service.exception.UserNotFoundException;
import com.cts.Registration_Service.service.FarmerDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class FarmerDocumentController {

    @Autowired
    private FarmerDocumentService documentService;

    @Autowired
    private FarmerDocumentRepo documentRepo;

    @Autowired
    private NotificationClient notificationClient;

    private void sendNotification(Long userId, String subject, String message, NotificationCategory category) {
        try {
            NotificationDTO newNotification = new NotificationDTO();

            newNotification.setUserId(userId);
            newNotification.setSubject(subject);
            newNotification.setMessage(message);
            newNotification.setCategory(category);
            newNotification.setStatus(NotificationStatus.UNREAD);

            notificationClient.createNotification(newNotification);
        } catch (Exception e) {
            System.err.println("Sending Notification failed: " + e.getMessage());
        }
    }

    @PostMapping("/farmer/{farmerId}/upload")
    public ResponseEntity<FarmerDocument> uploadDocument(
            @PathVariable Long farmerId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("docType") DocType docType) throws IOException {

        FarmerDocument savedDoc = documentService.uploadBlobFile(farmerId, file, docType);
        return new ResponseEntity<>(savedDoc, HttpStatus.CREATED);
    }

//    @GetMapping("/farmer/{farmerId}")
//    public ResponseEntity<List<FarmerDocument>> getDocumentsByFarmer(@PathVariable Long farmerId) {
//        return ResponseEntity.ok(documentService.getDocumentsByFarmer(farmerId));
//    }

    // FarmerDocumentController.java
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<FarmerDocument>> getDocumentsByFarmer(
            @PathVariable Long farmerId,
            @RequestParam(required = false) DocType type) { // Optional parameter

        return ResponseEntity.ok(documentService.getDocumentsByFarmer(farmerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FarmerDocumentResponseDTO>> getAllDocuments() {
        List<FarmerDocumentResponseDTO> farmerDocumentResponseDTOs = new ArrayList<>();
        List<FarmerDocument> FarmerDocuments=documentService.getAllDocuments();
        for(FarmerDocument farmerDocument:FarmerDocuments){
            FarmerDocumentResponseDTO farmerDocumentResponseDTO=new FarmerDocumentResponseDTO();
            farmerDocumentResponseDTO.setFarmerId(farmerDocument.getFarmer().getFarmerId());
            farmerDocumentResponseDTO.setDocumentId(farmerDocument.getId());
            farmerDocumentResponseDTO.setVerificationStatus(farmerDocument.getVerificationStatus());
            farmerDocumentResponseDTO.setDocType(farmerDocument.getDocType());
            farmerDocumentResponseDTO.setContent(farmerDocument.getContent());

            farmerDocumentResponseDTOs.add(farmerDocumentResponseDTO);
        }

        return ResponseEntity.ok(farmerDocumentResponseDTOs);
    }

    @PatchMapping("/{documentId}/verify")
    public ResponseEntity<FarmerDocumentResponseDTO> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam("status") VerificationStatus status) {

        FarmerDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new UserNotFoundException("Document not found.", HttpStatus.NOT_FOUND));

        FarmerDocument farmerDocument = documentService.verifyDocument(documentId, status);

        FarmerDocumentResponseDTO farmerDocumentResponseDTO = new FarmerDocumentResponseDTO();
        farmerDocumentResponseDTO.setFarmerId(farmerDocument.getFarmer().getFarmerId());
        farmerDocumentResponseDTO.setDocumentId(farmerDocument.getId());
        farmerDocumentResponseDTO.setVerificationStatus(farmerDocument.getVerificationStatus());
        farmerDocumentResponseDTO.setDocType(farmerDocument.getDocType());
        farmerDocumentResponseDTO.setContent(farmerDocument.getContent());



        System.out.println(farmerDocumentResponseDTO);
        if(status == VerificationStatus.VERIFIED) {
            sendNotification(doc.getFarmer().getUsers().getId(), // Using UserID for notification
                    "Document Approved",
                    "Your Document " + doc.getDocType() + " has been approved.",
                    NotificationCategory.VERIFICATION);
        } else if(status == VerificationStatus.REJECTED) {
            sendNotification(doc.getFarmer().getUsers().getId(),
                    "Document Rejected",
                    "Your Document " + doc.getDocType() + " was rejected. Please re-upload.",
                    NotificationCategory.ALERT);
        }

        return ResponseEntity.ok(farmerDocumentResponseDTO);
    }
}