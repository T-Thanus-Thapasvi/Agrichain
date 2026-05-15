package com.cts.Registration_Service.dao;

import com.cts.Registration_Service.dto.response.UserResponseDTO;
import com.cts.Registration_Service.entity.Users;
import com.cts.Registration_Service.enums.UserRole;
import com.cts.Registration_Service.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByPhone(String phone);
    Optional<Users> findByRole(UserRole role);

    @Query("SELECT new com.cts.Registration_Service.dto.response.UserResponseDTO(u.id, u.name, u.email, u.phone, u.role, u.status) " +
            "FROM Users u WHERE u.email = :email")
    Optional<UserResponseDTO> findUserByEmail(String  email);
}