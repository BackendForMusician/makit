package com.example.makit.email.Repository;

import com.example.makit.email.Entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    Optional<EmailEntity> findByEmail(String email);

}