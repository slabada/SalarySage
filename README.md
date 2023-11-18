Проект представляет собой комплексную систему управления персоналом и финансами, предоставляя организации набор инструментов для эффективного учета, администрирования и анализа данных. Проект включает в себя девять ключевых модулей, каждый из которых направлен на определенные аспекты управления бизнес-процессами.

  1) Модуль создания должности:
  Позволяет создавать и управлять структурой должностей в организации.
  Определяет оклад для каждой должности
  
  2) Модуль создания налога:
  Предоставляет возможность определения и управления налоговыми ставками.
  Автоматизирует расчет налогов для обеспечения точности финансовых данных.
  
  3) Модуль создания льготы:
  Позволяет внедрять и учет льгот для сотрудников в соответствии с действующим законодательством.
  Обеспечивает прозрачность и справедливость в предоставлении льгот.
  
  4) Модуль создания сотрудника:
  Упрощает процесс регистрации новых сотрудников в системе.
  Содержит базу данных сотрудников с ключевой информацией для эффективного управления персоналом.
  
  5) Модуль создания табеля отработанного времени:
  Обеспечивает возможность учета отработанного времени сотрудников.
  Генерирует отчеты о рабочем времени для управления и анализа.
  
  6) Модуль создания расчетного листа:
  Автоматизирует расчет заработной платы, учитывая налоги и льготы.
  Генерирует расчетные листы для каждого сотрудника.
  
  7) Модуль создания проекта:
  Позволяет создавать и управлять проектами в организации.
  Обеспечивает контроль над бюджетом, сроками и ресурсами проекта.
  
  8) Модуль создания дополнительных расходов для проекта:
  Позволяет учитывать и анализировать дополнительные расходы в рамках проекта.
  Обеспечивает прозрачность финансов проекта.
  
  9) Модуль отвечающий за MVC:
  Обеспечивает визуализацию данных по табелям, расчетным листам и информации о проектах.
  Предоставляет пользовательский интерфейс для удобного доступа и анализа данных.
  Также предоставляет возможность скачать данные с формате Word и Excel.

API: Должность

    POST:	/position/create -	Создает новую должность используя данные, переданные в теле HTTP-запроса.
    
    GET:	/position/{id} -	Получает информацию о должности по указанному идентификатору (id).
    
    PUT:	/position/{id} -	Обновляет информацию о должности по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса.
    
    DELETE:	/position/{id}	Удаляет должность с указанным идентификатором (id).

API: Налог

    POST:	/rate/create -	Создает новую налог используя данные, переданные в теле HTTP-запроса.
    
    GET:	/rate/{id} -	Получает информацию о налоге по указанному идентификатору (id).
    
    PUT:	/rate/{id} -	Обновляет информацию о налоге по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса.
    
    DELETE:	/rate/{id} -	Удаляет налог с указанным идентификатором (id).

API: Налоговый вычет

    POST:	/benefit/create	- Создает новую сущность Benefit, используя данные, переданные в теле HTTP-запроса.
    
    GET:	/benefit/{id}	- Получает информацию о сущности Benefit по указанному идентификатору (id).
    
    PUT:	/benefit/{id}	- Обновляет информацию о сущности Benefit по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса.
    
    DELETE:	/benefit/{id} -	Удаляет сущность Benefit с указанным идентификатором (id).

API: Сотрудник

    POST:	/employee/create -	Создает нового сотрудника, используя данные, переданные в теле HTTP-запроса.
    
    GET:	/employee/{id} -	Получает информацию о сотруднике по указанному идентификатору (id).
    
    PUT:	/employee/{id} -	Обновляет информацию о сотруднике по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса.
    
    DELETE:	/employee/{id} -	Удаляет сотрудника с указанным идентификатором (id).
    
    GET:	/employee/search -	Поиск сотрудников с заданными параметрами, используя параметры запроса "e" для фильтрации и "from" и "size" для пагинации. Возвращает список сотрудников, удовлетворяющих заданным критериям.

API: Табель учета рабочего времени

    POST:	/timesheet/create -	Создает новую запись табеля учета рабочего времени, используя данные, переданные в теле HTTP-запроса.
    
    GET:	/timesheet/{id} -	Получает информацию о записи табеля учета рабочего времени по указанному идентификатору (id).
    
    PUT:	/timesheet/{id} -	Обновляет информацию о записи табеля учета рабочего времени по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса.
    
    DELETE:	/timesheet/{id} -	Удаляет запись табеля учета рабочего времени с указанным идентификатором (id).
    
    GET:	/timesheet/employee/{id} -	Получает список записей табеля учета рабочего времени для определенного сотрудника, используя его идентификатор (id), год и месяц. Возвращает список объектов.

API: Расчетный лист

    POST:	/paysheet/create -	Создает новый расчетный лист используя данные, переданные в теле HTTP-запроса.
    
    GET:	/paysheet/{id} -	Получает информацию о расчетном листе по указанному идентификатору (id).
    
    PUT:	/paysheet/{id} -	Обновляет информацию о расчетном листе по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса.
    
    DELETE:	/paysheet/{id} -	Удаляет расчетный лист с указанным идентификатором (id).
    
    GET:	/paysheet/employee/{id} -	Получает список расчетных листов, связанных с определенным сотрудником, используя его идентификатор (id). Возвращает список PaySheetModel объектов.

API: Проект

    POST: /paysheet/create - Создает новый расчетный лист, используя данные, переданные в теле HTTP-запроса. Возвращает созданный объект PaySheetModel.
    
    GET: /paysheet/{id} - Получает информацию о расчетном листе по указанному идентификатору (id). Возвращает объект типа Optional<PaySheetModel>.
    
    PUT: /paysheet/{id} - Обновляет информацию о расчетном листе по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса. Возвращает обновленный объект PaySheetModel.
    
    DELETE: /paysheet/{id} - Удаляет расчетный лист с указанным идентификатором (id).
    
    GET: /paysheet/employee/{id} - Получает список расчетных листов, связанных с определенным сотрудником, используя его идентификатор (id). Возвращает список объектов PaySheetModel.

API: Доп.Расходы

    POST: /paysheet/create - Создает новый расчетный лист, используя данные, переданные в теле HTTP-запроса. Возвращает созданный объект PaySheetModel.
    
    GET: /paysheet/{id} - Получает информацию о расчетном листе по указанному идентификатору (id). Возвращает объект типа Optional<PaySheetModel>.
    
    PUT: /paysheet/{id} - Обновляет информацию о расчетном листе по указанному идентификатору (id) с использованием данных, переданных в теле HTTP-запроса. Возвращает обновленный объект PaySheetModel.
    
    DELETE: /paysheet/{id} - Удаляет расчетный лист с указанным идентификатором (id).

MVC: Отчет и Табель

    GET: /report/paysheet/{id} - Получает отчет о расчетном листе для указанного сотрудника.
    
    GET: /report/paysheet/{id}/downloads/word - Загружает в формате Word отчет о расчетном листе для указанного сотрудника.
    
    GET: /report/paysheet/{id}/downloads/excel - Загружает в формате Excel отчет о расчетном листе для указанного сотрудника.
    
    GET: /report/timesheet/{id} - Получает отчет о табеле времени для указанного сотрудника, с возможностью фильтрации по году и месяцу.
    
    GET: /report/timesheet/{id}/downloads/word - Загружает в формате Word отчет о табеле времени для указанного сотрудника.
    
    GET: /report/timesheet/{id}/downloads/excel - Загружает в формате Excel отчет о табеле времени для указанного сотрудника.

MVC: Проект

    GET /analytics/{id} - Получает отчет об аналитике для указанного проекта.
    
    GET /analytics/{id}/downloads/word - Загружает в формате Word отчет об анализе проекта.
    
    GET /analytics/{id}/downloads/excel - Загружает в формате Excel отчет об анализе проекта.

Связи таблиц:
![image](https://github.com/slabada/SalarySage/assets/82341789/7dec7be1-913d-417a-b547-6df8e61fd8a8)

Страница табеля сотрудника:
![image](https://github.com/slabada/SalarySage/assets/82341789/d51fef90-d1cd-4c86-8a02-57ed2768a7ba)

Страница финансового отчета сотрудника:
![image](https://github.com/slabada/SalarySage/assets/82341789/c1e4b471-f9f9-474c-9ef0-b7797f553aa5)

Страница проекта
![image](https://github.com/slabada/SalarySage/assets/82341789/20e4570c-08aa-49bd-994b-cd6f80a3850a)

Пример скаченного word файла с страницы проекта
![image](https://github.com/slabada/SalarySage/assets/82341789/addd0b4b-6095-4535-b04c-2a47094f9cc3)

Пример скаченного excel файла с страницы проекта
![image](https://github.com/slabada/SalarySage/assets/82341789/7bd7ae83-40e9-4ea0-a325-58a590e00dc3)
![image](https://github.com/slabada/SalarySage/assets/82341789/889f20f6-76c2-4163-8244-40004e40ef5c)
![image](https://github.com/slabada/SalarySage/assets/82341789/a1323179-dfb2-421e-9ac5-24ee960b48bb)