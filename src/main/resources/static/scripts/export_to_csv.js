import { InvalidTokenError, b64DecodeUnicode, base64_url_decode, jwtDecode } from './jwt-decode.js';

document.addEventListener('DOMContentLoaded', () => {
    const exportButton = document.getElementById('export-button');
    if (exportButton) {
        exportButton.addEventListener('click', exportToCSVMedicalCard);
    }
});

function exportToCSVMedicalCard() {
    const token = localStorage.getItem('authToken');

    if (!token) {
        console.log("Token not found. Redirecting to login...");
        window.location.href = '/login';
        return;
    }

    // Декодируем токен для получения userId
    const decodedToken = jwtDecode(token);
    const userId = decodedToken.userId;

    // Получаем данные профиля и записи по userId
    Promise.all([
        fetch('/api/profile', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(response => {
            if (!response.ok) throw new Error("Profile fetch failed");
            return response.json();
        }),
        fetch(`/api/records/byuser/${userId}`, {
            method: 'GET',
        }).then(response => {
            if (!response.ok) throw new Error("Records fetch failed");
            return response.json();
        })
    ])
    .then(([profileData, records]) => {
        // Формируем данные для CSV
        const csvContent = [];

        // Данные профиля
        csvContent.push(['Данные профиля']);
        csvContent.push(['ФИО', 'Дата рождения', 'Номер полиса ОМС']);
        csvContent.push([
            profileData.user.fullName,
            profileData.user.birthDate,
            profileData.user.mhiPolicy
        ]);
        csvContent.push([]); // Пустая строка для разделения

        // История записей
        csvContent.push(['История записей']);
        csvContent.push(['Дата начала', 'Время начала', 'Врач', 'Направление', 'Корпус']);
        records.forEach(record => {
            const appointmentDate = record.appointmentDate.split('T')[0];
            const appointmentTime = record.appointmentDate.split('T')[1].slice(0, 5);
            csvContent.push([
                appointmentDate,
                appointmentTime,
                record.idDoctor.fullName,
                record.idDoctor.idSpecialization.specializationName,
                record.idBuilding.buildingName
            ]);
        });

        // Преобразуем данные в CSV-формат
        const csvString = csvContent.map(row => row.join(",")).join("\n");

        // Генерируем CSV-файл и загружаем его
        const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement("a");
        const url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", "medical_card_data.csv");
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    })
    .catch(error => {
        console.error('Error exporting data:', error);
        alert('An error occurred while exporting your data.');
    });
}
