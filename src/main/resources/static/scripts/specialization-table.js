document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const tableBody = document.getElementById('specialization-table-body');
    const paginationContainer = document.getElementById('pagination');
    const detailsModal = document.getElementById('specialization-details-modal');
    const formModal = document.getElementById('specialization-form-modal');
    const addBtn = document.getElementById('add-specialization-btn');
    const editBtn = document.getElementById('edit-specialization-btn');
    const deleteBtn = document.getElementById('delete-specialization-btn');
    const form = document.getElementById('specialization-form');

    // Константы и состояние
    const itemsPerPage = 10;
    let currentPage = 1;
    let specializations = [];
    const authToken = localStorage.getItem('authToken');

    // Рендер таблицы
    const renderTable = (paginatedSpecializations) => {
        tableBody.innerHTML = '';

        paginatedSpecializations.forEach(spec => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${spec.specializationName}</td>
                <td>
                    <button class="btn" onclick="showSpecializationDetails(${spec.id})">
                        Подробнее
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    };

    // Рендер пагинации
    const renderPagination = (totalItems) => {
        paginationContainer.innerHTML = '';
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        const maxPagesToShow = 5;

        // Нумерация страниц
        let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);
        startPage = Math.max(1, endPage - maxPagesToShow + 1);

        for (let i = startPage; i <= endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i;
            pageButton.className = `pagination-btn ${i === currentPage ? 'active' : ''}`;
            pageButton.addEventListener('click', () => {
                currentPage = i;
                renderPagination(totalItems);
                updateTable();
            });
            paginationContainer.appendChild(pageButton);
        }
    };

    // Обновление таблицы
    const updateTable = () => {
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        renderTable(specializations.slice(startIndex, endIndex));
    };

    // Показать детали
    window.showSpecializationDetails = (id) => {
        fetch(`/api/specializations/${id}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        })
            .then(response => response.json())
            .then(spec => {
                document.getElementById('specialization-detail-name').textContent =
                    `Название: ${spec.specializationName}`;

                currentSpecializationId = spec.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка загрузки специализации:', error));
    };

    // Обработка формы
    const handleFormSubmit = (event) => {
        event.preventDefault();

        const specialization = {
            id: document.getElementById('specialization-id').value || null,
            specializationName: document.getElementById('specialization-name').value
        };

        const method = specialization.id ? 'PUT' : 'POST';
        const url = specialization.id
            ? `/api/specializations/${specialization.id}`
            : '/api/specializations';

        fetch(url, {
            method,
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(specialization)
        })
            .then(response => {
                if (response.ok) {
                    fetchSpecializations();
                    formModal.style.display = 'none';
                } else {
                    alert('Ошибка сохранения специализации');
                }
            })
            .catch(error => console.error('Ошибка сохранения:', error));
    };

    // Удаление
    const deleteSpecialization = () => {
        if (!confirm('Удалить специализацию?')) return;

        fetch(`/api/specializations/${currentSpecializationId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    fetchSpecializations();
                    detailsModal.style.display = 'none';
                } else {
                    alert('Ошибка удаления');
                }
            })
            .catch(error => console.error('Ошибка удаления:', error));
    };

    // Загрузка данных
    const fetchSpecializations = () => {
        fetch('/api/specializations', {
            headers: { 'Authorization': `Bearer ${authToken}` }
        })
            .then(response => response.json())
            .then(fetchedSpecializations => {
                specializations = fetchedSpecializations.filter(spec => spec !== null);
                renderPagination(specializations.length);
                updateTable();
            })
            .catch(error => console.error('Ошибка загрузки:', error));
    };

    // Обработчики событий
    document.getElementById('close-modal').addEventListener('click', () => {
        detailsModal.style.display = 'none';
    });

    document.getElementById('close-form-modal').addEventListener('click', () => {
        formModal.style.display = 'none';
    });

    addBtn.addEventListener('click', () => {
        document.getElementById('form-title').textContent = 'Добавить специализацию';
        document.getElementById('specialization-id').value = '';
        document.getElementById('specialization-name').value = '';
        formModal.style.display = 'block';
    });

    editBtn.addEventListener('click', () => {
        document.getElementById('form-title').textContent = 'Редактировать специализацию';
        document.getElementById('specialization-id').value = currentSpecializationId;
        document.getElementById('specialization-name').value =
            document.getElementById('specialization-detail-name').textContent.split(': ')[1];
        formModal.style.display = 'block';
    });

    deleteBtn.addEventListener('click', deleteSpecialization);
    form.addEventListener('submit', handleFormSubmit);

    // Инициализация
    fetchSpecializations();
});