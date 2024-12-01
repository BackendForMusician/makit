package com.example.makit.findid.Service;

import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.signup.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindIdService {

    private final UserRepository userRepository;

    public FindIdService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public String findEmailByPhoneNumber(String phoneNumber) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);
        return user != null ? user.getEmail() : null;
    }
}
