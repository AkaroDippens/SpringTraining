document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const token = localStorage.getItem('authToken');
    const modal = document.getElementById('appointment-modal');

    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Загрузка профиля
    const loadProfile = () => {
        fetch('/api/profile', {
            headers: { 'Authorization': `Bearer ${token}` }
        })
            .then(response => {
                if (!response.ok) throw new Error('Ошибка загрузки');
                return response.json();
            })
            .then(profile => {
                document.getElementById('fullName').textContent = profile.user.fullName;
                document.getElementById('birthDate').textContent = profile.user.birthDate;
                document.getElementById('mhiPolicy').textContent = profile.user.mhiPolicy;
                loadAppointments(profile.user.id);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                window.location.href = '/login';
            });
    };

    // Проверка статуса приема
    const checkAppointmentStatus = async (recordId) => {
        try {
            const response = await fetch(`/api/appointments/record/${recordId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!response.ok) return false;
            const { diagnosis, recommendations } = await response.json();
            return diagnosis && recommendations;
        } catch (error) {
            console.error('Ошибка:', error);
            return false;
        }
    };

    // Загрузка записей
    const loadAppointments = async (userId) => {
        try {
            const response = await fetch(`/api/records/byuser/${userId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!response.ok) throw new Error('Ошибка загрузки');

            const records = await response.json();
            const now = new Date();
            const adjustedNow = new Date(now.getTime() + 3 * 60 * 60 * 1000); // GMT+3

            const upcoming = [];
            const past = [];

            for (const record of records) {
                const isCompleted = await checkAppointmentStatus(record.id);
                const recordDate = new Date(record.appointmentDate);

                if (isCompleted) {
                    past.push(record);
                } else {
                    recordDate > adjustedNow ? upcoming.push(record) : past.push(record);
                }
            }

            renderAppointments(
                upcoming.sort((a, b) => new Date(b.appointmentDate) - new Date(a.appointmentDate)),
                past.sort((a, b) => new Date(b.appointmentDate) - new Date(a.appointmentDate))
            );
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Ошибка загрузки записей');
        }
    };

    // Рендер записей
    const renderAppointments = (upcoming, past) => {
        const list = document.getElementById('appointments-list');
        list.innerHTML = '';

        const createSection = (records, title, isUpcoming) => {
            if (records.length === 0) return;

            const section = document.createElement('div');
            section.innerHTML = `<h3 style="text-align: center;">${title}</h3>`;

            records.forEach(record => {
                const item = document.createElement('div');
                item.className = 'history-item';
                item.innerHTML = `
                    <div class="history-date">
                        Дата: ${record.appointmentDate.split('T')[0]} ${record.appointmentDate.split('T')[1].slice(0, 5)}
                    </div>
                    <div class="history-doctor">Врач: ${record.idDoctor.fullName}</div>
                    <div class="history-specialization">
                        Направление: ${record.idDoctor.idSpecialization.specializationName}
                    </div>
                    <div class="history-building">
                        Корпус: ${record.idBuilding.buildingName}
                    </div>
                `;

                const button = document.createElement('button');
                button.className = 'btn';
                button.textContent = isUpcoming ? 'Отменить' : 'Посмотреть отчёт';
                button.onclick = isUpcoming
                    ? () => cancelAppointment(record.id)
                    : () => viewReport(record.id);

                item.appendChild(button);
                section.appendChild(item);
            });

            list.appendChild(section);
        };

        createSection(upcoming, 'Предстоящие', true);
        createSection(past, 'Прошедшие', false);
    };

    // Отмена записи
    const cancelAppointment = (id) => {
        if (!confirm('Отменить запись?')) return;

        fetch(`/api/records/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        })
            .then(() => location.reload())
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Ошибка отмены');
            });
    };

    // Просмотр отчета
    const viewReport = (recordId) => {
        fetch(`/api/appointments/record/${recordId}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        })
            .then(response => {
                if (!response.ok) throw new Error('Не найдено');
                return response.json();
            })
            .then(data => {
                document.getElementById('diagnosis').textContent = data.diagnosis || 'Нет данных';
                document.getElementById('recommendations').textContent = data.recommendations || 'Нет рекомендаций';
                document.getElementById('reasons').textContent = data.reason || 'Причины отсутствуют';
                modal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Ошибка загрузки отчета');
            });
    };

    // Закрытие модалки
    document.getElementById('close-appointment-modal').addEventListener('click', () => {
        modal.style.display = 'none';
    });

    document.getElementById('close-modal').addEventListener('click', () => {
        modal.style.display = 'none';
    });

    // Инициализация
    loadProfile();
});