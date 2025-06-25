document.addEventListener('DOMContentLoaded', () => {
    // DOM элементы
    const logsTableBody = document.getElementById('logs-table-body');
    const paginationContainer = document.getElementById('pagination');
    const clearLogsButton = document.getElementById('clear-logs-btn');
    const exportLogsButton = document.getElementById('export-logs-btn');
    const importLogsButton = document.getElementById('import-logs-btn');
    const importLogsInput = document.getElementById('import-logs-input');

    // Константы и состояние
    const itemsPerPage = 10;
    let currentPage = 1;
    let logs = [];
    const authToken = localStorage.getItem('authToken');

    // Рендер таблицы логов
    const renderLogsTable = (paginatedLogs) => {
        logsTableBody.innerHTML = '';

        paginatedLogs.forEach(log => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${log.timestamp}</td>
                <td class="method-${log.method}">${log.method}</td>
                <td>${log.url}</td>
                <td class="status-${log.status}">${log.status}</td>
                <td>${log.userAgent}</td>
                <td>${log.ipAddress}</td>
            `;
            logsTableBody.appendChild(row);
        });
    };

    // Рендер пагинации
    const renderPagination = (totalItems) => {
        paginationContainer.innerHTML = '';
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        const maxPagesToShow = 5;

        // Кнопка "Предыдущая"
        const prevButton = document.createElement('button');
        prevButton.textContent = 'Предыдущая';
        prevButton.className = 'pagination-btn';
        prevButton.disabled = currentPage === 1;
        prevButton.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderPagination(totalItems);
                updateTable();
            }
        });
        paginationContainer.appendChild(prevButton);

        // Нумерация страниц
        let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);
        startPage = Math.max(1, endPage - maxPagesToShow + 1);

        for (let i = startPage; i <= endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i;
            pageButton.className = `pagination-btn ${i === currentPage ? 'active' : ''}`;
            pageButton.addEventListener('click', () => {
                currentPage = i;
                renderPagination(totalItems);
                updateTable();
            });
            paginationContainer.appendChild(pageButton);
        }

        // Кнопка "Следующая"
        const nextButton = document.createElement('button');
        nextButton.textContent = 'Следующая';
        nextButton.className = 'pagination-btn';
        nextButton.disabled = currentPage === totalPages;
        nextButton.addEventListener('click', () => {
            if (currentPage < totalPages) {
                currentPage++;
                renderPagination(totalItems);
                updateTable();
            }
        });
        paginationContainer.appendChild(nextButton);
    };

    // Обновление таблицы
    const updateTable = () => {
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        renderLogsTable(logs.slice(startIndex, endIndex));
    };

    // Загрузка логов с сервера
    const fetchLogs = () => {
        fetch('/api/logs', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.json())
            .then(fetchedLogs => {
                logs = fetchedLogs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
                renderPagination(logs.length);
                updateTable();
            })
            .catch(error => console.error('Ошибка при получении логов:', error));
    };

    // Обработчики событий
    clearLogsButton.addEventListener('click', () => {
        fetch('/api/logs', {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'DELETE'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchLogs();
            })
            .catch(error => console.error('Ошибка при очистке логов:', error));
    });

    exportLogsButton.addEventListener('click', () => {
        fetch('/api/logs/export', { headers: { 'Authorization': `Bearer ${authToken}` } })
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'logs.csv';
                document.body.appendChild(a);
                a.click();
                a.remove();
            })
            .catch(error => console.error('Ошибка при экспорте логов:', error));
    });

    importLogsButton.addEventListener('click', () => importLogsInput.click());

    importLogsInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        fetch('/api/logs/import', {
            headers: { 'Authorization': `Bearer ${authToken}` },
            method: 'POST',
            body: formData
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchLogs();
            })
            .catch(error => console.error('Ошибка при импорте логов:', error));
    });

    // Инициализация
    fetchLogs();
});