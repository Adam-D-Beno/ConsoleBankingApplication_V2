В задании вам предстоит разработать консольное банковское приложение на Java с использованием 
Spring Framework, предоставляющее базовые функции для управления пользователями и их банковскими 
счетами. 
Данное приложение адаптировано с работой БД, при адаптиции использован hibernate и postgre.
Как работает программа:
Приложение запускается и ожидает ввода команд от пользователя через консоль. Пользователь может 
выполнять различные операции, такие как создание нового пользователя, создание счета, пополнение и 
снятие средств, перевод между счетами и закрытие счета. Все операции логируются в консоль.
Входные типы операций, доступные для обработки через консоль:
 1. USER_CREATE - Создание нового пользователя.
 2. SHOW_ALL_USERS - Отображение списка всех пользователей.
 3. ACCOUNT_CREATE - Создание нового счета для пользователя.
 4. ACCOUNT_CLOSE - Закрытие счета.
 5. ACCOUNT_DEPOSIT - Пополнение счета.
 6. ACCOUNT_TRANSFER - Перевод средств между счетами.
 7. ACCOUNT_WITHDRAW - Снятие средств со счета.
 
 Описание операций и входные параметры:
- USER_CREATE : Запрашивает у пользователя логин. Проверяет, что пользователя с таким логином еще нет в 
системе. Создает нового пользователя и один счет с начальным балансом. Система должна назначить 
id пользователю.
- SHOW_ALL_USERS : Не требует входных данных. Выводит список всех пользователей и данные об их 
аккаунтах.
- ACCOUNT_CREATE : Запрашивает ID пользователя. Создает новый счет для указанного пользователя. Счет 
создается с дефолтным балансом из настроек. Назначается уникальный id аккаунту.
- ACCOUNT_CLOSE : Запрашивает ID счета. Закрывает указанный счет, переводя остаток средств на первый 
счет пользователя. Если у пользователя всего один счет, то закрывать его нельзя.
- ACCOUNT_DEPOSIT : Запрашивает ID счета и сумму. Пополняет счет на указанную сумму.
- ACCOUNT_TRANSFER : Запрашивает ID счета отправителя, ID счета получателя и сумму. Переводит средства 
между счетами. Перевод между счетами разных пользвателей облагается комиссией.
- ACCOUNT_WITHDRAW : Запрашивает ID счета и сумму. Снимает указанную сумму со счета.
- 
 Дефолтные настройки из application.properties:
- account.default-amount: Начальный баланс каждого нового счета.
- account.transfer-commission: Комиссия за перевод другому пользователю в процентах. Между 
собственными счетами комиссия не взимается.
