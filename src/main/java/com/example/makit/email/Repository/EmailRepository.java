package com.example.makit.email.Repository;

import com.example.makit.email.Entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    EmailEntity findByEmail(String email);
}