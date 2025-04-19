document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('authToken');

    if (!token) {
        console.log("Token not found. Redirecting to login...");
        window.location.href = '/login';
        return;
    }

    // Запрос данных профиля
    fetch('/api/profile', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            console.log("Invalid token or user not found.");
            window.location.href = '/login';
            return;
        }
        return response.json();
    })
    .then(profileData => {
        // Обновляем меню в зависимости от роли пользователя
        updateMenu(profileData.user.idRole.roleName);
    })
    .catch(error => {
        console.error('Error fetching profile:', error);
        alert('An error occurred while fetching your profile.');
    });
});

function updateMenu(userRole) {
    const navMenu = document.querySelector('.nav-menu');
    navMenu.innerHTML = ''; // Очищаем меню

    let menuItems = '';

    if (userRole === null) {
        menuItems = `        
            <a href="/login" class="nav-link">Войти</a>
            <a href="/register" class="nav-link">Регистрация</a>
        `;
    } else if (userRole === 'USER') {
        menuItems = `
            <a href="/records" class="nav-link">Записаться на прием</a>
            <a href="/profile" class="nav-link">Профиль</a>
            <a href="/logout" id="logout-link" class="nav-link">Выход</a>
        `;
    } else if (userRole === 'DOCTOR') {
        menuItems = `
            <a href="/doctor/appointments" class="nav-link">Приемы</a>
            <a href="/medicines" class="nav-link">Лекарства</a>
            <a href="/profile" class="nav-link">Профиль</a>
            <a href="/logout" id="logout-link" class="nav-link">Выход</a>
        `;
    } else if (userRole === 'ADMIN') {
        menuItems = `
            <a href="/records" class="nav-link">Записаться на прием</a>
            <a href="/doctors" class="nav-link">Врачи</a>
            <a href="/specializations" class="nav-link">Специализации</a>
            <a href="/buildings" class="nav-link">Корпуса</a>
            <a href="/users" class="nav-link">Пользователи</a>
            <a href="/medicines" class="nav-link">Лекарства</a>
            <a href="/profile" class="nav-link">Профиль</a>
            <a href="/logout" id="logout-link" class="nav-link">Выход</a>
        `;
    } else if (userRole === 'DB_ADMIN') {
        menuItems = `
            <a href="/db-admin/logs" class="nav-link">Логи пользователей</a>
            <a href="/db-admin/statistics" class="nav-link">Статистика</a>
            <a href="/profile" class="nav-link">Профиль</a>
            <a href="/logout" id="logout-link" class="nav-link">Выход</a>
        `;
    }

    navMenu.innerHTML = menuItems;

    const logoutLink = document.getElementById('logout-link');
    if (logoutLink) {
        logoutLink.addEventListener('click', (event) => {
            event.preventDefault(); // Предотвращаем переход по ссылке
            localStorage.removeItem('authToken'); // Удаляем токен из localStorage
            window.location.href = '/login'; // Перенаправляем на страницу входа
        });
    }
}