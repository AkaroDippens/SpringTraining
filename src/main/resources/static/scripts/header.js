document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Запрос данных профиля
    fetch('/api/profile', {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` }
    })
        .then(response => {
            if (!response.ok) {
                window.location.href = '/login';
                return;
            }
            return response.json();
        })
        .then(profileData => {
            updateMenu(profileData.user.idRole.roleName);
        })
        .catch(error => {
            console.error('Error fetching profile:', error);
            alert('An error occurred while fetching your profile.');
        });

    // Обновление меню навигации
    function updateMenu(userRole) {
        const navMenu = document.querySelector('.nav-menu');
        let menuItems = '';

        switch (userRole) {
            case null:
                menuItems = `        
                    <a href="/login" class="nav-link">Войти</a>
                    <a href="/register" class="nav-link">Регистрация</a>
                `;
                break;
            case 'USER':
                menuItems = `
                    <a href="/records" class="nav-link">Записаться на прием</a>
                    <a href="/profile" class="nav-link">Профиль</a>
                    <a href="/logout" id="logout-link" class="nav-link">Выход</a>
                `;
                break;
            case 'DOCTOR':
                menuItems = `
                    <a href="/doctor/appointments" class="nav-link">Приемы</a>
                    <a href="/medicines" class="nav-link">Лекарства</a>
                    <a href="/profile" class="nav-link">Профиль</a>
                    <a href="/logout" id="logout-link" class="nav-link">Выход</a>
                `;
                break;
            case 'ADMIN':
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
                break;
            case 'DB_ADMIN':
                menuItems = `
                    <a href="/db-admin/logs" class="nav-link">Логи пользователей</a>
                    <a href="/db-admin/statistics" class="nav-link">Статистика</a>
                    <a href="/profile" class="nav-link">Профиль</a>
                    <a href="/logout" id="logout-link" class="nav-link">Выход</a>
                `;
                break;
        }

        navMenu.innerHTML = menuItems;

        // Обработчик выхода
        const logoutLink = document.getElementById('logout-link');
        if (logoutLink) {
            logoutLink.addEventListener('click', (event) => {
                event.preventDefault();
                localStorage.removeItem('authToken');
                window.location.href = '/login';
            });
        }
    }
});