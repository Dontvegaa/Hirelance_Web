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
        confirmPasswordInput.addEventListener('focus', showValidationBox);
        passwordInput.addEventListener('blur', hideValidationBox);
        confirmPasswordInput.addEventListener('blur', hideValidationBox);
    }

    function showValidationBox() {
        if (validationBox) validationBox.classList.remove('hidden');
    }

    function hideValidationBox() {
        setTimeout(() => {
            if (!passwordInput.matches(':focus') && !confirmPasswordInput.matches(':focus')) {
                validationBox.classList.add('hidden');
            }
        }, 100);
    }

    function validatePassword() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        const hasLetter = /[a-zA-Z]/.test(password);
        const hasUpperCase = /[A-Z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasMinLength = password.length >= 8;
        const hasNoSpaces = !/\s/.test(password);
        const passwordsMatch = password === confirmPassword && password.length > 0;

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

    if (departamentoSelect && ciudadSelect && ubicacionHiddenInput && typeof locationData !== 'undefined') {

        departamentoSelect.addEventListener('change', function() {
            const selectedDepartamento = this.value;
            ciudadSelect.innerHTML = '<option value="">Selecciona una ciudad</option>';

            if (selectedDepartamento && locationData[selectedDepartamento]) {
                ciudadSelect.disabled = false;
                locationData[selectedDepartamento].forEach(function(ciudad) {
                    const option = document.createElement('option');
                    option.value = ciudad;
                    option.textContent = ciudad;
                    ciudadSelect.appendChild(option);
                });
            } else {
                ciudadSelect.disabled = true;
            }
            updateHiddenUbicacion();
        });

        ciudadSelect.addEventListener('change', function() {
            updateHiddenUbicacion();
        });

        function updateHiddenUbicacion() {
            const depto = departamentoSelect.value;
            const ciudad = ciudadSelect.value;
            if (depto && ciudad) {
                ubicacionHiddenInput.value = `${ciudad}, ${depto}`;
            } else {
                ubicacionHiddenInput.value = '';
            }
        }
    }

    // ==========================================
    //  3. NUEVAS FUNCIONES DE FORMATEO PARA DUI Y TELÉFONO
    // ==========================================
    const duiInput = document.getElementById('dui');
    const telefonoInput = document.getElementById('telefono');

    // Función para formatear automáticamente el DUI (06804213-9)
    function formatDUI(input) {
        let value = input.value.replace(/[^\d]/g, '');

        // Aplicar formato: XXXXXXXX-X después de 8 dígitos
        if (value.length >= 8) {
            value = value.substring(0, 8) + '-' + value.substring(8, 9);
        }

        input.value = value;
    }

    // Función para formatear automáticamente el teléfono (+503 6059-8569)
    function formatPhone(input) {
        let value = input.value.replace(/[^\d]/g, '');

        // Si empieza a escribir, agregamos automáticamente "+503 "
        if (value.length > 0 && !input.value.startsWith('+503')) {
            value = '503' + value; // Prefijo sin el + para el procesamiento
        }

        // Aplicar formato: +503 XXXX-XXXX
        if (value.length > 3) {
            let formattedValue = '+503 ';

            if (value.length > 7) {
                // +503 XXXX-XXXX
                formattedValue += value.substring(3, 7) + '-' + value.substring(7, 11);
            } else {
                // +503 XXX (en proceso de escritura)
                formattedValue += value.substring(3);
            }

            input.value = formattedValue;
        } else if (value.length > 0) {
            input.value = '+503 ' + value.substring(3);
        }
    }

    // Función para manejar el evento de teclado en DUI (evitar borrar el guión)
    function handleDUIKeydown(event) {
        const input = event.target;
        const cursorPosition = input.selectionStart;

        // Si presiona backspace y el cursor está justo después del guión
        if (event.key === 'Backspace' && cursorPosition === 9) {
            event.preventDefault();
            // Mover cursor antes del guión y borrar el dígito anterior
            input.setSelectionRange(8, 8);
            let value = input.value.replace(/[^\d]/g, '');
            value = value.substring(0, value.length - 1);
            input.value = value;
            formatDUI(input);
        }

        // Si presiona delete y el cursor está justo antes del guión
        if (event.key === 'Delete' && cursorPosition === 8) {
            event.preventDefault();
            // Borrar el dígito después del guión
            let value = input.value.replace(/[^\d]/g, '');
            if (value.length > 8) {
                value = value.substring(0, 8) + value.substring(9);
            }
            input.value = value;
            formatDUI(input);
            input.setSelectionRange(8, 8);
        }
    }

    // Función para manejar el evento de teclado en teléfono
    function handlePhoneKeydown(event) {
        const input = event.target;
        const cursorPosition = input.selectionStart;

        // Prevenir borrado del prefijo "+503 "
        if (event.key === 'Backspace' && cursorPosition <= 5) {
            event.preventDefault();
            return;
        }

        // Si presiona backspace en posición del guión
        if (event.key === 'Backspace' && cursorPosition === 10) {
            event.preventDefault();
            input.setSelectionRange(9, 9);
        }

        // Si presiona delete en posición del guión
        if (event.key === 'Delete' && cursorPosition === 9) {
            event.preventDefault();
            input.setSelectionRange(10, 10);
        }
    }

    // Aplicar event listeners
    if (duiInput) {
        duiInput.addEventListener('input', function() {
            formatDUI(this);
        });
        duiInput.addEventListener('keydown', handleDUIKeydown);

        // Establecer placeholder y maxlength
        duiInput.placeholder = '06804213-9';
        duiInput.maxLength = 10;
    }

    if (telefonoInput) {
        telefonoInput.addEventListener('input', function() {
            formatPhone(this);
        });
        telefonoInput.addEventListener('keydown', handlePhoneKeydown);
        telefonoInput.addEventListener('focus', function() {
            // Si está vacío, poner el prefijo
            if (!this.value) {
                this.value = '+503 ';
            }
        });

        // Establecer placeholder y maxlength
        telefonoInput.placeholder = '+503 6059-8569';
        telefonoInput.maxLength = 14; // +503 XXXX-XXXX = 14 caracteres
    }

    // Validación adicional antes de enviar el formulario
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            const duiInput = document.getElementById('dui');
            const phoneInput = document.getElementById('telefono');
            let isValid = true;

            if (duiInput && duiInput.value) {
                // Validar DUI: 8 dígitos + guión + 1 dígito
                const duiRegex = /^[0-9]{8}-[0-9]{1}$/;
                if (!duiRegex.test(duiInput.value)) {
                    alert('Por favor, ingrese un DUI válido con formato: 12345678-9');
                    duiInput.focus();
                    isValid = false;
                }
            }

            if (phoneInput && phoneInput.value && isValid) {
                // Validar teléfono: +503 XXXX-XXXX
                const phoneRegex = /^\+503\s[0-9]{4}-[0-9]{4}$/;
                if (!phoneRegex.test(phoneInput.value)) {
                    alert('Por favor, ingrese un teléfono válido con formato: +503 XXXX-XXXX');
                    phoneInput.focus();
                    isValid = false;
                }
            }

            if (!isValid) {
                event.preventDefault();
            }
        });
    });

});