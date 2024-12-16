document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('specialization-table-body');
    const detailsModal = document.getElementById('specialization-details-modal');
    const formModal = document.getElementById('specialization-form-modal');
    const closeDetailsModal = document.getElementById('close-modal');
    const closeFormModal = document.getElementById('close-form-modal');
    const addSpecializationBtn = document.getElementById('add-specialization-btn');
    const editSpecializationBtn = document.getElementById('edit-specialization-btn');
    const deleteSpecializationBtn = document.getElementById('delete-specialization-btn');
    const specializationForm = document.getElementById('specialization-form');
    const formTitle = document.getElementById('form-title');
    const specializationIdInput = document.getElementById('specialization-id');
    const specializationNameInput = document.getElementById('specialization-name');

    let currentSpecializationId = null;

    // Функция для вывода данных в таблицу
    const renderSpecializationsTable = (specializations) => {
        tableBody.innerHTML = ''; // Очистить таблицу перед рендером
        specializations.forEach(specialization => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${specialization.id}</td>
                <td>${specialization.specializationName}</td>
                <td><button class="btn" onclick="showSpecializationDetails(${specialization.id})">Подробнее</button></td>
            `;
            tableBody.appendChild(row);
        });
    };

    // Функция для отображения детальной информации
    window.showSpecializationDetails = (specializationId) => {
        fetch(`/api/specializations/${specializationId}`)
            .then(response => response.json())
            .then(specialization => {
                document.getElementById('specialization-detail-id').textContent = `ID: ${specialization.id}`;
                document.getElementById('specialization-detail-name').textContent = `Название специализации: ${specialization.specializationName}`;
                currentSpecializationId = specialization.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка при получении данных о специализации:', error);
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

    // Открытие модального окна для добавления новой специализации
    addSpecializationBtn.onclick = () => {
        formTitle.textContent = 'Добавить специализацию';
        specializationIdInput.value = '';
        specializationNameInput.value = '';
        formModal.style.display = 'block';
    };

    // Открытие модального окна для редактирования специализации
    editSpecializationBtn.onclick = () => {
        formTitle.textContent = 'Редактировать специализацию';
        specializationIdInput.value = currentSpecializationId;
        specializationNameInput.value = document.getElementById('specialization-detail-name').textContent.split(': ')[1];
        formModal.style.display = 'block';
    };

    // Удаление специализации
    deleteSpecializationBtn.onclick = () => {
        if (confirm('Вы уверены, что хотите удалить эту специализацию?')) {
            fetch(`/api/specializations/${currentSpecializationId}`, {
                method: 'DELETE'
            })
            .then(response => {
                if (response.ok) {
                    fetchSpecializations();
                    detailsModal.style.display = 'none';
                } else {
                    alert('Ошибка при удалении специализации');
                }
            })
            .catch(error => {
                console.error('Ошибка при удалении специализации:', error);
            });
        }
    };

    // Обработка отправки формы
    specializationForm.onsubmit = (event) => {
        event.preventDefault();
        const specialization = {
            id: specializationIdInput.value || null,
            specializationName: specializationNameInput.value
        };

        const method = specialization.id ? 'PUT' : 'POST';
        const url = specialization.id ? `/api/specializations/${specialization.id}` : '/api/specializations';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(specialization)
        })
        .then(response => {
            if (response.ok) {
                fetchSpecializations();
                formModal.style.display = 'none';
            } else {
                alert('Ошибка при сохранении специализации');
            }
        })
        .catch(error => {
            console.error('Ошибка при сохранении специализации:', error);
        });
    };

    // Получаем данные о специализациях с сервера
    const fetchSpecializations = () => {
        fetch('/api/specializations')
            .then(response => response.json())
            .then(specializations => {
                renderSpecializationsTable(specializations);
            })
            .catch(error => {
                console.error('Ошибка при получении списка специализаций:', error);
            });
    };

    fetchSpecializations();
});