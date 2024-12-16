document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('user-table-body');
    const tableHeaders = document.querySelectorAll('.user-table th[data-sort]');
    const roleFilter = document.getElementById('role-filter');
    const applyRoleFilterButton = document.getElementById('apply-role-filter');
    const resetRoleFilterButton = document.getElementById('reset-role-filter');
    const modal = document.getElementById('user-details-modal');
    const closeModal = document.getElementById('close-modal');
    const paginationContainer = document.getElementById('pagination');
    const itemsPerPage = 10;
    let currentPage = 1;
    let users = []; // Данные пользователей
    let currentSortColumn = null;
    let currentSortDirection = 'asc'; // Возможные значения: 'asc', 'desc'

    // Функция для вывода данных в таблицу
    const renderUsersTable = (filteredUsers) => {
        tableBody.innerHTML = '';
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        const paginatedUsers = filteredUsers.slice(startIndex, endIndex);

        paginatedUsers.forEach(user => {
            const row = document.createElement('tr');
            const roleChangeButton = user.roleName === 'DOCTOR'
                ? `<button class="btn-warning" onclick="changeUserRole(${user.id}, 3)">Снять роль доктора</button>`
                : `<button class="btn-primary" onclick="changeUserRole(${user.id}, 4)">Назначить доктором</button>`;

            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.fullName}</td>
                <td>${user.contactNumber || '—'}</td>
                <td>${user.mhiPolicy}</td>
                <td>${user.birthDate || '—'}</td>
                <td>${user.roleName}</td>
                <td>
                    <button class="btn" onclick="showUserDetails(${user.id})">Подробнее</button>
                    ${roleChangeButton}
                </td>
            `;
            tableBody.appendChild(row);
        });

        // Передаем длину полного массива, а не paginatedUsers
        renderPagination(filteredUsers.length);
    };

    const renderPagination = (totalItems) => {
        paginationContainer.innerHTML = '';
        const totalPages = Math.ceil(totalItems / itemsPerPage);

        for (let i = 1; i <= totalPages; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i;
            pageButton.className = `pagination-btn ${i === currentPage ? 'active' : ''}`;
            pageButton.addEventListener('click', () => {
                currentPage = i;
                const filteredUsers = filterByRole();
                renderUsersTable(filteredUsers);
            });
            paginationContainer.appendChild(pageButton);
        }
    };

    // Обновляем функцию изменения роли
    window.changeUserRole = (userId, newRoleId) => {
        fetch(`/api/users/${userId}/role?newRoleId=${newRoleId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                // Обновляем таблицу после смены роли
                return fetch('/api/users')
                    .then(response => response.json())
                    .then(fetchedUsers => {
                        users = fetchedUsers;
                        renderUsersTable(users);
                    });
            } else {
                throw new Error('Failed to change user role');
            }
        })
        .catch(error => {
            console.error('Error changing user role:', error);
            alert('Failed to change user role. Please try again.');
        });
    };

    // Функция для сортировки
    const sortUsers = (column, filteredUsers = users) => {
        // Определяем направление сортировки
        if (currentSortColumn === column) {
            currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            currentSortColumn = column;
            currentSortDirection = 'asc';
        }

        // Сортируем пользователей
        const sortedUsers = [...filteredUsers].sort((a, b) => {
            const valueA = a[column] ?? ''; // Значение A
            const valueB = b[column] ?? ''; // Значение B

            // Если значения строки, сравниваем их регистронезависимо
            if (typeof valueA === 'string' && typeof valueB === 'string') {
                return currentSortDirection === 'asc'
                    ? valueA.localeCompare(valueB)
                    : valueB.localeCompare(valueA);
            }

            // Для чисел и дат
            if (typeof valueA === 'number' || valueA instanceof Date) {
                return currentSortDirection === 'asc' ? valueA - valueB : valueB - valueA;
            }

            return 0; // Если не строка и не число, оставляем как есть
        });

        renderUsersTable(sortedUsers);
    };

    // Добавляем обработчики событий на заголовки
    tableHeaders.forEach(header => {
        header.addEventListener('click', () => {
            const column = header.dataset.sort;
            const filteredUsers = filterByRole(); // Учитываем текущую фильтрацию по роли
            sortUsers(column, filteredUsers);
        });
    });

    // Функция для фильтрации по роли
    const filterByRole = () => {
        const selectedRole = roleFilter.value.trim();
        return selectedRole === ''
            ? users // Если роль не выбрана, возвращаем всех пользователей
            : users.filter(user => user.roleName === selectedRole);
    };

    // Применение фильтра по роли
    applyRoleFilterButton.onclick = () => {
        const filteredUsers = filterByRole();
        renderUsersTable(filteredUsers); // Отображаем отфильтрованных пользователей
    };

    // Сброс фильтра по роли
    resetRoleFilterButton.onclick = () => {
        roleFilter.value = '';
        renderUsersTable(users); // Показываем всех пользователей
    };

    // Функция для отображения деталей пользователя
    window.showUserDetails = (userId) => {
        fetch(`/api/users/${userId}`)
            .then(response => response.json())
            .then(user => {
                document.getElementById('user-detail-id').textContent = `ID: ${user.id}`;
                document.getElementById('user-detail-full-name').textContent = `ФИО: ${user.fullName}`;
                document.getElementById('user-detail-contact').textContent = `Номер телефона: ${user.contactNumber || '—'}`;
                document.getElementById('user-detail-policy').textContent = `Полис ОМС: ${user.mhiPolicy}`;
                document.getElementById('user-detail-birth-date').textContent = `Дата рождения: ${user.birthDate || '—'}`;
                document.getElementById('user-detail-role').textContent = `Роль: ${user.roleName}`;

                modal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка при получении данных о пользователе:', error);
            });
    };

    // Закрытие модального окна
    closeModal.onclick = () => {
        modal.style.display = 'none';
    };

    // Закрытие модального окна при клике вне его
    window.onclick = (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };

    // Получаем данные о пользователях с сервера
    fetch('/api/users')
        .then(response => response.json())
        .then(fetchedUsers => {
            users = fetchedUsers; // Сохраняем пользователей
            renderUsersTable(users); // Отображаем полный список
        })
        .catch(error => {
            console.error('Ошибка при получении списка пользователей:', error);
        });
});
