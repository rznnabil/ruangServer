/**
 * Server Monitor Service — JS module
 * Memanggil endpoint /api/monitor/**
 */

import { api, auth } from '../core/api-client.js';
import { Toast }     from '../core/toast.js';

export const monitorService = {

    // ── Auth ─────────────────────────────────────────────────────

    login: (data) => api.post('/api/monitor/auth/login', data),

    register: (data) => api.post('/api/monitor/auth/register', data),

    /** Login dengan simpan token otomatis */
    async loginAndSave(data) {
        const result = await this.login(data);
        if (result.success && result.token) {
            auth.setToken(result.token);
        }
        return result;
    },

    logout() {
        auth.removeToken();
        window.location.href = '/login';
    },

    // ── Users ─────────────────────────────────────────────────────

    getUsers:          ()              => api.get('/api/monitor/users'),
    getUserByNim:      (nim)           => api.get(`/api/monitor/users/${nim}`),
    updateRole:        (nim, role)     => api.put(`/api/monitor/users/${nim}/role`, { role }),
    updateStatus:      (nim, active)   => api.put(`/api/monitor/users/${nim}/status`, { active }),
    deleteUser:        (nim)           => api.delete(`/api/monitor/users/${nim}`),

    // ── Face ──────────────────────────────────────────────────────

    verify:            (data)          => api.post('/api/monitor/verify', data),
    registerFace:      (data)          => api.post('/api/monitor/face/register', data),

    // ── Logs ──────────────────────────────────────────────────────

    getLogs:           ()              => api.get('/api/monitor/logs'),
    getTodayLogs:      ()              => api.get('/api/monitor/logs/today'),
    getLogsByNim:      (nim)           => api.get(`/api/monitor/logs/${nim}`),

    // ── Stats ─────────────────────────────────────────────────────

    getStats:          ()              => api.get('/api/monitor/stats'),
    getHourlyStats:    ()              => api.get('/api/monitor/stats/hourly'),
    getDailyStats:     ()              => api.get('/api/monitor/stats/daily'),
};
