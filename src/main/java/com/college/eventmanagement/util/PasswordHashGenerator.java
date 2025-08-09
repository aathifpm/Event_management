package com.college.eventmanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Matches existing hash: " + encoder.matches(password, "$2a$10$eImiTXuWVxfM37uY4JANjOL4uFOi6L4C8.P8PvVGqhjhKzWE8LKXy"));
    }
}
