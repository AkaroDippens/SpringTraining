document.addEventListener('DOMContentLoaded', () => {
    const logsTableBody = document.getElementById('logs-table-body');
    const paginationContainer = document.getElementById('pagination');
    const clearLogsButton = document.getElementById('clear-logs-btn');
    const exportLogsButton = document.getElementById('export-logs-btn');
    const importLogsButton = document.getElementById('import-logs-btn');
    const importLogsInput = document.getElementById('import-logs-input');
    const itemsPerPage = 10;
    let currentPage = 1;
    let logs = []; // Данные логов

    // Функция для вывода данных в таблицу
    const renderLogsTable = (paginatedLogs) => {
        logsTableBody.innerHTML = ''; // Очистить таблицу перед рендером
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

    const renderPagination = (totalItems) => {
        paginationContainer.innerHTML = '';
        const totalPages = Math.ceil(totalItems / itemsPerPage);

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

        // Отображение страниц
        const maxPagesToShow = 5; // Максимальное количество страниц для отображения
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

    const updateTable = () => {
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        const paginatedLogs = logs.slice(startIndex, endIndex);
        renderLogsTable(paginatedLogs);
    };

    // Получаем данные о логах с сервера
    const fetchLogs = () => {
        fetch('/api/logs')
            .then(response => response.json())
            .then(fetchedLogs => {
                // Сортируем логи по полю timestamp в порядке убывания
                logs = fetchedLogs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
                renderPagination(logs.length); // Отображаем пагинацию
                updateTable(); // Отображаем первую страницу
            })
            .catch(error => {
                console.error('Ошибка при получении логов:', error);
            });
    };

    clearLogsButton.addEventListener('click', () => {
        fetch('/api/logs', { method: 'DELETE' })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchLogs(); // Обновляем таблицу после очистки
            })
            .catch(error => {
                console.error('Ошибка при очистке логов:', error);
            });
    });

    exportLogsButton.addEventListener('click', () => {
        fetch('/api/logs/export')
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
            .catch(error => {
                console.error('Ошибка при экспорте логов:', error);
            });
    });

    importLogsButton.addEventListener('click', () => {
        importLogsInput.click();
    });

    importLogsInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (file) {
            const formData = new FormData();
            formData.append('file', file);

            fetch('/api/logs/import', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchLogs(); // Обновляем таблицу после импорта
            })
            .catch(error => {
                console.error('Ошибка при импорте логов:', error);
            });
        }
    });

    fetchLogs();
});