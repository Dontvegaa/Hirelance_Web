document.addEventListener('DOMContentLoaded', function() {

    // ==========================================
    //  1. CÓDIGO DE VALIDACIÓN DE CONTRASEÑA (Existente)
    // ==========================================
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('password-confirm');
    const validationBox = document.getElementById('password-validation-box');

    if (passwordInput && confirmPasswordInput && validationBox) {
        passwordInput.addEventListener('input', validatePassword);
        confirmPasswordInput.addEventListener('input', validatePassword);
        passwordInput.addEventListener('focus', showValidationBox);
        confirmPasswordInput.addEventListener('focus', showValidationBox); // Añadido para que se mantenga visible
        passwordInput.addEventListener('blur', hideValidationBox);
        confirmPasswordInput.addEventListener('blur', hideValidationBox); // Añadido para que se mantenga visible
    }

    function showValidationBox() {
        if (validationBox) validationBox.classList.remove('hidden');
    }

    function hideValidationBox() {
        // Solo se oculta si ninguno de los dos campos tiene foco
        setTimeout(() => { // Usamos un pequeño delay para permitir el cambio de foco
            if (!passwordInput.matches(':focus') && !confirmPasswordInput.matches(':focus')) {
                validationBox.classList.add('hidden');
            }
        }, 100);
    }

    function validatePassword() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // Validaciones
        const hasLetter = /[a-zA-Z]/.test(password);
        const hasUpperCase = /[A-Z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasMinLength = password.length >= 8;
        const hasNoSpaces = !/\s/.test(password);
        const passwordsMatch = password === confirmPassword && password.length > 0; // Solo valida si no está vacío

        // Actualizar UI
        updateValidationItem('val-letra', hasLetter);
        updateValidationItem('val-mayuscula', hasUpperCase);
        updateValidationItem('val-numero', hasNumber);
        updateValidationItem('val-longitud', hasMinLength);
        updateValidationItem('val-espacios', hasNoSpaces);
        updateValidationItem('val-coincidir', passwordsMatch);
    }

    function updateValidationItem(elementId, isValid) {
        const element = document.getElementById(elementId);
        if (element) {
            if (isValid) {
                element.classList.remove('text-red-500');
                element.classList.add('text-green-500');
            } else {
                element.classList.remove('text-green-500');
                element.classList.add('text-red-500');
            }
        }
    }

    // ==========================================
    //  2. NUEVO CÓDIGO PARA SELECTS DEPENDIENTES
    // ==========================================
    const departamentoSelect = document.getElementById('departamento');
    const ciudadSelect = document.getElementById('ciudad');
    const ubicacionHiddenInput = document.getElementById('ubicacion-hidden');

    // 'locationData' es la variable global que definimos en el HTML
    // con th:inline="javascript"
    if (departamentoSelect && ciudadSelect && ubicacionHiddenInput && typeof locationData !== 'undefined') {

        departamentoSelect.addEventListener('change', function() {
            const selectedDepartamento = this.value;

            // Limpiar el select de ciudades
            ciudadSelect.innerHTML = '<option value="">Selecciona una ciudad</option>';

            if (selectedDepartamento && locationData[selectedDepartamento]) {
                // Habilitar el select de ciudades
                ciudadSelect.disabled = false;

                // Llenar el select con las ciudades correspondientes
                locationData[selectedDepartamento].forEach(function(ciudad) {
                    const option = document.createElement('option');
                    option.value = ciudad;
                    option.textContent = ciudad;
                    ciudadSelect.appendChild(option);
                });
            } else {
                // Deshabilitar si no hay departamento seleccionado
                ciudadSelect.disabled = true;
            }

            // Actualizar el campo oculto
            updateHiddenUbicacion();
        });

        ciudadSelect.addEventListener('change', function() {
            // Actualizar el campo oculto cada vez que cambia la ciudad
            updateHiddenUbicacion();
        });

        function updateHiddenUbicacion() {
            const depto = departamentoSelect.value;
            const ciudad = ciudadSelect.value;

            if (depto && ciudad) {
                // Formato: "Ciudad, Departamento"
                ubicacionHiddenInput.value = `${ciudad}, ${depto}`;
            } else {
                ubicacionHiddenInput.value = ''; // Vaciar si no están ambos seleccionados
            }
        }
    }

});