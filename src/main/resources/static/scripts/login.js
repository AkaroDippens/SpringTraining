function login() {
    const mhiPolicy = document.getElementById('mhiPolicy').value;
    const password = document.getElementById('password').value;

    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ mhiPolicy, password })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Invalid credentials');
        }
        return response.json(); // Получаем объект ответа с токеном
    })
    .then(authResponse => {
        // Сохраняем токен в localStorage
        localStorage.setItem('authToken', authResponse.token);
        window.location.href = '/profile';

    })
    .catch(error => {
        console.error('Error:', error);
        alert('Неверный логин или пароль');
    });
}
