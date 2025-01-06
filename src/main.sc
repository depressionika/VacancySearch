require: slotfilling/slotFilling.sc
module = sys.zb-common
theme: /

# Начало диалога
    state: Start
        q!: $regex</start>
        a: Привет! Я помогу тебе найти работу. В каком городе ты ищешь вакансии?
        event!: GetCity
    
# Сбор города
    state: GetCity
        intent!: Location
        a: Отлично! Ты ищешь работу в {{city}}. А на какую должность ты ищешь работу?
        event!: GetProfession
        context: cityContext  # Сохраняем город в контексте

    
# Сбор профессии
    state: GetProfession
        intent!: Profession
        a: Хорошо! А на какую должность ты ищешь работу?
        event!: SearchVacancies
        context: professionContext  # Сохраняем профессию в контексте
    
    
# Поиск вакансий
    state: SearchVacancies
        event!: jobSearch
        a: Ищу вакансии для {{professionContext}} в {{cityContext}}, подожди немного...
        # Здесь будет запрос к API для поиска вакансий
    
    # Пример ответа с результатами поиска
        a: Вот несколько вакансий для тебя:
            - Вакансия: Программист
              Компания: IT Solutions
              Город: {{cityContext}}
              Зарплата: от 100,000 до 120,000 рублей
              Подробнее: [ссылка на вакансию]
    
            - Вакансия: Менеджер по продажам
              Компания: ООО Прогресс
              Город: Санкт-Петербург
              Зарплата: от 80,000 до 100,000 рублей
              Подробнее: [ссылка на вакансию]
    
# Пример окончания диалога
    state: End
        q!: $regex</end>
        a: Надеюсь, я смог помочь! Удачи в поиске работы! Если нужно, возвращайся.
    
# Обработка случаев, когда запрос не распознан
    state: NoMatch
        event!: noMatch
        a: Извини, я не понял твой запрос. Можешь переформулировать?
