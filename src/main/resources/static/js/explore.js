// Funcionalidad para pestañas de exploración
document.addEventListener('DOMContentLoaded', function() {
    // Tabs de proyectos/talentos
    const tabProyectos = document.getElementById('tab-proyectos');
    const tabTalentos = document.getElementById('tab-talentos');
    const proyectosSection = document.getElementById('proyectos-section');
    const talentosSection = document.getElementById('talentos-section');

    if (tabProyectos && tabTalentos) {
        tabProyectos.addEventListener('click', function() {
            this.classList.add('text-primary', 'border-primary');
            this.classList.remove('text-gray-400', 'border-transparent');

            tabTalentos.classList.remove('text-primary', 'border-primary');
            tabTalentos.classList.add('text-gray-400', 'border-transparent');

            proyectosSection.classList.remove('hidden');
            talentosSection.classList.add('hidden');
        });

        tabTalentos.addEventListener('click', function() {
            this.classList.add('text-primary', 'border-primary');
            this.classList.remove('text-gray-400', 'border-transparent');

            tabProyectos.classList.remove('text-primary', 'border-primary');
            tabProyectos.classList.add('text-gray-400', 'border-transparent');

            talentosSection.classList.remove('hidden');
            proyectosSection.classList.add('hidden');
        });
    }

    // Filtros de proyectos
    const projectFilters = document.querySelectorAll('.project-filter');
    const projectCards = document.querySelectorAll('[data-category]');

    projectFilters.forEach(filter => {
        filter.addEventListener('click', function() {
            const filterValue = this.getAttribute('data-filter');

            // Actualizar botones activos
            projectFilters.forEach(btn => {
                btn.classList.remove('bg-primary', 'text-white');
                btn.classList.add('bg-gray-700', 'hover:bg-gray-600');
            });
            this.classList.add('bg-primary', 'text-white');
            this.classList.remove('bg-gray-700', 'hover:bg-gray-600');

            // Filtrar proyectos
            projectCards.forEach(card => {
                if (filterValue === 'all' || card.getAttribute('data-category') === filterValue) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });

    // Filtros de talentos
    const talentFilters = document.querySelectorAll('.talent-filter');
    const talentCards = document.querySelectorAll('[data-skills]');

    talentFilters.forEach(filter => {
        filter.addEventListener('click', function() {
            const filterValue = this.getAttribute('data-filter');

            // Actualizar botones activos
            talentFilters.forEach(btn => {
                btn.classList.remove('bg-primary', 'text-white');
                btn.classList.add('bg-gray-700', 'hover:bg-gray-600');
            });
            this.classList.add('bg-primary', 'text-white');
            this.classList.remove('bg-gray-700', 'hover:bg-gray-600');

            // Filtrar talentos
            talentCards.forEach(card => {
                if (filterValue === 'all' || card.getAttribute('data-skills').includes(filterValue)) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
});
// explore.js - Funcionalidad específica para la página de exploración

document.addEventListener('DOMContentLoaded', function() {
    console.log('Explore.js cargado correctamente');
    initializeExploreTabs();
    initializeProjectFilters();
    initializeTalentFilters();
});

// Sistema de pestañas - VERSIÓN SIMPLIFICADA Y FUNCIONAL
function initializeExploreTabs() {
    const tabProyectos = document.getElementById('tab-proyectos');
    const tabTalentos = document.getElementById('tab-talentos');
    const proyectosSection = document.getElementById('proyectos-section');
    const talentosSection = document.getElementById('talentos-section');

    console.log('Inicializando pestañas:', { tabProyectos, tabTalentos, proyectosSection, talentosSection });

    if (tabProyectos && tabTalentos) {
        tabProyectos.addEventListener('click', function() {
            console.log('Click en pestaña Proyectos');
            switchTab('proyectos');
        });

        tabTalentos.addEventListener('click', function() {
            console.log('Click en pestaña Talentos');
            switchTab('talentos');
        });
    }
}

function switchTab(tabName) {
    const tabProyectos = document.getElementById('tab-proyectos');
    const tabTalentos = document.getElementById('tab-talentos');
    const proyectosSection = document.getElementById('proyectos-section');
    const talentosSection = document.getElementById('talentos-section');

    if (tabName === 'proyectos') {
        // Activar pestaña proyectos
        tabProyectos.classList.add('text-primary', 'border-primary');
        tabProyectos.classList.remove('text-gray-400', 'border-transparent');
        tabTalentos.classList.add('text-gray-400', 'border-transparent');
        tabTalentos.classList.remove('text-primary', 'border-primary');

        // Mostrar sección proyectos
        if (proyectosSection) proyectosSection.classList.remove('hidden');
        if (talentosSection) talentosSection.classList.add('hidden');

    } else {
        // Activar pestaña talentos
        tabTalentos.classList.add('text-primary', 'border-primary');
        tabTalentos.classList.remove('text-gray-400', 'border-transparent');
        tabProyectos.classList.add('text-gray-400', 'border-transparent');
        tabProyectos.classList.remove('text-primary', 'border-primary');

        // Mostrar sección talentos
        if (talentosSection) talentosSection.classList.remove('hidden');
        if (proyectosSection) proyectosSection.classList.add('hidden');
    }
}

// Filtros para proyectos - VERSIÓN SIMPLIFICADA
function initializeProjectFilters() {
    const filterButtons = document.querySelectorAll('.project-filter');
    console.log('Botones de filtro proyectos encontrados:', filterButtons.length);

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const filter = this.getAttribute('data-filter');
            console.log('Filtrando proyectos por:', filter);
            filterProjects(filter);

            // Update active filter button
            filterButtons.forEach(btn => {
                btn.classList.remove('bg-primary', 'text-white');
                btn.classList.add('bg-gray-700', 'text-white');
            });
            this.classList.add('bg-primary', 'text-white');
            this.classList.remove('bg-gray-700');
        });
    });
}

function filterProjects(category) {
    const projects = document.querySelectorAll('#proyectos-section [data-category]');
    console.log('Proyectos encontrados:', projects.length);

    projects.forEach(project => {
        if (category === 'all' || project.getAttribute('data-category') === category) {
            project.classList.remove('hidden');
        } else {
            project.classList.add('hidden');
        }
    });
}

// Filtros para talentos - VERSIÓN SIMPLIFICADA
function initializeTalentFilters() {
    const filterButtons = document.querySelectorAll('.talent-filter');
    console.log('Botones de filtro talentos encontrados:', filterButtons.length);

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const filter = this.getAttribute('data-filter');
            console.log('Filtrando talentos por:', filter);
            filterTalents(filter);

            // Update active filter button
            filterButtons.forEach(btn => {
                btn.classList.remove('bg-primary', 'text-white');
                btn.classList.add('bg-gray-700', 'text-white');
            });
            this.classList.add('bg-primary', 'text-white');
            this.classList.remove('bg-gray-700');
        });
    });
}

function filterTalents(skill) {
    const talents = document.querySelectorAll('#talentos-section [data-skills]');
    console.log('Talentos encontrados:', talents.length);

    talents.forEach(talent => {
        const talentSkills = talent.getAttribute('data-skills');
        if (skill === 'all' || talentSkills.includes(skill)) {
            talent.classList.remove('hidden');
        } else {
            talent.classList.add('hidden');
        }
    });
}
