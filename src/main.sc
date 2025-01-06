require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

# Начало диалога
    state: Start
        q!: $regex</start>
        a: Начнём.

    state: Hello
        intent!: /привет
        a: Привет! 


# Интент: пользователь хочет найти работу
    state: JobSearchIntent
        intent!: jobSearch_usersays_ru
        a: Отлично! В каком городе ты ищешь работу?


# Сбор города
    state: GetCity
        intent!: jobSearchLocation-yes_usersays_ru
        a: Хорошо! А на какую должность ты ищешь работу?

    state: GetCityNo
        intent!: jobSearchLocation-no_usersays_ru
        a: Окей, ты не хочешь указывать город. А на какую должность ты ищешь работу?
    
    
# Сбор профессии
    state: GetProfession
        intent!: jobSearchProfession-yes_usersays_ru
        a: Хорошо! А на какую должность ты ищешь работу?

    state: GetProfessionNo
        intent!: jobSearchProfession-no_usersays_ru
        a: Окей, я попробую найти вакансии без указания профессии.
    
    
    
# Поиск вакансий
    state: SearchVacancies
        event!: jobSearch
        a: Ищу вакансии, подожди немного...

    # Тут будет запрос к API HeadHunter или другой системе для поиска вакансий
    # Пример вызова вакансий с учетом города, профессии и зарплаты

# Ответ с результатами поиска
        a: Вот несколько вакансий для тебя:
            - Вакансия: Программист
            Компания: IT Solutions
            Город: Москва
            Зарплата: от 100,000 до 120,000 рублей
            Опыт: 2-3 года
            Подробнее: [ссылка на вакансию]

            - Вакансия: Менеджер по продажам
            Компания: ООО Прогресс
            Город: Санкт-Петербург
            Зарплата: от 80,000 до 100,000 рублей
            Опыт: 1-2 года
            Подробнее: [ссылка на вакансию]
    
    state: Bye
        intent!: /пока
        a: Пока пока

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}