/**
 * Absensi Service — JS module
 * Memanggil endpoint /rest/**, /api/face/**
 */

import { api } from '../core/api-client.js';

export const absensiService = {

    /** Semua data absensi */
    getAll: () => api.get('/rest/'),

    /** Single record */
    getById: (id) => api.get(`/rest/${id}`),

    /** Clock in */
    masuk: (data) => api.post('/rest/masuk', data),

    /** Clock out */
    keluar: (data) => api.post('/rest/keluar', data),

    /** Smart clock-in/out (cek otomatis) */
    absensi: (data) => api.post('/rest/absensi', data),

    /** Tambah anggota */
    tambahAnggota: (data) => api.post('/rest/tambahanggota', data),

    /** Update anggota */
    updateAnggota: (data) => api.post('/rest/updateanggota', data),

    /** Hapus data */
    deleteData: (data) => api.post('/rest/deletedata', data),
};

export const faceService = {

    /** Semua data face untuk client-side matching */
    getAll: () => api.get('/api/face/all'),

    /** Face data berdasarkan NIM */
    getByNim: (nim) => api.get(`/api/face/${nim}`),

    /** Register wajah baru */
    register: (data) => api.post('/api/face/register', data),

    /** Absensi via face recognition */
    absensi: (data) => api.post('/api/face/absensi', data),

    /** Jumlah wajah terdaftar per NIM */
    countByNim: (nim) => api.get(`/api/face/count/${nim}`),

    /** Hapus satu face */
    deleteById: (id) => api.delete(`/api/face/${id}`),

    /** Hapus semua face by NIM */
    deleteByNim: (nim) => api.delete(`/api/face/nim/${nim}`),
};
