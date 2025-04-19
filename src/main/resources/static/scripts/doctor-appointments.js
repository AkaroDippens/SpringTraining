import { jwtDecode } from './jwt-decode.js';

document.addEventListener('DOMContentLoaded', async function () {
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
    let currentAppointmentId = null;

    const token = localStorage.getItem('authToken');
    const decodedToken = jwtDecode(token);
    const fullName = decodedToken.fullName;

    let doctorId = null;

    async function fetchDoctorId() {
        try {
            const response = await fetch(`/api/doctors/byfullname/${fullName}`);
            if (!response.ok) {
                throw new Error('Ошибка при запросе данных о докторе');
            }
            const doctor = await response.json();
            return doctor.id;
        } catch (error) {
            console.error('Ошибка при получении ID доктора:', error);
            return null;
        }
    }

    async function fetchRecordsByDoctorId(doctorId) {
        try {
            const response = await fetch(`/api/records/bydoctor/${doctorId}`);
            if (!response.ok) {
                throw new Error('Ошибка при запросе записей');
            }
            return await response.json();
        } catch (error) {
            console.error('Ошибка при получении записей:', error);
            return [];
        }
    }

    async function fetchAppointmentsByRecordIds(recordIds) {
        try {
            const appointments = await Promise.all(
                recordIds.map(async recordId => {
                    const response = await fetch(`/api/appointments/record/${recordId}`);
                    if (response.ok) {
                        return response.json();
                    } else {
                        console.warn(`Запись с ID ${recordId} не имеет приёма.`);
                        return null;
                    }
                })
            );
            return appointments.filter(appt => appt !== null);
        } catch (error) {
            console.error('Ошибка при загрузке приёмов:', error);
            return [];
        }
    }

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
                // Приём считается прошедшим только если оба поля diagnosis и recommendations заполнены
                const isPast = appointment && appointment.diagnosis && appointment.recommendations;

                if (isPast) {
                    pastRecords.push({ record, appointment });
                } else {
                    upcomingRecords.push({ record, appointment });
                }
            });

            // Сортировка по убыванию даты (самые свежие сверху)
            upcomingRecords.sort((a, b) => new Date(b.record.appointmentDate) - new Date(a.record.appointmentDate));
            pastRecords.sort((a, b) => new Date(b.record.appointmentDate) - new Date(a.record.appointmentDate));

            // Предстоящие
            if (upcomingRecords.length > 0) {
                upcomingRecords.forEach(({ record, appointment }) => {
                    const item = createAppointmentItem(record, appointment);
                    upcomingList.appendChild(item);
                });
            } else {
                upcomingList.innerHTML = '<p>Нет предстоящих записей.</p>';
            }

            // Прошедшие
            if (pastRecords.length > 0) {
                pastRecords.forEach(({ record, appointment }) => {
                    const item = createAppointmentItem(record, appointment);
                    pastList.appendChild(item);
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

    window.startAppointment = function (recordId) {
        fetch(`/api/records/${recordId}/start-appointment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Ошибка при создании приёма');
                }
            })
            .then(appointment => {
                alert('Приём успешно начат!');
                loadAppointments();
            })
            .catch(error => console.error('Ошибка:', error));
    };

    window.openAppointment = function (id) {
        fetch(`/api/appointments/${id}`)
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
        fetch(`/api/appointments/${id}`)
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
        if (upcomingList.style.display === 'block') {
            upcomingList.style.display = 'none';
        } else {
            upcomingList.style.display = 'block';
            pastList.style.display = 'none';
        }
    });

    pastHeader.addEventListener('click', () => {
        if (pastList.style.display === 'block') {
            pastList.style.display = 'none';
        } else {
            pastList.style.display = 'block';
            upcomingList.style.display = 'none';
        }
    });

    closeButton.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    closeAppointmentButton.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    closeViewButton.addEventListener('click', () => {
        viewModal.style.display = 'none';
    });

    closeViewAppointmentButton.addEventListener('click', () => {
        viewModal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        } else if (event.target === viewModal) {
            viewModal.style.display = 'none';
        }
    });

    doctorId = await fetchDoctorId();
    if (doctorId) {
        loadAppointments();
    }
});