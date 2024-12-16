document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('doctor-table-body');
    const detailsModal = document.getElementById('doctor-details-modal');
    const changeSpecializationModal = document.getElementById('change-specialization-modal');
    const closeDetailsModal = document.getElementById('close-modal');
    const closeChangeSpecializationModal = document.getElementById('close-change-specialization-modal');
    const changeSpecializationBtn = document.getElementById('change-specialization-btn');
    const changeSpecializationForm = document.getElementById('change-specialization-form');
    const doctorIdInput = document.getElementById('doctor-id');
    const specializationSelect = document.getElementById('specialization');

    let currentDoctorId = null;

    // Функция для вывода данных в таблицу
    const renderDoctorsTable = (doctors) => {
        tableBody.innerHTML = ''; // Очистить таблицу перед рендером
        doctors.forEach(doctor => {
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

    // Функция для отображения детальной информации
    window.showDoctorDetails = (doctorId) => {
        fetch(`/api/doctors/${doctorId}`)
            .then(response => response.json())
            .then(doctor => {
                document.getElementById('doctor-detail-full-name').textContent = `ФИО: ${doctor.fullName}`;
                document.getElementById('doctor-detail-specialization').textContent = `Специализация: ${doctor.idSpecialization.specializationName}`;
                document.getElementById('doctor-detail-building').textContent = `Строение: ${doctor.idBuilding.buildingName}`;
                document.getElementById('doctor-detail-experience').textContent = `Стаж: ${doctor.experience}`;
                currentDoctorId = doctor.id;
                detailsModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка при получении данных о враче:', error);
            });
    };

    // Закрытие модального окна с деталями
    closeDetailsModal.onclick = () => {
        detailsModal.style.display = 'none';
    };

    // Закрытие модального окна для изменения специализации
    closeChangeSpecializationModal.onclick = () => {
        changeSpecializationModal.style.display = 'none';
    };

    // Открытие модального окна для изменения специализации
    changeSpecializationBtn.onclick = () => {
        doctorIdInput.value = currentDoctorId;
        fetchSpecializations();
        changeSpecializationModal.style.display = 'block';
    };

    // Обработка отправки формы для изменения специализации
    changeSpecializationForm.onsubmit = (event) => {
        event.preventDefault();
        const specializationId = specializationSelect.value;

        fetch(`/api/doctors/${currentDoctorId}/specialization?specializationId=${specializationId}`, {
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
        .catch(error => {
            console.error('Ошибка при изменении специализации:', error);
        });
    };

    // Получаем данные о специализациях для выпадающего списка
    const fetchSpecializations = () => {
        fetch('/api/specializations')
            .then(response => response.json())
            .then(specializations => {
                specializationSelect.innerHTML = '';
                specializations.forEach(specialization => {
                    const option = document.createElement('option');
                    option.value = specialization.id;
                    option.textContent = specialization.specializationName;
                    specializationSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Ошибка при получении списка специализаций:', error);
            });
    };

    // Получаем данные о врачах с сервера
    const fetchDoctors = () => {
        fetch('/api/doctors')
            .then(response => response.json())
            .then(doctors => {
                renderDoctorsTable(doctors);
            })
            .catch(error => {
                console.error('Ошибка при получении списка врачей:', error);
            });
    };

    fetchDoctors();
});