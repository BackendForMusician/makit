package com.example.makit.login.Util;


import com.example.makit.signup.Entity.UserEntity;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String LOGIN_MEMBER = "loginMember";

    public static void saveUserToSession(HttpSession session, UserEntity user) {
        session.setAttribute(LOGIN_MEMBER, user);
    }

    public static UserEntity getUserFromSession(HttpSession session) {
        return (session != null) ? (UserEntity) session.getAttribute(LOGIN_MEMBER) : null;
    }
}