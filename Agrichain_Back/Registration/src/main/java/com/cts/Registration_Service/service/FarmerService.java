package com.cts.Registration_Service.service;

import com.cts.Registration_Service.client.UserClient;
import com.cts.Registration_Service.dao.FarmerRepo;
import com.cts.Registration_Service.dto.response.FarmerResponseDTO;
import com.cts.Registration_Service.entity.Farmer;
import com.cts.Registration_Service.entity.FarmerDocument;
import com.cts.Registration_Service.enums.DocType;
import com.cts.Registration_Service.enums.FarmerStatus;
import com.cts.Registration_Service.enums.VerificationStatus;
import com.cts.Registration_Service.exception.FarmerNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FarmerService {

    @Autowired
    private FarmerRepo farmerRepo;

    @Autowired
    private UserClient userClient;

    @Transactional
    public Farmer completeFarmerProfile(Long userId, Farmer updates, MultipartFile idProof, MultipartFile landRecord) throws IOException {
        Farmer existingFarmer = farmerRepo.findByUsers_id(userId)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer profile not found"));

        existingFarmer.setAddress(updates.getAddress());
        existingFarmer.setLandDetails(updates.getLandDetails());
        existingFarmer.setDob(updates.getDob());
        existingFarmer.setGender(updates.getGender());
        existingFarmer.setStatus(FarmerStatus.PENDING_VERIFICATION);

        if (idProof != null) {
            attachDocument(existingFarmer, idProof, DocType.ID_PROOF);
        }
        if (landRecord != null) {
            attachDocument(existingFarmer, landRecord, DocType.LAND_RECORD);
        }
        System.out.println("farmer: " + existingFarmer);

        return farmerRepo.save(existingFarmer);
    }

    private void attachDocument(Farmer farmer, MultipartFile file, DocType type) throws IOException {
        FarmerDocument doc = new FarmerDocument();
        doc.setFileName(file.getOriginalFilename());
        doc.setContent(file.getBytes()); // BLOB storage
        doc.setDocType(type);
        doc.setVerificationStatus(VerificationStatus.PENDING);

        doc.setFarmer(farmer);
        farmer.getDocuments().add(doc);
    }

    public FarmerResponseDTO mapToResponse(Farmer farmer) {
        FarmerResponseDTO response = new FarmerResponseDTO();
        response.setFarmerId(farmer.getFarmerId());
        response.setName(farmer.getName());
        response.setDob(farmer.getDob());

        // FIX: Convert Enum to String for the DTO
        if (farmer.getGender() != null) {
            response.setGender(farmer.getGender());
        }

        response.setAddress(farmer.getAddress());
        response.setLandDetails(farmer.getLandDetails());
        response.setContactInfo(farmer.getContactInfo());
        response.setStatus(farmer.getStatus() != null ? farmer.getStatus() : FarmerStatus.ACTIVE);

        if (farmer.getUsers() != null) {
            response.setUserId(farmer.getUsers().getId());
        }
        return response;
    }

    public Optional<FarmerResponseDTO> getFarmerByUserId(Long userId) {
        return farmerRepo.findByUsers_id(userId)
                .map(this::mapToResponseDTO);
    }

    private FarmerResponseDTO mapToResponseDTO(Farmer farmer) {
        return FarmerResponseDTO.builder()
                .farmerId(farmer.getFarmerId())
                .name(farmer.getName())
                .email(farmer.getEmail())
                .dob(farmer.getDob())
                .gender(farmer.getGender())
                .address(farmer.getAddress())
                .contactInfo(farmer.getContactInfo())
                .landDetails(farmer.getLandDetails())
                .status(farmer.getStatus())

                .userId(farmer.getUsers().getId())
                .build();
    }

    public Farmer getFarmerById(Long farmerId) {
        return farmerRepo.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException(
                        "The requested Farmer profile (ID: " + farmerId + ") does not exist in our records or may have been removed."));
    }

    public List<Farmer> getAllFarmers() { return farmerRepo.findAll(); }
    public Farmer updateFarmer(Farmer farmer) { return farmerRepo.save(farmer); }
    public void deleteFarmer(Long farmerId) { farmerRepo.deleteById(farmerId); }
}