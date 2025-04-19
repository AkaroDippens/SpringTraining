document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('medicine-table-body');
    const detailsModal = document.getElementById('medicine-details-modal');
    const formModal = document.getElementById('medicine-form-modal');
    const closeDetailsModal = document.getElementById('close-modal');
    const closeFormModal = document.getElementById('close-form-modal');
    const addMedicineBtn = document.getElementById('add-medicine-btn');
    const editMedicineBtn = document.getElementById('edit-medicine-btn');
    const deleteMedicineBtn = document.getElementById('delete-medicine-btn');
    const medicineForm = document.getElementById('medicine-form');
    const formTitle = document.getElementById('form-title');
    const medicineIdInput = document.getElementById('medicine-id');
    const medicineNameInput = document.getElementById('medicine-name');
    const manufacturerInput = document.getElementById('manufacturer');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');

    let medicines = [];

    let currentMedicineId = null;

    // Функция для вывода данных в таблицу
    const renderMedicinesTable = (medicines) => {
        tableBody.innerHTML = ''; // Очистить таблицу перед рендером
        medicines.forEach(medicine => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${medicine.id}</td>
                <td>${medicine.medicineName}</td>
                <td>${medicine.manufacturer}</td>
                <td><button class="btn" onclick="showMedicineDetails(${medicine.id})">Подробнее</button></td>
            `;
            tableBody.appendChild(row);
        });
    };

    // Функция для отображения детальной информации
    window.showMedicineDetails = (medicineId) => {
        fetch(`/api/medicines/${medicineId}`)
            .then(response => response.json())
            .then(medicine => {
                document.getElementById('medicine-detail-id').textContent = `ID: ${medicine.id}`;
                document.getElementById('medicine-detail-name').textContent = `Название лекарства: ${medicine.medicineName}`;
                document.getElementById('medicine-detail-manufacturer').textContent = `Производитель: ${medicine.manufacturer}`;
                currentMedicineId = medicine.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка при получении данных о лекарстве:', error);
            });
    };

    // Закрытие модального окна с деталями
    closeDetailsModal.onclick = () => {
        detailsModal.style.display = 'none';
    };

    // Закрытие модального окна с формой
    closeFormModal.onclick = () => {
        formModal.style.display = 'none';
    };

    const searchMedicines = (query) => {
        if (!query.trim()) return medicines;

        return medicines.filter(medicine =>
            medicine.medicineName.toLowerCase().includes(query.toLowerCase()) ||
            medicine.manufacturer.toLowerCase().includes(query.toLowerCase())
        );
    };

    searchBtn.onclick = () => {
        const filteredMedicines = searchMedicines(searchInput.value);
        renderMedicinesTable(filteredMedicines);
    };


    // Открытие модального окна для добавления нового лекарства
    addMedicineBtn.onclick = () => {
        formTitle.textContent = 'Добавить лекарство';
        medicineIdInput.value = '';
        medicineNameInput.value = '';
        manufacturerInput.value = '';
        formModal.style.display = 'block';
    };

    // Открытие модального окна для редактирования лекарства
    editMedicineBtn.onclick = () => {
        formTitle.textContent = 'Редактировать лекарство';
        medicineIdInput.value = currentMedicineId;
        medicineNameInput.value = document.getElementById('medicine-detail-name').textContent.split(': ')[1];
        manufacturerInput.value = document.getElementById('medicine-detail-manufacturer').textContent.split(': ')[1];
        formModal.style.display = 'block';
    };

    // Удаление лекарства
    deleteMedicineBtn.onclick = () => {
        if (confirm('Вы уверены, что хотите удалить это лекарство?')) {
            fetch(`/api/medicines/${currentMedicineId}`, {
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
            .catch(error => {
                console.error('Ошибка при удалении лекарства:', error);
            });
        }
    };

    // Обработка отправки формы
    medicineForm.onsubmit = (event) => {
        event.preventDefault();
        const medicine = {
            id: medicineIdInput.value || null,
            medicineName: medicineNameInput.value,
            manufacturer: manufacturerInput.value
        };

        const method = medicine.id ? 'PUT' : 'POST';
        const url = medicine.id ? `/api/medicines/${medicine.id}` : '/api/medicines';

        fetch(url, {
            method: method,
            headers: {
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
        .catch(error => {
            console.error('Ошибка при сохранении лекарства:', error);
        });
    };

    // Получаем данные о лекарствах с сервера
    const fetchMedicines = () => {
        fetch('/api/medicines')
            .then(response => response.json())
            .then(fetchMedicines => {
                medicines = fetchMedicines
                renderMedicinesTable(medicines);
            })
            .catch(error => {
                console.error('Ошибка при получении списка лекарств:', error);
            });
    };

    fetchMedicines();
});