require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /
    
    state: NewState
        script:
            $temp.response = $http.post("http://185.242.118.144:8000/find_jobs", 
            {
                salary: $session.salary,
                text: $session.profession
            }, 
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );

        # Отправляем запрос на внешний API для поиска вакансий
        if: $temp.response.isOk
            # Если запрос успешен, выводим вакансии
            a: |
                Вот несколько вакансий для тебя:
                Профессия: {{$temp.response.data.position}}
                Компания: {{$temp.response.data.company}}
                Город: {{$temp.response.data.location}}
                Зарплата: от {{$temp.response.data.from_salary}} до {{$temp.response.data.to_salary}} {{$temp.response.data.currency}}
        else: 
            # Если запрос не успешен, выводим ошибку
            a: Не удалось найти вакансии. Попробуй ещё раз.

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
