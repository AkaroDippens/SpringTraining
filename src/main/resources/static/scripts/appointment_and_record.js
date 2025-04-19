import { InvalidTokenError, b64DecodeUnicode, base64_url_decode, jwtDecode } from './jwt-decode.js';

document.addEventListener('DOMContentLoaded', () => {
    const specializationList = document.getElementById('specialization-list');
    const doctorList = document.getElementById('doctor-list');
    const dateGrid = document.getElementById('date-grid');
    const timeGrid = document.getElementById('time-grid');
    const confirmButton = document.getElementById('confirm-record');
    const doctorSection = document.getElementById('doctor-section');
    const datetimeSection = document.getElementById('datetime-section');

    let selectedSpecializationId = null;
    let selectedDoctorId = null;
    let selectedDoctor = null;
    let selectedBuildingId = null;
    let selectedDate = null;
    let selectedTime = null;

    const timeSlots = [
        '09:00', '09:30', '10:00', '10:30',
        '11:00', '11:30', '12:00', '12:30',
        '13:00', '13:30', '14:00', '14:30',
        '15:00', '15:30', '16:00', '16:30'
    ];

    // Загружаем список специализаций
    function loadSpecializations() {
        fetch('/api/specializations')
            .then(response => response.json())
            .then(specializations => {
                specializationList.innerHTML = '';
                specializations.forEach(spec => {
                    const button = document.createElement('button');
                    button.className = 'btn-specialization';
                    button.textContent = spec.specializationName;
                    button.onclick = () => {
                        selectedSpecializationId = spec.id;
                        doctorList.innerHTML = ''; // Очищаем список врачей
                        dateGrid.innerHTML = ''; // Очищаем даты
                        timeGrid.innerHTML = ''; // Очищаем время
                        loadDoctorsBySpecialization(spec.id);
                        doctorSection.style.display = 'block';
                        datetimeSection.style.display = 'none';
                        document.querySelectorAll('.btn-specialization').forEach(btn => btn.classList.remove('selected'));
                        // Добавляем класс selected к выбранной кнопке
                        button.classList.add('selected');
                    };
                    specializationList.appendChild(button);
                });
            })
            .catch(console.error);
    }

    // Загружаем список врачей по специализации
    function loadDoctorsBySpecialization(specializationId) {
        fetch(`/api/doctors/byspecialization/${specializationId}`)
            .then(response => response.json())
            .then(doctors => {
                doctorList.innerHTML = '';
                doctors.forEach(doctor => {
                    const button = document.createElement('button');
                    button.className = 'btn-doctor';
                    button.innerHTML = `
                        <p><strong>ФИО:</strong> ${doctor.fullName}</p>
                        <p><strong>Корпус:</strong> ${doctor.idBuilding.buildingName}, ${doctor.idBuilding.address}</p>
                        <p><strong>Стаж работы:</strong> ${doctor.experience}</p>
                    `;
                    button.onclick = () => {
                        selectedDoctor = doctor;
                        selectedDoctorId = doctor.id;
                        selectedBuildingId = doctor.idBuilding.id;
                        loadDates();
                        datetimeSection.style.display = 'block';
                        document.querySelectorAll('.btn-doctor').forEach(btn => btn.classList.remove('selected'));
                        button.classList.add('selected');
                    };
                    doctorList.appendChild(button);
                });
            })
            .catch(console.error);
    }

    // Загружаем список дат
    function loadDates() {
        dateGrid.innerHTML = '';
        const startDate = new Date();
        for (let i = 0; i < 7; i++) {
            const date = new Date(startDate);
            date.setDate(startDate.getDate() + i);
            const btn = document.createElement('button');
            btn.className = 'date-card';
            btn.textContent = date.toLocaleDateString('ru-RU');
            btn.onclick = () => selectDate(date, btn);
            dateGrid.appendChild(btn);
        }
    }

    // Выбор даты
    function selectDate(date, btn) {
        selectedDate = formatDate(date);
        clearSelection('date');
        btn.classList.add('selected');
        loadTimeSlots(selectedDate);
    }

    // Загружаем доступное время
    function loadTimeSlots() {
        timeGrid.innerHTML = '';
        timeSlots.forEach((slot) => {
            const button = document.createElement('button');
            button.className = 'btn-time';
            button.textContent = slot;
            button.onclick = () => {
                document.querySelectorAll('.btn-time.selected').forEach(b => b.classList.remove('selected'));
                button.classList.add('selected');
                selectedTime = slot;
                confirmButton.style.display = 'block';
            };
            timeGrid.appendChild(button);
        });
    }

    // Подтверждаем запись
    confirmButton.onclick = () => {
        if (!selectedSpecializationId || !selectedDoctorId || !selectedBuildingId || !selectedDate || !selectedTime) {
            alert('Заполните все поля для записи.');
            return;
        }

        const appointmentDateTime = `${selectedDate}T${selectedTime}:00Z`;
        const token = localStorage.getItem('authToken');

        const decodedToken = jwtDecode(token);
        const userId = decodedToken.userId;

        fetch('/api/records', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                idDoctor: selectedDoctor,
                idBuilding: { id: selectedBuildingId },
                idUser: { id: userId },
                appointmentDate: appointmentDateTime
            })
        })
            .then(response => {
                if (response.ok) {
                    alert('Запись успешно создана.');
                    loadSpecializations(); // Сбрасываем интерфейс
                    doctorSection.style.display = 'none';
                    datetimeSection.style.display = 'none';
                    confirmButton.style.display = 'none';
                } else {
                    alert('Выбранное время занято.');
                }
            })
            .catch(console.error);
    };

    // Очистка выбранного элемента
    function clearSelection(type) {
        switch (type) {
            case 'date':
                dateGrid.querySelectorAll('.date-card').forEach(btn => btn.classList.remove('selected'));
                break;
        }
    }

    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0'); // Месяцы от 0 до 11
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    // Инициализация
    loadSpecializations();
});