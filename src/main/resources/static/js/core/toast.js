/**
 * Toast Notification Component
 *
 * Penggunaan:
 *   Toast.show('Data berhasil disimpan', 'success');
 *   Toast.show('Terjadi kesalahan', 'error');
 *   Toast.show('Perhatian!', 'warning');
 *   Toast.show('Info baru', 'info');
 */

const ICONS = {
    success: '✅',
    error:   '❌',
    warning: '⚠️',
    info:    'ℹ️',
};

class ToastManager {
    constructor() {
        this.container = null;
    }

    _ensureContainer() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        }
    }

    show(message, type = 'info', duration = 4000) {
        this._ensureContainer();

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span style="font-size:18px;flex-shrink:0">${ICONS[type] || 'ℹ️'}</span>
            <div style="flex:1">
                <div style="font-size:14px;font-weight:500;color:#1e293b">${message}</div>
            </div>
            <button onclick="this.closest('.toast').remove()"
                    style="background:none;border:none;cursor:pointer;color:#94a3b8;font-size:18px;padding:0;line-height:1;flex-shrink:0">
                ×
            </button>
        `;

        this.container.appendChild(toast);

        // Auto-remove after duration
        if (duration > 0) {
            setTimeout(() => {
                toast.style.animation = 'toastSlideIn 0.3s ease reverse';
                setTimeout(() => toast.remove(), 280);
            }, duration);
        }
    }

    success(msg, duration) { this.show(msg, 'success', duration); }
    error  (msg, duration) { this.show(msg, 'error',   duration); }
    warning(msg, duration) { this.show(msg, 'warning', duration); }
    info   (msg, duration) { this.show(msg, 'info',    duration); }
}

// Global singleton — gunakan window.Toast.show(...)
window.Toast = new ToastManager();
export const Toast = window.Toast;
