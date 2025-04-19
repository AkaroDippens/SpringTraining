// Функция для обновления всех iframe на странице
function refreshIframes() {
// Находим все iframe на странице
const iframes = document.querySelectorAll('iframe');

// Перезагружаем каждый iframe
iframes.forEach(iframe => {
  iframe.src = iframe.src; // Перезагружаем iframe
});
}

// Находим кнопку и добавляем обработчик события
const refreshButton = document.getElementById('refresh-button');
refreshButton.addEventListener('click', refreshIframes);