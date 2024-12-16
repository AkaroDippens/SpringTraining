document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem('authToken');

    if (!token) {
        console.log("Token not found. Redirecting to login...");
        window.location.href = '/login';
        return;
    }

    // Получение профиля пользователя и загрузка записей
    fetch('/api/profile', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                console.log("Invalid token or user not found.");
                window.location.href = '/login';
                return;
            }
            return response.json();
        })
        .then(profileData => {
            // Обновляем страницу с полученными данными профиля
            const userId = profileData.user.id; // ID текущего пользователя
            document.getElementById('fullName').textContent = profileData.user.fullName;
            document.getElementById('birthDate').textContent = profileData.user.birthDate;
            document.getElementById('mhiPolicy').textContent = profileData.user.mhiPolicy;

            // Загружаем записи пользователя
            loadAppointments(userId);
        })
        .catch(error => {
            console.error('Error fetching profile:', error);
            alert('An error occurred while fetching your profile.');
        });
});

// Функция для загрузки записей пользователя
function loadAppointments(userId) {
    fetch(`/api/records/byuser/${userId}`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user records');
            }
            return response.json();
        })
        .then(records => {
            const now = new Date();
            const nowUTC = new Date(Date.UTC(
                now.getUTCFullYear(),
                now.getUTCMonth(),
                now.getUTCDate(),
                now.getUTCHours(),
                now.getUTCMinutes(),
                now.getUTCSeconds()
            ));
            console.log("Current time in UTC: ", nowUTC);

            const adjustedNow = new Date(nowUTC.getTime() + 3 * 60 * 60 * 1000);
            console.log("Adjusted current time (GMT+3): ", adjustedNow);

            const upcomingRecords = records.filter(record => {
                const appointmentDate = new Date(record.appointmentDate);
                return appointmentDate > adjustedNow;
            });

            const pastRecords = records.filter(record => {
                const appointmentDate = new Date(record.appointmentDate);
                return appointmentDate <= adjustedNow;
            });

            const list = document.getElementById('appointments-list');
            list.innerHTML = '';

            if (upcomingRecords.length > 0) {
                const upcomingSection = document.createElement('div');
                upcomingSection.innerHTML = `
                    <h3 style="text-align: center;">Предстоящие</h3>
                `;
                upcomingRecords.forEach(record => {
                    const item = createAppointmentItem(record, true);
                    upcomingSection.appendChild(item);
                });
                list.appendChild(upcomingSection);
            }

            if (pastRecords.length > 0) {
                const pastSection = document.createElement('div');
                pastSection.innerHTML = `
                    <h3 style="text-align: center;">Прошедшие</h3>
                `;
                pastRecords.forEach(record => {
                    const item = createAppointmentItem(record, false);
                    pastSection.appendChild(item);
                });
                list.appendChild(pastSection);
            }
        })
        .catch(error => {
            console.error('Error loading records:', error);
            alert('Failed to load user records.');
        });
}

function createAppointmentItem(record, isUpcoming) {
    const item = document.createElement('div');
    item.className = 'history-item';
    item.innerHTML = `
        <div class="history-date">Дата: ${record.appointmentDate.split('T')[0]} ${record.appointmentDate.split('T')[1].slice(0, 5)}</div>
        <div class="history-doctor">Врач: ${record.idDoctor.fullName}</div>
        <div class="history-specialization">Направление: ${record.idDoctor.idSpecialization.specializationName}</div>
        <div class="history-building">Корпус: ${record.idBuilding.buildingName}</div>
    `;

    if (isUpcoming) {
        const cancelButton = document.createElement('button');
        cancelButton.className = 'btn';
        cancelButton.textContent = 'Отменить';
        cancelButton.onclick = () => deleteRecord(record.id);
        item.appendChild(cancelButton);
    } else {
        const reportButton = document.createElement('button');
        reportButton.className = 'btn';
        reportButton.textContent = 'Посмотреть отчёт';
        reportButton.onclick = () => viewAppointmentReport(record.id);
        item.appendChild(reportButton);
    }

    return item;
}

function deleteRecord(recordId) {
    fetch(`/api/records/${recordId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
    })
        .then(() => {
            alert('Запись отменена');
            location.reload(); // Обновляем страницу для повторной загрузки записей
        })
        .catch(error => {
            console.error('Error deleting record:', error);
            alert('Failed to cancel the record.');
        });
}

function viewAppointmentReport(recordId) {
    fetch(`/api/appointments/record/${recordId}`)
        .then(response => {
            if (!response.ok) throw new Error('Appointment not found');
            return response.json();
        })
        .then(appointment => {
            document.getElementById("diagnosis").textContent = appointment.diagnosis || 'Нет данных';
            document.getElementById("recommendations").textContent = appointment.recommendations || 'Нет рекомендаций';
            document.getElementById("appointment-modal").style.display = "block";
        })
        .catch(error => {
            console.error('Error fetching appointment:', error);
            alert('Failed to load appointment details.');
        });
}

document.getElementById("close-appointment-modal").onclick = function () {
    document.getElementById("appointment-modal").style.display = "none";
};

document.getElementById("close-modal").onclick = function () {
    document.getElementById("appointment-modal").style.display = "none";
};

