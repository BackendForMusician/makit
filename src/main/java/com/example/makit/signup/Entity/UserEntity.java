package com.example.makit.signup.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
    private String userType = "USER"; //유저 권한

    @OneToMany(mappedBy = "user")
    private Set<UserField> userFields = new HashSet<>(); // 선택한 분야

    @OneToMany(mappedBy = "user")
    private Set<UserGenre> userGenres = new HashSet<>(); // 선택한 장르

    // 필요한 다른 정보들을 여기에 추가 예정
}
