document.addEventListener('DOMContentLoaded', () => {
    const refreshButton = document.getElementById('refresh-button');

    const refreshIframes = () => {
        document.querySelectorAll('iframe').forEach(iframe => {
            iframe.src = iframe.src; // Перезагрузка iframe
        });
    };

    refreshButton.addEventListener('click', refreshIframes);
});