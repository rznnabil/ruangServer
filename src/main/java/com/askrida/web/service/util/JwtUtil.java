package com.askrida.web.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JWT Utility — Spring Bean.
 * Secret key diambil dari application.properties (monitor.jwt.secret).
 * Inject via @Autowired, bukan static call.
 */
@Component
public class JwtUtil {

    @Value("${monitor.jwt.secret}")
    private String secretKey;

    @Value("${monitor.jwt.expiration-ms:86400000}")
    private long expirationMs;

    private static final String ALGORITHM = "HmacSHA256";

    // ── Token Generation ─────────────────────────────────────

    public String generateToken(String nim, String nama, String role) {
        try {
            long now = System.currentTimeMillis();
            long exp = now + expirationMs;

            String header  = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            String payloadJson = String.format(
                "{\"nim\":\"%s\",\"nama\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                escapeJson(nim), escapeJson(nama), escapeJson(role), now / 1000, exp / 1000
            );
            String payload   = base64UrlEncode(payloadJson);
            String signature = hmacSha256(header + "." + payload, secretKey);

            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    // ── Token Validation ─────────────────────────────────────

    public JwtClaims validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }

            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // Verify signature
            String expectedSignature = hmacSha256(parts[0] + "." + parts[1], secretKey);
            if (!expectedSignature.equals(parts[2])) {
                return null; // Invalid signature
            }

            // Decode payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // Parse claims manually
            JwtClaims claims = new JwtClaims();
            claims.nim = extractJsonValue(payloadJson, "nim");
            claims.nama = extractJsonValue(payloadJson, "nama");
            claims.role = extractJsonValue(payloadJson, "role");

            // Check expiration
            String expStr = extractJsonValue(payloadJson, "exp");
            if (expStr != null) {
                long exp = Long.parseLong(expStr);
                if (System.currentTimeMillis() / 1000 > exp) {
                    return null; // Token expired
                }
            }

            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    public JwtClaims extractFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return validateToken(authHeader.substring(7));
    }

    // ── Helpers ──────────────────────────────────────────────

    private String base64UrlEncode(String data) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(':', keyIndex + searchKey.length());
        if (colonIndex == -1) return null;

        // Skip whitespace after colon
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        if (valueStart >= json.length()) return null;

        if (json.charAt(valueStart) == '"') {
            // String value
            int valueEnd = json.indexOf('"', valueStart + 1);
            if (valueEnd == -1) return null;
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // Number or boolean value
            int valueEnd = valueStart;
            while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * JWT Claims data class
     */
    public static class JwtClaims {
        public String nim;
        public String nama;
        public String role;

        public boolean isAdmin() {
            return "ADMIN".equalsIgnoreCase(role);
        }
    }
}
