document.addEventListener('DOMContentLoaded', () => {

    // Clases de estilo para las pestañas
    const activeClasses = 'border-orange-500 text-orange-500';
    const inactiveClasses = 'border-transparent text-gray-400 hover:text-white hover:border-gray-700';

    // Función para cambiar de pestaña
    window.changeTab = function(button, tabId) {
        // Ocultar todos los paneles
        document.querySelectorAll('.tab-panel').forEach(panel => {
            panel.classList.add('hidden');
        });

        // Desactivar todos los botones
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove(...activeClasses.split(' '));
            btn.classList.add(...inactiveClasses.split(' '));
        });

        // Mostrar el panel correcto
        const panelToShow = document.getElementById('tab-' + tabId);
        if (panelToShow) {
            panelToShow.classList.remove('hidden');
        }

        // Activar el botón correcto
        if (button) {
            button.classList.remove(...inactiveClasses.split(' '));
            button.classList.add(...activeClasses.split(' '));
        }
    }

    // Inicializar (asegurarse de que las clases por defecto estén bien)
    const defaultActiveBtn = document.querySelector('.active-tab-btn');
    if (defaultActiveBtn) {
        defaultActiveBtn.classList.remove(...inactiveClasses.split(' '));
        defaultActiveBtn.classList.add(...activeClasses.split(' '));
    }
});