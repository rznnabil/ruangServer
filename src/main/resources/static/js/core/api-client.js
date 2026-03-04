/**
 * API Client — Central fetch wrapper
 *
 * Semua request ke backend harus melalui fungsi apiCall() ini.
 * Fitur:
 * - Otomatis attach Authorization header (JWT dari localStorage)
 * - Handle 401 → redirect ke login
 * - Handle 403 → tampilkan pesan akses ditolak
 * - Standarisasi error handling
 * - Logging request di development mode
 *
 * Contoh penggunaan:
 *   import { api } from './api-client.js';
 *   const result = await api.get('/api/v1/members');
 *   const result = await api.post('/api/v1/attendance', { nim: '123' });
 */

const API_BASE_URL = '';   // kosong = same origin
const TOKEN_KEY    = 'app_jwt_token';
const DEV_MODE     = window.location.hostname === 'localhost';

// ── Token Management ──────────────────────────────────────────────

export const auth = {
    getToken:    ()        => localStorage.getItem(TOKEN_KEY),
    setToken:    (token)   => localStorage.setItem(TOKEN_KEY, token),
    removeToken: ()        => localStorage.removeItem(TOKEN_KEY),
    isLoggedIn:  ()        => !!localStorage.getItem(TOKEN_KEY),
};

// ── Core fetch wrapper ────────────────────────────────────────────

async function apiCall(method, url, body = null, options = {}) {
    const token = auth.getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...options.headers,
    };

    const config = {
        method,
        headers,
        ...(body !== null ? { body: JSON.stringify(body) } : {}),
    };

    if (DEV_MODE) {
        console.debug(`[API] ${method} ${url}`, body || '');
    }

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, config);

        // Handle auth errors globally
        if (response.status === 401) {
            auth.removeToken();
            if (!options.skipRedirect) {
                window.location.href = '/login';
            }
            return { success: false, message: 'Sesi habis. Silakan login kembali.' };
        }

        if (response.status === 403) {
            Toast.show('Akses ditolak: Anda tidak memiliki izin untuk aksi ini.', 'error');
            return { success: false, message: 'Akses ditolak' };
        }

        // Parse response
        const contentType = response.headers.get('Content-Type') || '';
        let data;
        if (contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = { success: response.ok, message: await response.text() };
        }

        // Log error response in dev mode
        if (DEV_MODE && !data.success) {
            console.warn(`[API] Error response:`, data);
        }

        return data;

    } catch (error) {
        console.error(`[API] Network error for ${method} ${url}:`, error);
        Toast.show('Koneksi gagal. Periksa koneksi internet Anda.', 'error');
        return { success: false, message: 'Network error: ' + error.message };
    }
}

// ── Convenience methods ───────────────────────────────────────────

export const api = {
    get:    (url, options)         => apiCall('GET',    url, null, options),
    post:   (url, body, options)   => apiCall('POST',   url, body, options),
    put:    (url, body, options)   => apiCall('PUT',    url, body, options),
    delete: (url, options)         => apiCall('DELETE', url, null, options),
    patch:  (url, body, options)   => apiCall('PATCH',  url, body, options),
};

// ── Form Data upload (multipart) ──────────────────────────────────

export async function apiUpload(url, formData) {
    const token = auth.getToken();
    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            method: 'POST',
            headers: token ? { 'Authorization': `Bearer ${token}` } : {},
            body: formData,
        });
        return await response.json();
    } catch (error) {
        return { success: false, message: 'Upload gagal: ' + error.message };
    }
}
