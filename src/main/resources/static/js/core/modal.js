/**
 * Modal Component
 *
 * Penggunaan — Konfirmasi hapus:
 *   const confirmed = await Modal.confirm('Hapus data ini?', 'Aksi ini tidak bisa dibatalkan.');
 *   if (confirmed) { ... }
 *
 * Penggunaan — Modal form:
 *   Modal.open('modal-id');
 *   Modal.close('modal-id');
 */

class ModalManager {
    /**
     * Tampilkan dialog konfirmasi.
     * Mengembalikan Promise<boolean>.
     */
    confirm(title, message = '', confirmText = 'Ya, Lanjutkan', dangerMode = true) {
        return new Promise((resolve) => {
            // Hapus modal lama jika ada
            const existing = document.getElementById('__confirm-modal');
            if (existing) existing.remove();

            const overlay = document.createElement('div');
            overlay.id = '__confirm-modal';
            overlay.className = 'modal-overlay';
            overlay.innerHTML = `
                <div class="modal" style="max-width:420px">
                    <div class="modal-header">
                        <h5 style="font-size:16px;font-weight:600">${title}</h5>
                        <button class="modal-close" id="__confirm-x">×</button>
                    </div>
                    <div class="modal-body">
                        <p style="font-size:14px;color:#64748b">${message}</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-outline" id="__confirm-cancel">Batal</button>
                        <button class="btn ${dangerMode ? 'btn-danger' : 'btn-primary'}" id="__confirm-ok">
                            ${confirmText}
                        </button>
                    </div>
                </div>
            `;

            document.body.appendChild(overlay);
            requestAnimationFrame(() => overlay.classList.add('active'));

            const cleanup = (result) => {
                overlay.classList.remove('active');
                setTimeout(() => overlay.remove(), 250);
                resolve(result);
            };

            document.getElementById('__confirm-ok').onclick     = () => cleanup(true);
            document.getElementById('__confirm-cancel').onclick = () => cleanup(false);
            document.getElementById('__confirm-x').onclick      = () => cleanup(false);
            overlay.onclick = (e) => { if (e.target === overlay) cleanup(false); };
        });
    }

    /** Buka modal berdasarkan ID elemen */
    open(modalId) {
        const overlay = document.getElementById(modalId);
        if (overlay) {
            overlay.classList.add('active');
            overlay.style.display = 'flex';
        }
    }

    /** Tutup modal berdasarkan ID elemen */
    close(modalId) {
        const overlay = document.getElementById(modalId);
        if (overlay) {
            overlay.classList.remove('active');
            setTimeout(() => { overlay.style.display = ''; }, 250);
        }
    }
}

window.Modal = new ModalManager();
export const Modal = window.Modal;
