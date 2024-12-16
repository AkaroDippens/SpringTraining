document.getElementById("registrationForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    // Сброс ошибок
    document.querySelectorAll(".error-message").forEach(el => el.textContent = "");

    // Собираем данные из формы
    const formData = {
        fullName: document.getElementById("fullName").value,
        mhiPolicy: document.getElementById("mhiPolicy").value,
        birthDate: document.getElementById("birthDate").value,
        password: document.getElementById("password").value
    };

    try {
        const response = await fetch("/api/users/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        });

        // Если ответ не успешный (например, 400 или 500)
        if (!response.ok) {
            const errors = await response.json();

            // Отображаем ошибки
            for (const field in errors) {
                const errorElement = document.getElementById(`error-${field}`);
                if (errorElement) {
                    errorElement.textContent = errors[field];
                }
            }
            return;
        }

        // Успешный ответ
        alert("Пользователь успешно зарегистрирован!");
        document.getElementById("registrationForm").reset();
        window.location.href = `/login`;
    } catch (error) {
        console.error("Ошибка при отправке данных:", error);
        alert("Произошла ошибка при отправке данных. Попробуйте еще раз.");
    }
});