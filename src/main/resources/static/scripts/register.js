document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registrationForm');

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Сброс ошибок
        document.querySelectorAll('.error-message').forEach(el => {
            el.textContent = '';
        });

        // Подготовка данных
        const formData = {
            fullName: document.getElementById('fullName').value,
            mhiPolicy: document.getElementById('mhiPolicy').value,
            birthDate: document.getElementById('birthDate').value,
            password: document.getElementById('password').value
        };

        try {
            const response = await fetch('/api/users/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errors = await response.json();
                Object.entries(errors).forEach(([field, message]) => {
                    const errorElement = document.getElementById(`error-${field}`);
                    if (errorElement) errorElement.textContent = message;
                });
                return;
            }

            alert('Регистрация успешна!');
            window.location.href = '/login';
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Ошибка регистрации');
        }
    });
});