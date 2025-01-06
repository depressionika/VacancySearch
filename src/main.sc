require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

# Начало диалога
    state: Start
        q!: $regex</start>
        a: Привет! Я помогу тебе найти работу. В каком городе ты ищешь вакансии?
        slot: city_slot

# Сбор города
    state: GetCity
        intent!: jobSearchLocation-yes_usersays_ru
        a: Отлично! Ты ищешь работу в {{city_slot}}. А на какую должность ты ищешь работу?
        slot: profession_slot

# Пример поиска вакансий
    state: SearchVacancies
        event!: jobSearch
        a: Ищу вакансии для {{profession_slot}} в {{city_slot}}, подожди немного...

# Ответ с результатами поиска
        a: Вот несколько вакансий для тебя:
            - Вакансия: Программист
              Компания: IT Solutions
              Город: {{city_slot}}
              Зарплата: от 100,000 до 120,000 рублей
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