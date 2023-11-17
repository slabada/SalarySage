
-- Для создания должностей
INSERT INTO position (name, rate) VALUES
('Программист', 50000),
('Юрист', 25000);

-- Для создания сотрудника с использованием должности
INSERT INTO employee (address, first_name, last_name, position_id) VALUES
('Улица пушкина, д. колотушкина', 'Иван','Программистов',1),
('Улица пушкина, д. колотушкина', 'Василий','Юристов',2);

-- Для создания записи в табели для конкретного сотрудника
INSERT INTO time_sheet (date, hours_worked, is_holiday, is_medical,is_vacation, employee_id_id) VALUES
('2023-11-12', '08:00:00', false, false,false, 1),
('2023-11-12', '06:00:00', false, false,false, 2);

-- Для создания льготы
INSERT INTO benefit (amount, name) VALUES
(1000,'Вычет на ребенка I');

-- Для создания расчетного листа для двух разных пользователей
INSERT INTO pay_sheet (year, month, total_amount,  employee_id_id) VALUES
(2023,11, '666', 1),
(2023,11, '777', 2);

-- Создание доп.расходники
INSERT INTO expenditure (amount, name) VALUES
(150000, 'Аутсорсинг дизайна'),
(150000, 'Аутсорсинг ИИ технологий');

-- Создание проекта
INSERT INTO project (end_date, name, start_date) VALUES
('2023-11-30', 'Финальный проект', '2023-11-01');

-- Добавление в проект сотрудников
INSERT INTO project_employee (project, employee) VALUES
(1, 1);

-- Добавление в проект расходника
INSERT INTO project_expenditure (project, expenditure) VALUES
(1, 1);