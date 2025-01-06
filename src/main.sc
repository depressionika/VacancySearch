require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /
    
    state: NewState
        HttpRequest: 
            url = http://185.242.118.144:8000/find_jobs
            method = POST
            dataType = json
            body = {
                    "salary": {{$session.salary}},
                    "text": "{{$session.profession}}"
                }
            okState = /Найденные вакансии
            timeout = 0
            headers = []
            vars = [{"name":"position","value":"$httpResponse.position"},{"name":"company","value":"$httpResponse.company"},{"name":"location","value":"$httpResponse.location"},{"name":"from_salary","value":"$httpResponse.from_salary"},{"name":"to_salary","value":"$httpResponse.to_salary"},{"name":"currency","value":"$httpResponse.currency"}]

    state: Найденные вакансии
    event: noMatch || toState = "./"
        a: |
            Найденные вакансии:
            Профессия: {{$session.position}}
            Компания: {{$session.company}}
            Город: {{$session.location}}
            Зарплата: от {{$session.from_salary}} до {{$session.to_salary}} {{$session.currency}}
    
        # Теперь используем правильное оформление для HTML
        fulfillmentMessage:
            - text:
                text:
                    - "Найденные вакансии:<br><b>Профессия:</b> {{$session.position}}<br><b>Компания:</b> {{$session.company}}<br><b>Город:</b> {{$session.location}}<br><b>Зарплата:</b> от {{$session.from_salary}} до {{$session.to_salary}} {{$session.currency}}"
                
    state: вывод
        a: {{$session.profession}}
            {{$session.city}}
            {{$session.salary}} || htmlEnabled = true, html = "{{$session.profession}}<br>{{$session.city}}<br>{{$session.salary}}"

    state: Start
        q!: $regex</start>
        a: Начнём.
        intent: /sys/aimylogic/ru/hello || toState = "/Hello"
        event: noMatch || toState = "./"

    state: Hello
        intent!: /привет
        a: Привет привет
        intent: /Поиск работы || toState = "/Поиск работы"
        event: noMatch || toState = "./"

    state: Bye
        intent!: /пока
        a: Пока пока

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}

    state: Поиск работы
        InputText: 
            prompt = Какая профессия вам интересна?
            varName = profession
            html = 
            htmlEnabled = false
            actions = 
            then = /Какой город
        event: noMatch || toState = "./"

    state: Какой город
        InputText: 
            prompt = В каком городе?
            varName = city
            html = 
            htmlEnabled = false
            then = /Зарплата
            actions = 

    state: Зарплата
        InputNumber: 
            prompt = Желаемая зарплата?
            varName = salary
            html = 
            htmlEnabled = false
            failureMessage = [""]
            failureMessageHtml = [""]
            then = /NewState
            minValue = 5000
            maxValue = 10000000
            actions = 