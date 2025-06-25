document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const tableBody = document.getElementById('user-table-body');
    const roleFilter = document.getElementById('role-filter');
    const searchInput = document.getElementById('search-input');
    const modal = document.getElementById('user-details-modal');

    // Константы
    const itemsPerPage = 10;

    // Состояние
    let users = [];
    let currentPage = 1;
    let currentSort = { column: null, direction: 'asc' };
    const authToken = localStorage.getItem('authToken');

    // Рендер таблицы
    const renderTable = (filteredUsers) => {
        tableBody.innerHTML = '';
        const startIndex = (currentPage - 1) * itemsPerPage;
        const paginatedUsers = filteredUsers.slice(startIndex, startIndex + itemsPerPage);

        paginatedUsers.forEach(user => {
            const row = document.createElement('tr');
            const roleButton = user.roleName === 'DOCTOR'
                ? `<button class="btn-warning" onclick="changeUserRole(${user.id}, 3)">
                    Снять роль доктора
                   </button>`
                : `<button class="btn-primary" onclick="changeUserRole(${user.id}, 4)">
                    Назначить доктором
                   </button>`;

            row.innerHTML = `
                <td>${user.fullName}</td>
                <td>${user.contactNumber || '—'}</td>
                <td>${user.mhiPolicy}</td>
                <td>${user.birthDate || '—'}</td>
                <td>${user.roleName}</td>
                <td>
                    <button class="btn" onclick="showUserDetails(${user.id})">
                        Подробнее
                    </button>
                    ${roleButton}
                </td>
            `;
            tableBody.appendChild(row);
        });

        renderPagination(filteredUsers.length);
    };

    // Пагинация
    const renderPagination = (totalItems) => {
        const pagination = document.getElementById('pagination');
        pagination.innerHTML = '';
        const pageCount = Math.ceil(totalItems / itemsPerPage);

        for (let i = 1; i <= pageCount; i++) {
            const btn = document.createElement('button');
            btn.textContent = i;
            btn.className = `pagination-btn ${i === currentPage ? 'active' : ''}`;
            btn.addEventListener('click', () => {
                currentPage = i;
                renderTable(filterUsers());
            });
            pagination.appendChild(btn);
        }
    };

    // Фильтрация
    const filterUsers = () => {
        let filtered = [...users];
        const role = roleFilter.value.trim();
        const query = searchInput.value.trim().toLowerCase();

        if (role) filtered = filtered.filter(u => u.roleName === role);
        if (query) {
            filtered = filtered.filter(u =>
                u.fullName.toLowerCase().includes(query) ||
                (u.contactNumber && u.contactNumber.includes(query)) ||
                (u.mhiPolicy && u.mhiPolicy.includes(query))
            );
        }

        if (currentSort.column) {
            filtered.sort((a, b) => {
                const valA = a[currentSort.column] ?? '';
                const valB = b[currentSort.column] ?? '';
                return currentSort.direction === 'asc'
                    ? String(valA).localeCompare(String(valB))
                    : String(valB).localeCompare(String(valA));
            });
        }

        return filtered;
    };

    // Сортировка
    const setupSorting = () => {
        document.querySelectorAll('.user-table th[data-sort]').forEach(header => {
            header.addEventListener('click', () => {
                const column = header.dataset.sort;
                currentSort = {
                    column,
                    direction: currentSort.column === column && currentSort.direction === 'asc'
                        ? 'desc'
                        : 'asc'
                };
                renderTable(filterUsers());
            });
        });
    };

    // Изменение роли
    window.changeUserRole = (userId, roleId) => {
        fetch(`/api/users/${userId}/role?newRoleId=${roleId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) return fetchUsers();
                throw new Error('Ошибка изменения роли');
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Не удалось изменить роль');
            });
    };

    // Показать детали
    window.showUserDetails = (userId) => {
        fetch(`/api/users/${userId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        })
            .then(response => response.json())
            .then(user => {
                document.getElementById('user-detail-full-name').textContent = `ФИО: ${user.fullName}`;
                document.getElementById('user-detail-contact').textContent = `Телефон: ${user.contactNumber || '—'}`;
                document.getElementById('user-detail-policy').textContent = `Полис: ${user.mhiPolicy}`;
                document.getElementById('user-detail-birth-date').textContent = `Дата рождения: ${user.birthDate || '—'}`;
                document.getElementById('user-detail-role').textContent = `Роль: ${user.roleName}`;

                modal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка загрузки:', error));
    };

    // Загрузка данных
    const fetchUsers = () => {
        fetch('/api/users', {
            headers: { 'Authorization': `Bearer ${authToken}` }
        })
            .then(response => response.json())
            .then(data => {
                users = data;
                renderTable(filterUsers());
            })
            .catch(error => console.error('Ошибка загрузки:', error));
    };

    // Обработчики событий
    document.getElementById('close-modal').addEventListener('click', () => {
        modal.style.display = 'none';
    });

    document.getElementById('apply-role-filter').addEventListener('click', () => {
        currentPage = 1;
        renderTable(filterUsers());
    });

    document.getElementById('reset-role-filter').addEventListener('click', () => {
        roleFilter.value = '';
        currentPage = 1;
        renderTable(filterUsers());
    });

    document.getElementById('search-btn').addEventListener('click', () => {
        currentPage = 1;
        renderTable(filterUsers());
    });

    window.onclick = (event) => {
        if (event.target === modal) modal.style.display = 'none';
    };

    // Инициализация
    setupSorting();
    fetchUsers();
});