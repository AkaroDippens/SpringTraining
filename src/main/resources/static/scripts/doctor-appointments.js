import { jwtDecode } from './jwt-decode.js';

document.addEventListener('DOMContentLoaded', async function () {
    // DOM элементы
    const upcomingList = document.getElementById('upcoming-list');
    const pastList = document.getElementById('past-list');
    const upcomingHeader = document.getElementById('upcoming-header');
    const pastHeader = document.getElementById('past-header');
    const modal = document.getElementById('appointment-modal');
    const viewModal = document.getElementById('view-appointment-modal');
    const closeButton = document.getElementById('close-modal');
    const closeAppointmentButton = document.getElementById('close-appointment-modal');
    const saveChangesButton = document.getElementById('save-changes');
    const completeAppointmentButton = document.getElementById('complete-appointment');
    const closeViewButton = document.getElementById('close-view-modal');
    const closeViewAppointmentButton = document.getElementById('close-view-appointment');

    // Состояние приложения
    let currentAppointmentId = null;
    const token = localStorage.getItem('authToken');
    const decodedToken = jwtDecode(token);
    const fullName = decodedToken.fullName;
    let doctorId = null;

    // Получение ID доктора
    async function fetchDoctorId() {
        try {
            const response = await fetch(`/api/doctors/byfullname/${fullName}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) throw new Error('Ошибка при запросе данных о докторе');
            const doctor = await response.json();
            return doctor.id;
        } catch (error) {
            console.error('Ошибка при получении ID доктора:', error);
            return null;
        }
    }

    // Получение записей по ID доктора
    async function fetchRecordsByDoctorId(doctorId) {
        try {
            const response = await fetch(`/api/records/bydoctor/${doctorId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) throw new Error('Ошибка при запросе записей');
            return await response.json();
        } catch (error) {
            console.error('Ошибка при получении записей:', error);
            return [];
        }
    }

    // Получение приемов по ID записей
    async function fetchAppointmentsByRecordIds(recordIds) {
        try {
            const appointments = await Promise.all(
                recordIds.map(async recordId => {
                    const response = await fetch(`/api/appointments/record/${recordId}`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    });

                    if (response.ok) return response.json();
                    console.warn(`Запись с ID ${recordId} не имеет приёма.`);
                    return null;
                })
            );

            return appointments.filter(appt => appt !== null);
        } catch (error) {
            console.error('Ошибка при загрузке приёмов:', error);
            return [];
        }
    }

    // Загрузка приемов
    async function loadAppointments() {
        try {
            if (!doctorId) {
                console.error('Не удалось загрузить приёмы, ID доктора отсутствует.');
                return;
            }

            const records = await fetchRecordsByDoctorId(doctorId);
            if (records.length === 0) {
                upcomingList.innerHTML = '<p>Нет предстоящих записей.</p>';
                pastList.innerHTML = '<p>Нет прошедших записей.</p>';
                return;
            }

            const recordIds = records.map(record => record.id);
            const appointments = await fetchAppointmentsByRecordIds(recordIds);

            upcomingList.innerHTML = '';
            pastList.innerHTML = '';

            const upcomingRecords = [];
            const pastRecords = [];

            records.forEach(record => {
                const appointment = appointments.find(appt => appt.idRecord.id === record.id);
                const isPast = appointment && appointment.diagnosis && appointment.recommendations;

                if (isPast) pastRecords.push({ record, appointment });
                else upcomingRecords.push({ record, appointment });
            });

            upcomingRecords.sort((a, b) => new Date(b.record.appointmentDate) - new Date(a.record.appointmentDate));
            pastRecords.sort((a, b) => new Date(b.record.appointmentDate) - new Date(a.record.appointmentDate));

            if (upcomingRecords.length > 0) {
                upcomingRecords.forEach(({ record, appointment }) => {
                    upcomingList.appendChild(createAppointmentItem(record, appointment));
                });
            } else {
                upcomingList.innerHTML = '<p>Нет предстоящих записей.</p>';
            }

            if (pastRecords.length > 0) {
                pastRecords.forEach(({ record, appointment }) => {
                    pastList.appendChild(createAppointmentItem(record, appointment));
                });
            } else {
                pastList.innerHTML = '<p>Нет прошедших записей.</p>';
            }

            upcomingList.style.display = 'block';
            pastList.style.display = 'none';
        } catch (error) {
            console.error('Ошибка при загрузке приёмов:', error);
        }
    }

    // Создание элемента приема
    function createAppointmentItem(record, appointment) {
        const item = document.createElement('div');
        item.className = 'history-item';
        const isPast = appointment && appointment.diagnosis && appointment.recommendations;

        item.innerHTML = `
            <div><strong>Дата:</strong> ${record.appointmentDate.split('T')[0]} ${record.appointmentDate.split('T')[1].slice(0, 5)}</div>
            <div><strong>Пациент:</strong> ${record.idUser.fullName}</div>
            <div><strong>Полис:</strong> ${record.idUser.mhiPolicy}</div>
            ${appointment ? 
                (isPast ? 
                    `<button class="btn" onclick="viewAppointment(${appointment.id})">Просмотреть</button>` :
                    `<button class="btn" onclick="openAppointment(${appointment.id})">Редактировать приём</button>`) :
                `<button class="btn" onclick="startAppointment(${record.id})">Начать приём</button>`
            }
        `;

        return item;
    }

    // Глобальные функции для обработки событий
    window.startAppointment = function (recordId) {
        fetch(`/api/records/${recordId}/start-appointment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (response.ok) return response.json();
                throw new Error('Ошибка при создании приёма');
            })
            .then(() => {
                alert('Приём успешно начат!');
                loadAppointments();
            })
            .catch(error => console.error('Ошибка:', error));
    };

    window.openAppointment = function (id) {
        fetch(`/api/appointments/${id}`, { headers: { 'Authorization': `Bearer ${token}` } })
            .then(response => response.json())
            .then(appt => {
                const isPast = appt.diagnosis && appt.recommendations;
                if (isPast) {
                    alert('Этот приём уже завершён и не подлежит редактированию.');
                    return;
                }

                currentAppointmentId = appt.id;
                document.getElementById('reason').value = appt.reason || '';
                document.getElementById('diagnosis').value = appt.diagnosis || '';
                document.getElementById('recommendations').value = appt.recommendations || '';
                modal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка при загрузке данных приёма:', error));
    };

    window.viewAppointment = function (id) {
        fetch(`/api/appointments/${id}`, { headers: { 'Authorization': `Bearer ${token}` } })
            .then(response => response.json())
            .then(appt => {
                document.getElementById('view-date').textContent = appt.idRecord.appointmentDate.split('T')[0];
                document.getElementById('view-reason').textContent = appt.reason || 'Не указано';
                document.getElementById('view-diagnosis').textContent = appt.diagnosis || 'Не указано';
                document.getElementById('view-recommendations').textContent = appt.recommendations || 'Не указано';
                viewModal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка при загрузке данных приёма:', error));
    };

    // Обработчики событий
    saveChangesButton.addEventListener('click', () => {
        const updatedData = {
            reason: document.getElementById('reason').value,
            diagnosis: document.getElementById('diagnosis').value,
            recommendations: document.getElementById('recommendations').value
        };

        fetch(`/api/appointments/${currentAppointmentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updatedData)
        })
            .then(response => {
                if (response.ok) {
                    alert('Изменения сохранены!');
                    modal.style.display = 'none';
                    loadAppointments();
                } else {
                    throw new Error('Ошибка при сохранении изменений');
                }
            })
            .catch(error => console.error('Ошибка при обновлении данных приёма:', error));
    });

    completeAppointmentButton.addEventListener('click', () => {
        const updatedData = {
            reason: document.getElementById('reason').value,
            diagnosis: document.getElementById('diagnosis').value || 'Диагноз не указан',
            recommendations: document.getElementById('recommendations').value || 'Рекомендации не указаны'
        };

        fetch(`/api/appointments/${currentAppointmentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updatedData)
        })
            .then(response => {
                if (response.ok) {
                    alert('Приём завершён!');
                    modal.style.display = 'none';
                    loadAppointments();
                } else {
                    throw new Error('Ошибка при завершении приёма');
                }
            })
            .catch(error => console.error('Ошибка при завершении приёма:', error));
    });

    upcomingHeader.addEventListener('click', () => {
        upcomingList.style.display = upcomingList.style.display === 'block' ? 'none' : 'block';
        pastList.style.display = 'none';
    });

    pastHeader.addEventListener('click', () => {
        pastList.style.display = pastList.style.display === 'block' ? 'none' : 'block';
        upcomingList.style.display = 'none';
    });

    // Закрытие модальных окон
    [closeButton, closeAppointmentButton].forEach(btn =>
        btn.addEventListener('click', () => modal.style.display = 'none')
    );

    [closeViewButton, closeViewAppointmentButton].forEach(btn =>
        btn.addEventListener('click', () => viewModal.style.display = 'none')
    );

    window.addEventListener('click', (event) => {
        if (event.target === modal) modal.style.display = 'none';
        else if (event.target === viewModal) viewModal.style.display = 'none';
    });

    // Инициализация
    doctorId = await fetchDoctorId();
    if (doctorId) loadAppointments();
});