package com.example.makit.signup.Entity;

import com.example.makit.signup.Converter.TermsAgreementConverter;
import com.example.makit.signup.DTO.TermsAgreement;
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
@Table(name = "USER_ENTITY")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private String userType = "USER";

    @OneToMany(mappedBy = "user")
    private Set<UserField> userFields = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserGenre> userGenres = new HashSet<>();

    @Convert(converter = TermsAgreementConverter.class)
    @Column(name = "terms_agreement_json", columnDefinition = "TEXT")
    private TermsAgreement termsAgreement;

    @Column(name = "registration", nullable = false)
    private boolean registration = false; // 기본값: false (구독하지 않은 사용자)

}
