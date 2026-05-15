package com.cts.Registration_Service.controller;

import com.cts.Registration_Service.dto.request.LoginRequestDTO;
import com.cts.Registration_Service.dto.request.UserRequestDTO;
import com.cts.Registration_Service.dto.request.AuditLogRequestDTO;
import com.cts.Registration_Service.dto.response.UserResponseDTO;
import com.cts.Registration_Service.service.UserService;
import com.cts.Registration_Service.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDTO dto) {
        String statusMessage = userService.registerUser(dto);
        return ResponseEntity.ok(Collections.singletonMap("message", statusMessage));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        String token = userService.validateLogin(dto);
        String userHandle = Optional.ofNullable(dto.getEmail()).orElse("Unknown");
        UserResponseDTO user = userService.findUserByEmail(dto.getEmail());

        // Local Audit Logging
        try {
            AuditLogRequestDTO auditDTO = AuditLogRequestDTO.builder()
                    .performedBy(userHandle)
                    .action("LOGIN")
                    .role(String.valueOf(user.getRole()))
                    .details("User logged in successfully via Authentication endpoint")
                    .timestamp(LocalDateTime.now())
                    .build();
            auditLogService.logAction(auditDTO);
        } catch (Exception e) {
            System.err.println("Login Audit failed: " + e.getMessage());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or " +
            "@userService.fetchUserById(#id).email == authentication.name")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO updatedUser = userService.updateUser(id, dto);

        // Audit Logging for Update
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            AuditLogRequestDTO auditDTO = AuditLogRequestDTO.builder()
                    .performedBy(currentUser)
                    .action("UPDATE_USER")
                    .role("SYSTEM")
                    .details("Updated details for User ID: " + id)
                    .timestamp(LocalDateTime.now())
                    .build();
            auditLogService.logAction(auditDTO);
        } catch (Exception e) {
            System.err.println("Update Audit failed: " + e.getMessage());
        }

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'OFFICER', 'ADMIN', 'AUDITOR') or " +
            "#id.toString() == authentication.name or " +
            "@userService.fetchUserById(#id).email == authentication.name")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.fetchUserById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('MANAGER', 'OFFICER', 'ADMIN', 'AUDITOR')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}