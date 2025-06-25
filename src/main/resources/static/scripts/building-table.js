document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const tableBody = document.getElementById('building-table-body');
    const detailsModal = document.getElementById('building-details-modal');
    const formModal = document.getElementById('building-form-modal');
    const paginationContainer = document.getElementById('pagination');

    // Состояние
    let buildings = []; // Добавляем глобальную переменную buildings
    let currentBuildingId = null;
    const authToken = localStorage.getItem('authToken');
    const itemsPerPage = 10;
    let currentPage = 1;

    // Рендер таблицы корпусов
    const renderBuildingsTable = (buildingsToRender) => {
        tableBody.innerHTML = '';

        buildingsToRender.forEach(building => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${building.buildingName}</td>
                <td>${building.address}</td>
                <td>${building.contactNumber}</td>
                <td><button class="btn" onclick="showBuildingDetails(${building.id})">Подробнее</button></td>
            `;
            tableBody.appendChild(row);
        });
    };

    // Рендер пагинации
    const renderPagination = (totalItems) => {
        paginationContainer.innerHTML = '';
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        const maxPagesToShow = 5;

        let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);
        startPage = Math.max(1, endPage - maxPagesToShow + 1);

        for (let i = startPage; i <= endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i;
            pageButton.className = `pagination-btn ${i === currentPage ? 'active' : ''}`;
            pageButton.addEventListener('click', () => {
                currentPage = i;
                updateTable();
                renderPagination(totalItems);
            });
            paginationContainer.appendChild(pageButton);
        }
    };

    // Обновление таблицы
    const updateTable = () => {
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        renderBuildingsTable(buildings.slice(startIndex, endIndex));
    };

    // Показать детали корпуса
    window.showBuildingDetails = (buildingId) => {
        fetch(`/api/buildings/${buildingId}`, { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(building => {
                document.getElementById('building-detail-name').textContent = `Название здания: ${building.buildingName}`;
                document.getElementById('building-detail-address').textContent = `Адрес: ${building.address}`;
                document.getElementById('building-detail-contact').textContent = `Контактный номер: ${building.contactNumber}`;

                currentBuildingId = building.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка при получении данных о здании:', error));
    };

    // Обработка формы
    const handleFormSubmit = (event) => {
        event.preventDefault();

        const building = {
            id: document.getElementById('building-id').value || null,
            buildingName: document.getElementById('building-name').value,
            address: document.getElementById('address').value,
            contactNumber: document.getElementById('contact-number').value
        };

        const method = building.id ? 'PUT' : 'POST';
        const url = building.id ? `/api/buildings/${building.id}` : '/api/buildings';

        fetch(url, {
            method,
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(building)
        })
            .then(response => {
                if (response.ok) {
                    fetchBuildings();
                    formModal.style.display = 'none';
                } else {
                    alert('Ошибка при сохранении здания');
                }
            })
            .catch(error => console.error('Ошибка при сохранении здания:', error));
    };

    // Удаление корпуса
    const deleteBuilding = () => {
        if (!confirm('Вы уверены, что хотите удалить это здание?')) return;

        fetch(`/api/buildings/${currentBuildingId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    fetchBuildings();
                    detailsModal.style.display = 'none';
                } else {
                    alert('Ошибка при удалении здания');
                }
            })
            .catch(error => console.error('Ошибка при удалении здания:', error));
    };

    // Загрузка корпусов
    const fetchBuildings = () => {
        fetch('/api/buildings', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(fetchedBuildings => {
                buildings = fetchedBuildings; // Присваиваем данные глобальной переменной buildings
                updateTable();
                renderPagination(buildings.length);
            })
            .catch(error => console.error('Ошибка при получении списка зданий:', error));
    };

    // Обработчики событий
    document.getElementById('close-modal').addEventListener('click', () => {
        detailsModal.style.display = 'none';
    });

    document.getElementById('close-form-modal').addEventListener('click', () => {
        formModal.style.display = 'none';
    });

    document.getElementById('add-building-btn').addEventListener('click', () => {
        document.getElementById('form-title').textContent = 'Добавить здание';
        document.getElementById('building-id').value = '';
        document.getElementById('building-name').value = '';
        document.getElementById('address').value = '';
        document.getElementById('contact-number').value = '';
        formModal.style.display = 'block';
    });

    document.getElementById('edit-building-btn').addEventListener('click', () => {
        document.getElementById('form-title').textContent = 'Редактировать здание';
        document.getElementById('building-id').value = currentBuildingId;
        document.getElementById('building-name').value = document.getElementById('building-detail-name').textContent.split(': ')[1];
        document.getElementById('address').value = document.getElementById('building-detail-address').textContent.split(': ')[1];
        document.getElementById('contact-number').value = document.getElementById('building-detail-contact').textContent.split(': ')[1];
        formModal.style.display = 'block';
    });

    document.getElementById('delete-building-btn').addEventListener('click', deleteBuilding);
    document.getElementById('building-form').addEventListener('submit', handleFormSubmit);

    // Инициализация
    fetchBuildings();
});