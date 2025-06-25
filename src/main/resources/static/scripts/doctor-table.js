document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const tableBody = document.getElementById('doctor-table-body');
    const detailsModal = document.getElementById('doctor-details-modal');
    const changeSpecializationModal = document.getElementById('change-specialization-modal');
    const changeBuildingModal = document.getElementById('change-building-modal');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const paginationContainer = document.getElementById('pagination');

    // Состояние
    let doctors = [];
    let currentDoctorId = null;
    const authToken = localStorage.getItem('authToken');
    const itemsPerPage = 10;
    let currentPage = 1;

    // Рендер таблицы врачей
    const renderDoctorsTable = (doctorsToRender) => {
        tableBody.innerHTML = '';

        doctorsToRender.forEach(doctor => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${doctor.fullName}</td>
                <td>${doctor.idSpecialization.specializationName}</td>
                <td>${doctor.idBuilding.buildingName}</td>
                <td>${doctor.experience}</td>
                <td><button class="btn" onclick="showDoctorDetails(${doctor.id})">Подробнее</button></td>
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
        renderDoctorsTable(doctors.slice(startIndex, endIndex));
    };

    // Поиск врачей
    const searchDoctors = (query) => {
        if (!query.trim()) return doctors;

        return doctors.filter(doctor =>
            doctor.fullName.toLowerCase().includes(query.toLowerCase()) ||
            doctor.idSpecialization.specializationName.toLowerCase().includes(query.toLowerCase())
        );
    };

    // Показать детали врача
    window.showDoctorDetails = (doctorId) => {
        fetch(`/api/doctors/${doctorId}`, { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(doctor => {
                document.getElementById('doctor-detail-full-name').textContent = `ФИО: ${doctor.fullName}`;
                document.getElementById('doctor-detail-specialization').textContent = `Специализация: ${doctor.idSpecialization.specializationName}`;
                document.getElementById('doctor-detail-building').textContent = `Строение: ${doctor.idBuilding.buildingName}`;
                document.getElementById('doctor-detail-experience').textContent = `Стаж: ${doctor.experience}`;

                currentDoctorId = doctor.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка при получении данных о враче:', error));
    };

    // Загрузка специализаций для выпадающего списка
    const fetchSpecializations = () => {
        fetch('/api/specializations', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(specializations => {
                const select = document.getElementById('specialization');
                select.innerHTML = '';

                specializations.forEach(spec => {
                    const option = document.createElement('option');
                    option.value = spec.id;
                    option.textContent = spec.specializationName;
                    select.appendChild(option);
                });
            })
            .catch(error => console.error('Ошибка при получении списка специализаций:', error));
    };

    // Загрузка корпусов для выпадающего списка
    const fetchBuildings = () => {
        fetch('/api/buildings', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(buildings => {
                const select = document.getElementById('building');
                select.innerHTML = '';

                buildings.forEach(building => {
                    const option = document.createElement('option');
                    option.value = building.id;
                    option.textContent = building.buildingName;
                    select.appendChild(option);
                });
            })
            .catch(error => console.error('Ошибка при получении списка корпусов:', error));
    };

    // Загрузка врачей
    const fetchDoctors = () => {
        fetch('/api/doctors', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(fetchedDoctors => {
                doctors = fetchedDoctors;
                updateTable();
                renderPagination(doctors.length);
            })
            .catch(error => console.error('Ошибка при получении списка врачей:', error));
    };

    // Обработчики событий
    searchBtn.addEventListener('click', () => {
        const filteredDoctors = searchDoctors(searchInput.value);
        currentPage = 1; // Сбрасываем на первую страницу при поиске
        renderDoctorsTable(filteredDoctors.slice(0, itemsPerPage));
        renderPagination(filteredDoctors.length);
    });

    document.getElementById('close-modal').addEventListener('click', () => {
        detailsModal.style.display = 'none';
    });

    document.getElementById('change-specialization-btn').addEventListener('click', () => {
        document.getElementById('doctor-id').value = currentDoctorId;
        fetchSpecializations();
        changeSpecializationModal.style.display = 'block';
    });

    document.getElementById('close-change-specialization-modal').addEventListener('click', () => {
        changeSpecializationModal.style.display = 'none';
    });

    document.getElementById('change-specialization-form').addEventListener('submit', (event) => {
        event.preventDefault();
        const specializationId = document.getElementById('specialization').value;

        fetch(`/api/doctors/${currentDoctorId}/specialization?specializationId=${specializationId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'PUT'
        })
            .then(response => {
                if (response.ok) {
                    fetchDoctors();
                    changeSpecializationModal.style.display = 'none';
                    detailsModal.style.display = 'none';
                } else {
                    alert('Ошибка при изменении специализации');
                }
            })
            .catch(error => console.error('Ошибка при изменении специализации:', error));
    });

    document.getElementById('change-building-btn').addEventListener('click', () => {
        document.getElementById('building-doctor-id').value = currentDoctorId;
        fetchBuildings();
        changeBuildingModal.style.display = 'block';
    });

    document.getElementById('close-change-building-modal').addEventListener('click', () => {
        changeBuildingModal.style.display = 'none';
    });

    document.getElementById('change-building-form').addEventListener('submit', (event) => {
        event.preventDefault();
        const buildingId = document.getElementById('building').value;

        fetch(`/api/doctors/${currentDoctorId}/building?buildingId=${buildingId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'PUT'
        })
            .then(response => {
                if (response.ok) {
                    fetchDoctors();
                    changeBuildingModal.style.display = 'none';
                    detailsModal.style.display = 'none';
                } else {
                    alert('Ошибка при изменении корпуса');
                }
            })
            .catch(error => console.error('Ошибка при изменении корпуса:', error));
    });

    // Инициализация
    fetchDoctors();
});