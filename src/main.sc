require: slotfilling/slotFilling.sc
module = sys.zb-common
theme: /

# Начало диалога
state: Start
    q!: $regex</start>
    a: Привет! Я помогу тебе найти работу. В каком городе ты ищешь вакансии?
    intent: /sys/aimylogic/ru/hello || toState = "/Hello"
    event: noMatch || toState = "./"

# Приветствие
state: Hello
    intent!: /привет
    a: Привет! Как я могу помочь?
    intent: /Поиск работы || toState = "/Поиск работы"
    event: noMatch || toState = "./"

# Прощание
state: Bye
    intent!: /пока
    a: Пока! Удачи в поиске работы!

# Обработка неизвестных запросов
state: NoMatch
    event!: noMatch
    a: Я не понял. Вы сказали: {{$request.query}}

# Поиск работы
state: Поиск работы
    InputText: 
        prompt = Какая профессия вам интересна?
        varName = profession
        htmlEnabled = false
        actions = 
        then = /Какой город

# Сбор города
state: Какой город
    InputText: 
        prompt = В каком городе?
        varName = city
        htmlEnabled = false
        then = /Зарплата
        actions = 

# Сбор желаемой зарплаты
state: Зарплата
    InputNumber: 
        prompt = Желаемая зарплата?
        varName = salary
        htmlEnabled = false
        failureMessage = ["Пожалуйста, введите число."]
        failureMessageHtml = ["Пожалуйста, введите число."]
        then = /NewState
        minValue = 5000
        maxValue = 10000000
        actions = 

# Поиск вакансий
state: NewState
    HttpRequest: 
        url = http://185.242.118.144:8000/find_jobs
        method = POST
        body = {
                "salary": {{$session.salary}},
                "text": "{{$session.profession}}"
            }
        okState = /Найденные вакансии
        timeout = 0
        headers = []
        vars = [
            {"name":"position","value":"$httpResponse.position"},
            {"name":"company","value":"$httpResponse.company"},
            {"name":"location","value":"$httpResponse.location"},
            {"name":"from_salary","value":"$httpResponse.from_salary"},
            {"name":"to_salary","value":"$httpResponse.to_salary"},
            {"name":"currency","value":"$httpResponse.currency"}
        ]

# Вывод вакансий
state: Найденные вакансии
    event: noMatch || toState = "./"
    a: Получены данные от API: {{$httpResponse}}
    a: Найденные вакансии:
        Профессия: "{{$session.position}}"
        Компания: {{$session.company}}
        Город: {{$session.location}}
        Зарплата: {{$session.from_salary}} - {{$session.to_salary}} {{$session.currency}} 
        || htmlEnabled = true, html = "Найденные вакансии:<br>Профессия: {{$session.position}}<br>Компания: {{$session.company}}<br>Город: {{$session.location}}<br>Зарплата: {{$session.from_salary}} - {{$session.to_salary}} {{$session.currency}}"

# Завершение диалога
state: End
    q!: $regex</end>
    a: Надеюсь, я смог помочь! Удачи в поиске работы. Если нужно, возвращайся.

# Обработка случаев, когда запрос не распознан
state: NoMatch
    event!: noMatch
    a: Извини, я не понял твой запрос. Можешь переформулировать?
