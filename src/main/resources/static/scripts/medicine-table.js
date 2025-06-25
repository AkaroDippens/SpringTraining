document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const tableBody = document.getElementById('medicine-table-body');
    const detailsModal = document.getElementById('medicine-details-modal');
    const formModal = document.getElementById('medicine-form-modal');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const paginationContainer = document.getElementById('pagination');

    // Состояние
    let medicines = [];
    let currentMedicineId = null;
    const authToken = localStorage.getItem('authToken');
    const itemsPerPage = 10;
    let currentPage = 1;

    // Рендер таблицы лекарств
    const renderMedicinesTable = (medicinesToRender) => {
        tableBody.innerHTML = '';

        medicinesToRender.forEach(medicine => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${medicine.medicineName}</td>
                <td>${medicine.manufacturer}</td>
                <td><button class="btn" onclick="showMedicineDetails(${medicine.id})">Подробнее</button></td>
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
        renderMedicinesTable(medicines.slice(startIndex, endIndex));
    };

    // Поиск лекарств
    const searchMedicines = (query) => {
        if (!query.trim()) return medicines;

        return medicines.filter(medicine =>
            medicine.medicineName.toLowerCase().includes(query.toLowerCase()) ||
            medicine.manufacturer.toLowerCase().includes(query.toLowerCase())
        );
    };

    // Показать детали лекарства
    window.showMedicineDetails = (medicineId) => {
        fetch(`/api/medicines/${medicineId}`, { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(medicine => {
                document.getElementById('medicine-detail-name').textContent = `Название лекарства: ${medicine.medicineName}`;
                document.getElementById('medicine-detail-manufacturer').textContent = `Производитель: ${medicine.manufacturer}`;

                currentMedicineId = medicine.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка при получении данных о лекарстве:', error));
    };

    // Обработка формы
    const handleFormSubmit = (event) => {
        event.preventDefault();

        const medicine = {
            id: document.getElementById('medicine-id').value || null,
            medicineName: document.getElementById('medicine-name').value,
            manufacturer: document.getElementById('manufacturer').value
        };

        const method = medicine.id ? 'PUT' : 'POST';
        const url = medicine.id ? `/api/medicines/${medicine.id}` : '/api/medicines';

        fetch(url, {
            method,
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(medicine)
        })
            .then(response => {
                if (response.ok) {
                    fetchMedicines();
                    formModal.style.display = 'none';
                } else {
                    alert('Ошибка при сохранении лекарства');
                }
            })
            .catch(error => console.error('Ошибка при сохранении лекарства:', error));
    };

    // Удаление лекарства
    const deleteMedicine = () => {
        if (!confirm('Вы уверены, что хотите удалить это лекарство?')) return;

        fetch(`/api/medicines/${currentMedicineId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    fetchMedicines();
                    detailsModal.style.display = 'none';
                } else {
                    alert('Ошибка при удалении лекарства');
                }
            })
            .catch(error => console.error('Ошибка при удалении лекарства:', error));
    };

    // Загрузка лекарств
    const fetchMedicines = () => {
        fetch('/api/medicines', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(fetchedMedicines => {
                medicines = fetchedMedicines;
                updateTable();
                renderPagination(medicines.length);
            })
            .catch(error => console.error('Ошибка при получении списка лекарств:', error));
    };

    // Обработчики событий
    searchBtn.addEventListener('click', () => {
        const filteredMedicines = searchMedicines(searchInput.value);
        currentPage = 1; // Сбрасываем на первую страницу при поиске
        renderMedicinesTable(filteredMedicines.slice(0, itemsPerPage));
        renderPagination(filteredMedicines.length);
    });

    document.getElementById('close-modal').addEventListener('click', () => {
        detailsModal.style.display = 'none';
    });

    document.getElementById('close-form-modal').addEventListener('click', () => {
        formModal.style.display = 'none';
    });

    document.getElementById('add-medicine-btn').addEventListener('click', () => {
        document.getElementById('form-title').textContent = 'Добавить лекарство';
        document.getElementById('medicine-id').value = '';
        document.getElementById('medicine-name').value = '';
        document.getElementById('manufacturer').value = '';
        formModal.style.display = 'block';
    });

    document.getElementById('edit-medicine-btn').addEventListener('click', () => {
        document.getElementById('form-title').textContent = 'Редактировать лекарство';
        document.getElementById('medicine-id').value = currentMedicineId;
        document.getElementById('medicine-name').value = document.getElementById('medicine-detail-name').textContent.split(': ')[1];
        document.getElementById('manufacturer').value = document.getElementById('medicine-detail-manufacturer').textContent.split(': ')[1];
        formModal.style.display = 'block';
    });

    document.getElementById('delete-medicine-btn').addEventListener('click', deleteMedicine);
    document.getElementById('medicine-form').addEventListener('submit', handleFormSubmit);

    // Инициализация
    fetchMedicines();
});