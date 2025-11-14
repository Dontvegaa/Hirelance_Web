document.addEventListener('DOMContentLoaded', function() {

    // 1. Referencias a los elementos del modal
    const modal = document.getElementById('delete-modal');
    const confirmBtn = document.getElementById('delete-modal-confirm-btn');
    const messageP = document.getElementById('delete-modal-message');

    // 2. Variable global para saber qué formulario enviar
    let formToSubmit = null;

    // 3. Función global para ABRIR el modal
    // (La hacemos global para que onclick="" pueda encontrarla)
    window.openDeleteModal = function(formId, message) {
        if (!modal || !messageP) return;

        // Encontrar el formulario específico por su ID
        formToSubmit = document.getElementById(formId);

        // Poner el mensaje de confirmación
        messageP.textContent = message;

        // Mostrar el modal
        modal.classList.remove('hidden');
    }

    // 4. Función global para CERRAR el modal
    window.closeDeleteModal = function() {
        if (!modal) return;
        modal.classList.add('hidden');
        formToSubmit = null;
    }

    // 5. Añadir el "click listener" al botón de confirmar
    if (confirmBtn) {
        confirmBtn.addEventListener('click', () => {
            if (formToSubmit) {
                formToSubmit.submit();
            }
        });
    }

    // 6. Añadir listener para el botón de cancelar dentro del modal
    const cancelBtn = document.getElementById('delete-modal-cancel-btn');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', closeDeleteModal);
    }

    // 7. (Opcional) Añadir listener al botón 'X'
    const closeBtn = document.getElementById('delete-modal-close-btn');
    if (closeBtn) {
        closeBtn.addEventListener('click', closeDeleteModal);
    }
});