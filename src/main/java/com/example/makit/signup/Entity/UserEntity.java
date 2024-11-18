package com.example.makit.signup.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 사용자 ID (DB 자동 생성)

    private String email;         // 이메일
    private String password;      // 비밀번호
    private String nickname;      // 닉네임
    private String phoneNumber;   // 전화번호
    private String selectedFields; // 선택한 분야
    private String selectedGenres; // 선택한 장르

    // 필요한 다른 정보들을 여기에 추가 예정
}
