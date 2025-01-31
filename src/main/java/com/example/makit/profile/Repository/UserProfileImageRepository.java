package com.example.makit.profile.Repository;

import com.example.makit.profile.Entity.UserProfileImage;
import com.example.makit.signup.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {
    UserProfileImage findByUser(UserEntity user);
}
