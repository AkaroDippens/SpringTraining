-- Таблица Роли
CREATE TABLE roles (
    id_role SERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL
);

-- Таблица Пользователи
CREATE TABLE users (
    id_user SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    contact_number VARCHAR(20),
    MHI_policy VARCHAR(30) NOT NULL,
    birth_date DATE,
	id_role INT NOT NULL,
    FOREIGN KEY (id_role) REFERENCES roles(id_role)
        ON UPDATE CASCADE
        ON DELETE RESTRICT -- нельзя удалить роль, если она используется
);

-- Таблица Приёмы
CREATE TABLE appointments (
    id_appointment SERIAL PRIMARY KEY,
	reason VARCHAR(250),
    diagnosis VARCHAR(250),
    recommendations VARCHAR(250),
	id_record INT NOT NULL,
	id_recipe INT,
	FOREIGN KEY (id_recipe) REFERENCES recipes(id_recipe),
    FOREIGN KEY (id_record) REFERENCES records(id_record)
        ON UPDATE CASCADE
        ON DELETE CASCADE -- при удалении записи на приём, удаляются все приёмы
);

-- Таблица Медицинская карта
CREATE TABLE medical_records (
    id_medical_record SERIAL PRIMARY KEY,
    user_information TEXT,
	id_user INT NOT NULL,
    id_appointment INT,
    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON UPDATE CASCADE
        ON DELETE RESTRICT, -- нельзя удалить пользователя, если есть медкарта
	FOREIGN KEY (id_appointment) REFERENCES appointments(id_appointment)
        ON UPDATE CASCADE
);

-- Таблица Специализации
CREATE TABLE specializations (
    id_specialization SERIAL PRIMARY KEY,
    specialization_name VARCHAR(250) NOT NULL
);

-- Таблица Корпуса
CREATE TABLE buildings (
    id_building SERIAL PRIMARY KEY,
    building_name VARCHAR(150) NOT NULL,
    address VARCHAR(150),
    contact_number VARCHAR(50)
);

-- Таблица Врачи
CREATE TABLE doctors (
    id_doctor SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    experience DATE,
	id_specialization INT NOT NULL,
    id_building INT NOT NULL,
    FOREIGN KEY (id_specialization) REFERENCES specializations(id_specialization)
        ON UPDATE CASCADE
        ON DELETE RESTRICT, -- нельзя удалить специализацию, если есть врач
    FOREIGN KEY (id_building) REFERENCES buildings(id_building)
        ON UPDATE CASCADE
        ON DELETE RESTRICT -- нельзя удалить корпус, если в нём работают врачи
);

-- Таблица Анализы
CREATE TABLE analyzes (
    id_analyze SERIAL PRIMARY KEY,
    analyze_result TEXT,
    receive_time TIMESTAMP,
	id_doctor INT NOT NULL,
    id_appointment INT NOT NULL,
    FOREIGN KEY (id_doctor) REFERENCES doctors(id_doctor)
        ON UPDATE CASCADE,
    FOREIGN KEY (id_appointment) REFERENCES appointments(id_appointment)
        ON UPDATE CASCADE
        ON DELETE CASCADE -- при удалении приёма, удаляются все анализы
);

-- Таблица Записи
CREATE TABLE records (
    id_record SERIAL PRIMARY KEY,
    appointment_date TIMESTAMP NOT NULL,
	id_user INT NOT NULL,
    id_building INT NOT NULL,
    id_doctor INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON UPDATE CASCADE,
    FOREIGN KEY (id_building) REFERENCES buildings(id_building)
        ON UPDATE CASCADE
        ON DELETE RESTRICT, -- нельзя удалить корпус, если есть записи
    FOREIGN KEY (id_doctor) REFERENCES doctors(id_doctor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT -- нельзя удалить врача, если есть записи
);

-- Таблица Лекарства
CREATE TABLE medicines (
    id_medicine SERIAL PRIMARY KEY,
    medicine_name VARCHAR(150) NOT NULL,
    manufacturer VARCHAR(200) NOT NULL
);

-- Таблица Рецепты
CREATE TABLE recipes (
    id_recipe SERIAL PRIMARY KEY,
    id_doctor INT NOT NULL,
    FOREIGN KEY (id_doctor) REFERENCES doctors(id_doctor)
        ON UPDATE CASCADE
);

-- Промежуточная таблица для Рецептов и Лекарств (многие ко многим)
CREATE TABLE recipes_medicines (
    id_recipe INT NOT NULL,
    usage_method TEXT,
	id_medicine INT NOT NULL,
    PRIMARY KEY (id_recipe, id_medicine),
    FOREIGN KEY (id_recipe) REFERENCES recipes(id_recipe)
        ON UPDATE CASCADE
        ON DELETE CASCADE, -- при удалении рецепта, удаляются связи с лекарствами
    FOREIGN KEY (id_medicine) REFERENCES medicines(id_medicine)
        ON UPDATE CASCADE
);

INSERT INTO roles (role_name) VALUES ('Администратор'), ('Врач'), ('Пациент');
INSERT INTO users (full_name, contact_number, MHI_policy, birth_date, id_role) 
	VALUES ('Иван Иванов', '123456789', 'POLICY123', '1990-01-01', 3);  -- Пациент
INSERT INTO buildings (building_name, address, contact_number) 
	VALUES ('Корпус 1', 'ул. Пушкина, д. 10', '123456789');
INSERT INTO specializations (specialization_name) 
	VALUES ('Терапевт'), ('Хирург');
INSERT INTO doctors (full_name, experience, id_specialization, id_building) 
	VALUES ('Доктор Смит', '2000-01-01', 1, 1);  -- Терапевт
INSERT INTO medicines (medicine_name, manufacturer) 
	VALUES ('Парацетамол', 'Фарм Инк.');
INSERT INTO recipes (id_doctor) VALUES (1);  -- Доктор Смит
INSERT INTO recipes_medicines (id_recipe, usage_method, id_medicine) 
	VALUES (1, '1 таблетка 3 раза в день', 1);  -- Парацетамол


SELECT * FROM roles;
SELECT * FROM users;
SELECT
    u.id_user,
    u.full_name AS "Полное имя",
    u.contact_number AS "Контактный номер",
    u.MHI_policy AS "Полис ОМС",
    r.role_name AS "Роль"
FROM
    users u
JOIN
    roles r ON u.id_role = r.id_role;
	
SELECT * FROM medical_records


CREATE OR REPLACE FUNCTION create_medical_record() 
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO medical_records (id_user, user_information)
    VALUES (NEW.id_user, 'Информация о пользователе');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_create_medical_record
AFTER INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION create_medical_record();


DROP TABLE users;
DROP TABLE roles;