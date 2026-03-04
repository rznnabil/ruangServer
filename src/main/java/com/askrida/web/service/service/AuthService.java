package com.askrida.web.service.service;

import com.askrida.web.service.model.ServerUser;
import com.askrida.web.service.repository.ServerUserRepository;
import com.askrida.web.service.util.PasswordUtil;
import com.askrida.web.service.exception.ResourceNotFoundException;
import com.askrida.web.service.exception.UnauthorizedException;
import com.askrida.web.service.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

/**
 * Business logic untuk autentikasi dan manajemen session.
 * Menggantikan logika login yang sebelumnya ada di PageController.
 */
@Service
public class AuthService {

    @Autowired
    private ServerUserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    /**
     * Login admin: cek credential bawaan atau dari DB (role ADMIN).
     */
    public Map<String, Object> loginAdmin(String username, String password, HttpSession session) {
        // Cek admin credential dari config
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            setSession(session, "admin", "ADMIN", adminUsername);
            return buildSuccess("Login admin berhasil", "ADMIN", "/");
        }

        // Cek dari server_users dengan role ADMIN
        ServerUser user = userRepository.findByNim(username);
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole()) && user.isActive()) {
            if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                if (!passwordUtil.matches(password, user.getPasswordHash())) {
                    return buildError("Password salah");
                }
            }
            setSession(session, user.getNama(), "ADMIN", user.getNim());
            return buildSuccess("Login admin berhasil", "ADMIN", "/");
        }

        return buildError("Username/password admin salah atau bukan role ADMIN");
    }

    /**
     * Login user biasa (role USER).
     */
    public Map<String, Object> loginUser(String username, String password, HttpSession session) {
        ServerUser user = userRepository.findByNim(username);
        if (user == null) {
            return buildError("NIM tidak ditemukan. Silakan daftar terlebih dahulu.");
        }
        if (!user.isActive()) {
            return buildError("Akun Anda tidak aktif. Hubungi admin.");
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return buildError("Akun ini adalah ADMIN. Silakan login melalui tab Admin.");
        }
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            if (!passwordUtil.matches(password, user.getPasswordHash())) {
                return buildError("Password salah");
            }
        }
        setSession(session, user.getNama(), user.getRole(), user.getNim());
        return buildSuccess("Login berhasil", user.getRole(), "/user-dashboard");
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void setSession(HttpSession session, String nama, String role, String nim) {
        session.setAttribute("loggedIn", true);
        session.setAttribute("username", nama);
        session.setAttribute("role", role);
        session.setAttribute("userNim", nim);
    }

    private Map<String, Object> buildSuccess(String message, String role, String redirect) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("message", message);
        r.put("role", role);
        r.put("redirect", redirect);
        return r;
    }

    private Map<String, Object> buildError(String message) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", false);
        r.put("message", message);
        return r;
    }
}
