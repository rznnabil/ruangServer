package com.askrida.web.service.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility untuk hashing dan verifikasi password menggunakan BCrypt.
 * Gunakan encode() saat menyimpan password baru ke database.
 * Gunakan matches() saat memverifikasi login.
 */
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    /**
     * Hash password plain-text → BCrypt hash.
     */
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Verifikasi password plain-text terhadap hash yang tersimpan di DB.
     */
    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        // Backward-compat: jika hash belum tersimpan sebagai BCrypt, coba plain-text dulu
        if (!hashedPassword.startsWith("$2a$") && !hashedPassword.startsWith("$2b$")) {
            return rawPassword.equals(hashedPassword);
        }
        return encoder.matches(rawPassword, hashedPassword);
    }
}
