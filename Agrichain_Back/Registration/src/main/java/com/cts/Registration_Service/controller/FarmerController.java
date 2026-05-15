package com.cts.Registration_Service.controller;

import com.cts.Registration_Service.dto.response.FarmerDocumentResponseDTO;
import com.cts.Registration_Service.dto.response.FarmerResponseDTO;
import com.cts.Registration_Service.entity.Farmer;
import com.cts.Registration_Service.service.FarmerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInput;
import java.util.List;

@RestController
@RequestMapping("/farmers")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;


    @PostMapping(value="/register/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FarmerResponseDTO> completeRegistration(
            @PathVariable Long userId,
            @RequestPart("farmer") FarmerResponseDTO farmerDto,
            @RequestPart(value = "idProof", required = false) MultipartFile idProof,
            @RequestPart(value = "landRecord", required = false) MultipartFile landRecord) {
        try {

            Farmer farmer=new Farmer();
            farmer.setStatus(farmerDto.getStatus());
            farmer.setFarmerId(farmerDto.getFarmerId());
            farmer.setDob(farmerDto.getDob());
            farmer.setAddress(farmerDto.getAddress());
            farmer.setGender(farmerDto.getGender());
            farmer.setLandDetails(farmerDto.getLandDetails());

            System.out.println("before inserting");
            Farmer updatedFarmer = farmerService.completeFarmerProfile(userId, farmer, idProof, landRecord);
            return ResponseEntity.ok(farmerService.mapToResponse(updatedFarmer));
        } catch (Exception e) {
            System.out.println("catch vloxke");
            System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-by-userid/{id}")
    public ResponseEntity<FarmerResponseDTO> getFarmerByUserId(@PathVariable("id") Long id) {
        return farmerService.getFarmerByUserId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmerResponseDTO> getFarmerById(@PathVariable Long id) {
        Farmer farmer = farmerService.getFarmerById(id);
        return ResponseEntity.ok(farmerService.mapToResponse(farmer));
    }

    @GetMapping
    public List<FarmerResponseDTO> getAllFarmers() {
        List<Farmer> farmers = farmerService.getAllFarmers();

        return farmers.stream().map(farmer -> {
            // Map the documents first
            List<FarmerDocumentResponseDTO> docDtos = farmer.getDocuments().stream()
                    .map(doc -> FarmerDocumentResponseDTO.builder()
                            .documentId(doc.getId())
                            .docType(doc.getDocType())
                            .content(doc.getContent())
                            .verificationStatus(doc.getVerificationStatus())
                            .build())
                    .toList();

            // Build the FarmerResponseDTO
            return FarmerResponseDTO.builder()
                    .farmerId(farmer.getFarmerId())
                    .userId(farmer.getUsers() != null ? farmer.getUsers().getId() : null)
                    .name(farmer.getName())
                    .dob(farmer.getDob())
                    .email(farmer.getEmail())
                    .contactInfo(farmer.getContactInfo())
                    .gender(farmer.getGender())
                    .address(farmer.getAddress())
                    .landDetails(farmer.getLandDetails())
                    .status(farmer.getStatus())
                    .documents(docDtos)
                    .build();
        }).toList();
    }

    @PutMapping("/{id}")
    public Farmer updateFarmer(@PathVariable Long id, @RequestBody Farmer farmer) {
        farmer.setFarmerId(id);
        return farmerService.updateFarmer(farmer);
    }

    @DeleteMapping("/{id}")
    public void deleteFarmer(@PathVariable Long id) {
        farmerService.deleteFarmer(id);
    }

}