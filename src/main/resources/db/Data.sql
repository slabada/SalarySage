
-- Для создания должностей
INSERT INTO position (name, rate) VALUES
('Программист', 50000),
('Юрист', 25000);

-- Для создания сотрудника с использованием должности
INSERT INTO employee (address, first_name, last_name, position_id) VALUES
('Улица пушкина, д. колотушкина', 'Иван','Программистов',1),
('Улица пушкина, д. колотушкина', 'Василий','Юристов',2);

-- Для создания записи в табели для конкретного сотрудника
INSERT INTO time_sheet (date, hours_worked, is_holiday, is_medical, employee_id_id) VALUES
('2023-11-12', '08:00:00', false, true, 1),
('2023-11-12', '06:00:00', false, false, 2);

-- Для создания льготы
INSERT INTO benefit (amount, name) VALUES
(1000,'Вычет на ребенка I');

-- Для создания расчетного листа для двух разных пользователей
INSERT INTO pay_sheet (year, month, total_amount,  employee_id_id) VALUES
(2023,11, '666', 1),
(2023,11, '777', 2);