document.addEventListener('DOMContentLoaded', function() {

    // 1. Verificar que 'locationData' se cargó en el HTML
    if (typeof locationData === 'undefined') {
        console.error('locationData no está definido. Asegúrate de cargarlo en el HTML.');
        return;
    }

    // 2. Obtener los elementos del DOM
    const deptoSelect = document.getElementById('departamento');
    const ciudadSelect = document.getElementById('ciudad');

    // 3. Obtener la ciudad actual guardada (la pasamos desde el HTML)
    const currentCity = ciudadSelect.dataset.currentCity;

    /**
     * Rellena el menú de ciudades basado en el departamento seleccionado.
     * @param {string} selectedDepto - El nombre del departamento.
     */
    function populateCiudades(selectedDepto) {
        // Limpiar ciudades anteriores
        ciudadSelect.innerHTML = '<option value="">Selecciona una ciudad</option>';

        // Comprobar si el departamento existe y tiene ciudades
        if (selectedDepto && locationData[selectedDepto]) {
            ciudadSelect.disabled = false;

            // Rellenar con las nuevas ciudades
            locationData[selectedDepto].forEach(function(ciudad) {
                const option = document.createElement('option');
                option.value = ciudad;
                option.textContent = ciudad;

                // ¡IMPORTANTE! Seleccionar la ciudad si coincide con la guardada
                if (ciudad === currentCity) {
                    option.selected = true;
                }

                ciudadSelect.appendChild(option);
            });
        } else {
            // Deshabilitar si no se selecciona un departamento
            ciudadSelect.disabled = true;
        }
    }

    // --- EJECUCIÓN ---

    // 1. Al cargar la página, rellenar las ciudades
    //    basado en el departamento que ya está seleccionado en el HTML.
    if (deptoSelect.value) {
        populateCiudades(deptoSelect.value);
    }

    // 2. Añadir el 'listener' para cuando el usuario cambie el departamento.
    deptoSelect.addEventListener('change', function() {
        // Al cambiar, ya no nos importa la "ciudad actual",
        // así que la función se llama sin ese dato.
        populateCiudades(this.value);
    });
});