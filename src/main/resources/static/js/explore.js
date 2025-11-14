// explore.js - Funcionalidad para la página de exploración

document.addEventListener('DOMContentLoaded', function() {
    // Configuración de búsqueda en tiempo real (opcional)
    const searchInput = document.getElementById('searchInput');
    const searchForm = document.getElementById('searchForm');

    if (searchInput && searchForm) {
        // Opcional: Búsqueda en tiempo real con debounce
        let searchTimeout;
        searchInput.addEventListener('input', function(e) {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                // Si quieres búsqueda en tiempo real, descomenta esta línea:
                // searchForm.submit();
            }, 500);
        });

        // Enter para buscar inmediatamente
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                searchForm.submit();
            }
        });
    }

    // Efectos de hover para las tarjetas
    const projectCards = document.querySelectorAll('.bg-gray-800.rounded-lg');
    projectCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.transition = 'transform 0.3s ease';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Animación para los filtros
    const filterButtons = document.querySelectorAll('a[href*="categoria="]');
    filterButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Agregar efecto de loading opcional
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Cargando...';

            setTimeout(() => {
                this.innerHTML = originalText;
            }, 1000);
        });
    });

    // Smooth scroll para las secciones
    function smoothScrollToSection(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
            section.scrollIntoView({ behavior: 'smooth' });
        }
    }

    // Mostrar mensajes de estado
    function showMessage(message, type = 'info') {
        const messageDiv = document.createElement('div');
        messageDiv.className = `fixed top-4 right-4 p-4 rounded-md z-50 ${
            type === 'success' ? 'bg-green-500' :
                type === 'error' ? 'bg-red-500' : 'bg-blue-500'
        } text-white`;
        messageDiv.textContent = message;

        document.body.appendChild(messageDiv);

        setTimeout(() => {
            messageDiv.remove();
        }, 3000);
    }

    // Manejar clicks en enlaces deshabilitados
    document.querySelectorAll('a.bg-gray-600').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            showMessage('Este perfil no está disponible temporalmente', 'error');
        });
    });

    // Cargar más proyectos (para futura implementación de paginación)
    function loadMoreProjects() {
        // Aquí puedes implementar carga infinita cuando la tengas en el backend
        console.log('Cargar más proyectos...');
    }

    // Intersection Observer para carga infinita (opcional)
    if ('IntersectionObserver' in window) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    // loadMoreProjects(); // Descomentar cuando implementes paginación
                }
            });
        });

        const sentinel = document.createElement('div');
        sentinel.id = 'load-more-sentinel';
        document.querySelector('.grid')?.appendChild(sentinel);
        observer.observe(sentinel);
    }

    console.log('Hirelance Explore - JavaScript cargado correctamente');
});