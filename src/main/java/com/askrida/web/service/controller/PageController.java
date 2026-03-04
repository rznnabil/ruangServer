package com.askrida.web.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.askrida.web.service.repository.RepositoryTes;
import com.askrida.web.service.repository.ServerUserRepository;
import com.askrida.web.service.model.ServerUser;
import com.askrida.web.service.model.RestResult;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private RepositoryTes repTes;

    @Autowired
    private ServerUserRepository serverUserRepo;

    // ===== Helper: Check if logged in =====
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedIn") != null;
    }

    private String getRole(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null ? role.toString() : "";
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(getRole(session));
    }

    // ===== ADMIN DASHBOARD =====
    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        // If USER role, redirect to user dashboard
        if (!isAdmin(session)) {
            return "redirect:/user-dashboard";
        }
        List<RestResult> dataAbsensi;
        try {
            dataAbsensi = repTes.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[Dashboard] Error loading absensi: " + e.getMessage());
            dataAbsensi = new ArrayList<>();
        }
        model.addAttribute("dataAbsensi", dataAbsensi);
        model.addAttribute("totalAbsensi", dataAbsensi.size());

        long hadir = dataAbsensi.stream().filter(r -> r.getWaktu_input() != null).count();
        model.addAttribute("totalHadir", hadir);
        model.addAttribute("totalRuangan", 4);
        model.addAttribute("username", session.getAttribute("username"));
        return "dashboard";
    }

    @GetMapping("/monitoring")
    public String monitoring(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/user-dashboard";
        }
        List<RestResult> dataAbsensi;
        try {
            dataAbsensi = repTes.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[Monitoring] Error loading absensi: " + e.getMessage());
            dataAbsensi = new ArrayList<>();
        }
        model.addAttribute("dataAbsensi", dataAbsensi);
        model.addAttribute("username", session.getAttribute("username"));
        return "dashboard";
    }

    // ===== USER DASHBOARD =====
    @GetMapping("/user-dashboard")
    public String userDashboard(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        // If ADMIN, redirect to admin dashboard
        if (isAdmin(session)) {
            return "redirect:/";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("userNim", session.getAttribute("userNim"));
        model.addAttribute("userRole", session.getAttribute("role"));
        return "user-dashboard";
    }

    // ===== ADMIN-ONLY PAGES =====
    @GetMapping("/face-register")
    public String faceRegister(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/user-dashboard";
        }
        return "face-register";
    }

    @GetMapping("/face-absensi")
    public String faceAbsensi(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/user-dashboard";
        }
        return "face-absensi";
    }

    // ===== SHARED PAGES (both roles) =====
    @GetMapping("/server-monitor")
    public String serverMonitor(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        return "server-monitor";
    }

    @GetMapping("/server-monitor-register")
    public String serverMonitorRegister(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/user-dashboard";
        }
        return "server-monitor-register";
    }

    @GetMapping("/user-management")
    public String userManagement(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        return "user-management";
    }

    // ===== ADMIN-ONLY PAGES =====
    // NOTE: /property, /project, /language, /chatbot, /music, /expense
    //       are handled by their own controllers
    //       (PropertyController, ProjectController, LanguageController,
    //        ChatController, MusicController, ExpenseController)

    // ===== LOGIN & LOGOUT =====
    @GetMapping("/login")
    public String login(HttpSession session) {
        if (isLoggedIn(session)) {
            return isAdmin(session) ? "redirect:/" : "redirect:/user-dashboard";
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> processLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(defaultValue = "ADMIN") String role,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (role.equalsIgnoreCase("ADMIN")) {
            // === ADMIN LOGIN ===
            // 1. Try hardcoded admin
            if ("admin".equals(username) && "admin123".equals(password)) {
                session.setAttribute("loggedIn", true);
                session.setAttribute("username", "admin");
                session.setAttribute("role", "ADMIN");
                session.setAttribute("userNim", "admin");
                response.put("success", true);
                response.put("message", "Login admin berhasil");
                response.put("role", "ADMIN");
                response.put("redirect", "/");
                return response;
            }
            // 2. Try server_users with ADMIN role
            try {
                ServerUser user = serverUserRepo.findByNim(username);
                if (user != null && "ADMIN".equalsIgnoreCase(user.getRole()) && user.isActive()) {
                    // Check password (if password_hash is set)
                    if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                        if (!password.equals(user.getPasswordHash())) {
                            response.put("success", false);
                            response.put("message", "Password salah");
                            return response;
                        }
                    }
                    session.setAttribute("loggedIn", true);
                    session.setAttribute("username", user.getNama());
                    session.setAttribute("role", "ADMIN");
                    session.setAttribute("userNim", user.getNim());
                    response.put("success", true);
                    response.put("message", "Login admin berhasil");
                    response.put("role", "ADMIN");
                    response.put("redirect", "/");
                    return response;
                }
            } catch (Exception e) {
                System.err.println("[Login] Error checking server_users: " + e.getMessage());
            }
            response.put("success", false);
            response.put("message", "Username/password admin salah atau bukan role ADMIN");

        } else {
            // === USER LOGIN ===
            try {
                ServerUser user = serverUserRepo.findByNim(username);
                if (user == null) {
                    response.put("success", false);
                    response.put("message", "NIM tidak ditemukan. Silakan daftar terlebih dahulu.");
                    return response;
                }
                if (!user.isActive()) {
                    response.put("success", false);
                    response.put("message", "Akun Anda tidak aktif. Hubungi admin.");
                    return response;
                }
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    response.put("success", false);
                    response.put("message", "Akun ini adalah ADMIN. Silakan login melalui tab Admin.");
                    return response;
                }
                // Check password (if password_hash is set)
                if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                    if (!password.equals(user.getPasswordHash())) {
                        response.put("success", false);
                        response.put("message", "Password salah");
                        return response;
                    }
                }
                // Login success
                session.setAttribute("loggedIn", true);
                session.setAttribute("username", user.getNama());
                session.setAttribute("role", "USER");
                session.setAttribute("userNim", user.getNim());
                response.put("success", true);
                response.put("message", "Login berhasil, selamat datang " + user.getNama());
                response.put("role", "USER");
                response.put("redirect", "/user-dashboard");
            } catch (Exception e) {
                System.err.println("[Login] Error: " + e.getMessage());
                response.put("success", false);
                response.put("message", "Terjadi kesalahan server");
            }
        }
        return response;
    }
}
