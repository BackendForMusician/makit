package com.example.makit.login.Controller;

import com.example.makit.login.DTO.LoginRequest;
import com.example.makit.login.Service.LoginService;
import com.example.makit.login.Util.SessionUtil;
import com.example.makit.signup.Entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    @PostMapping( "/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginUser, HttpServletRequest request) {

        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        if (session != null && session.getAttribute("loginMember") != null) {
            return ResponseEntity.status(400).body("Already logged in");
        }
        // 사용자 인증
        return loginService.authenticate(loginUser.getEmail(), loginUser.getPassword())
                .map(user -> {
                    HttpSession newSession = request.getSession(true); // 새로운 세션 생성
                    SessionUtil.saveUserToSession(newSession, user);
                    return ResponseEntity.ok("Login successful");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid credentials"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate(); // 세션 무효화
        return ResponseEntity.ok("Logout successful.");
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        UserEntity loginMember = SessionUtil.getUserFromSession(request.getSession(false));
        if (loginMember == null) {
            return ResponseEntity.badRequest().body("Not logged in");
        }
        return ResponseEntity.ok("Logged in as: " + loginMember.getEmail());
    }
}