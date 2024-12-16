import { jwtDecode } from './jwt-decode.js';

document.addEventListener('DOMContentLoaded', async function () {
    const appointmentsList = document.getElementById('appointments-list');
    const modal = document.getElementById('appointment-modal');
    const closeButton = document.getElementById('close-modal');
    const closeAppointmentButton = document.getElementById('close-appointment-modal');
    const saveChangesButton = document.getElementById('save-changes');
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
            const records = await response.json();
            return records.map(record => record.id); // Возвращаем только ID записей
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
            return appointments.filter(appt => appt !== null); // Исключаем записи без приёмов
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

            // Шаг 1: Получить ID записей, связанных с доктором
            const recordIds = await fetchRecordsByDoctorId(doctorId);
            if (recordIds.length === 0) {
                appointmentsList.innerHTML = '<p>Нет записей, связанных с этим доктором.</p>';
                return;
            }

            // Шаг 2: Получить приёмы по ID записей
            const appointments = await fetchAppointmentsByRecordIds(recordIds);

            // Фильтрация только предстоящих приёмов
            const now = new Date();
            const upcomingAppointments = appointments.filter(appt => {
                const apptDate = new Date(appt.idRecord.appointmentDate);
                return apptDate > now; // Только предстоящие приёмы
            });

            // Отображение приёмов
            appointmentsList.innerHTML = ''; // Очищаем список
            if (upcomingAppointments.length > 0) {
                upcomingAppointments.forEach(appt => {
                    const item = createAppointmentItem(appt);
                    appointmentsList.appendChild(item);
                });
            } else {
                appointmentsList.innerHTML = '<p>Нет предстоящих приёмов.</p>';
            }
        } catch (error) {
            console.error('Ошибка при загрузке приёмов:', error);
        }
    }

    function createAppointmentItem(appt) {
        const item = document.createElement('div');
        item.className = 'history-item';
        item.innerHTML = `
            <div><strong>Дата:</strong> ${appt.idRecord.appointmentDate.split('T')[0]} ${appt.idRecord.appointmentDate.split('T')[1].slice(0, 5)}</div>
            <div><strong>Пациент:</strong> ${appt.idRecord.idUser.fullName}</div>
            <div><strong>Полис:</strong> ${appt.idRecord.idUser.mhiPolicy}</div>
            <button class="btn" onclick="openAppointment(${appt.id})">Открыть приём</button>
        `;
        return item;
    }

    window.openAppointment = function (id) {
        fetch(`/api/appointments/${id}`)
            .then(response => response.json())
            .then(appt => {
                currentAppointmentId = appt.id;
                document.getElementById('reason').value = appt.reason || '';
                document.getElementById('diagnosis').value = appt.diagnosis || '';
                document.getElementById('recommendations').value = appt.recommendations || '';
                modal.style.display = 'block';
            })
            .catch(error => console.error('Ошибка при загрузке данных приёма:', error));
    };

    saveChangesButton.addEventListener('click', () => {
        const updatedData = {
            id: currentAppointmentId,
            reason: document.getElementById('reason').value,
            diagnosis: document.getElementById('diagnosis').value,
            recommendations: document.getElementById('recommendations').value,
        };

        fetch(`/api/appointments/${currentAppointmentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
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

    doctorId = await fetchDoctorId();
    if (doctorId) {
        loadAppointments();
    }

    if (closeButton) {
        closeButton.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    } else {
        console.error('Element with ID "close-modal" not found.');
    }

    if (closeAppointmentButton) {
        closeAppointmentButton.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    } else {
        console.error('Element with ID "close-appointment-modal" not found.');
    }

    // Дополнительно: Закрытие окна при клике вне модального содержимого
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
});
