package com.cts.Registration_Service.dto.response;

import com.cts.Registration_Service.enums.FarmerGender;
import com.cts.Registration_Service.enums.FarmerStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerResponseDTO {

    private Long farmerId;
    private Long userId; // Representing the associated User by ID only
    private String name;
    private LocalDate dob;
    private String email;
    private String contactInfo;
    private FarmerGender gender;
    private String address;
    private String landDetails;
    private FarmerStatus status;
    private List<FarmerDocumentResponseDTO> documents;

}