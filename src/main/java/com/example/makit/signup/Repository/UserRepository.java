package com.example.makit.signup.Repository;

import com.example.makit.signup.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
