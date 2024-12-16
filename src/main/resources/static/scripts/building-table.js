document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('building-table-body');
    const detailsModal = document.getElementById('building-details-modal');
    const formModal = document.getElementById('building-form-modal');
    const closeDetailsModal = document.getElementById('close-modal');
    const closeFormModal = document.getElementById('close-form-modal');
    const addBuildingBtn = document.getElementById('add-building-btn');
    const editBuildingBtn = document.getElementById('edit-building-btn');
    const deleteBuildingBtn = document.getElementById('delete-building-btn');
    const buildingForm = document.getElementById('building-form');
    const formTitle = document.getElementById('form-title');
    const buildingIdInput = document.getElementById('building-id');
    const buildingNameInput = document.getElementById('building-name');
    const addressInput = document.getElementById('address');
    const contactNumberInput = document.getElementById('contact-number');

    let currentBuildingId = null;

    // Функция для вывода данных в таблицу
    const renderBuildingsTable = (buildings) => {
        tableBody.innerHTML = ''; // Очистить таблицу перед рендером
        buildings.forEach(building => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${building.id}</td>
                <td>${building.buildingName}</td>
                <td>${building.address}</td>
                <td>${building.contactNumber}</td>
                <td><button class="btn" onclick="showBuildingDetails(${building.id})">Подробнее</button></td>
            `;
            tableBody.appendChild(row);
        });
    };

    // Функция для отображения детальной информации
    window.showBuildingDetails = (buildingId) => {
        fetch(`/api/buildings/${buildingId}`)
            .then(response => response.json())
            .then(building => {
                document.getElementById('building-detail-id').textContent = `ID: ${building.id}`;
                document.getElementById('building-detail-name').textContent = `Название здания: ${building.buildingName}`;
                document.getElementById('building-detail-address').textContent = `Адрес: ${building.address}`;
                document.getElementById('building-detail-contact').textContent = `Контактный номер: ${building.contactNumber}`;
                currentBuildingId = building.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка при получении данных о здании:', error);
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

    // Открытие модального окна для добавления нового здания
    addBuildingBtn.onclick = () => {
        formTitle.textContent = 'Добавить здание';
        buildingIdInput.value = '';
        buildingNameInput.value = '';
        addressInput.value = '';
        contactNumberInput.value = '';
        formModal.style.display = 'block';
    };

    // Открытие модального окна для редактирования здания
    editBuildingBtn.onclick = () => {
        formTitle.textContent = 'Редактировать здание';
        buildingIdInput.value = currentBuildingId;
        buildingNameInput.value = document.getElementById('building-detail-name').textContent.split(': ')[1];
        addressInput.value = document.getElementById('building-detail-address').textContent.split(': ')[1];
        contactNumberInput.value = document.getElementById('building-detail-contact').textContent.split(': ')[1];
        formModal.style.display = 'block';
    };

    // Удаление здания
    deleteBuildingBtn.onclick = () => {
        if (confirm('Вы уверены, что хотите удалить это здание?')) {
            fetch(`/api/buildings/${currentBuildingId}`, {
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
            .catch(error => {
                console.error('Ошибка при удалении здания:', error);
            });
        }
    };

    // Обработка отправки формы
    buildingForm.onsubmit = (event) => {
        event.preventDefault();
        const building = {
            id: buildingIdInput.value || null,
            buildingName: buildingNameInput.value,
            address: addressInput.value,
            contactNumber: contactNumberInput.value
        };

        const method = building.id ? 'PUT' : 'POST';
        const url = building.id ? `/api/buildings/${building.id}` : '/api/buildings';

        fetch(url, {
            method: method,
            headers: {
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
        .catch(error => {
            console.error('Ошибка при сохранении здания:', error);
        });
    };

    // Получаем данные о зданиях с сервера
    const fetchBuildings = () => {
        fetch('/api/buildings')
            .then(response => response.json())
            .then(buildings => {
                renderBuildingsTable(buildings);
            })
            .catch(error => {
                console.error('Ошибка при получении списка зданий:', error);
            });
    };

    fetchBuildings();
});