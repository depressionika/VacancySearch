require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /
    
    state: SearchVacancies
        q!: * найти работу * 
        script:
            # Отправляем запрос на внешний API для поиска вакансий
            $vacancies.response = $http.post("http://185.242.118.144:8000/find_jobs", {
                query: {
                    salary: $session.salary,         # Из сессии получаем зарплату
                    text: $session.profession        # Из сессии получаем профессию
                }
            });
            
        if: $vacancies.response.isOk
            # Если запрос успешен, выводим вакансии
            a: |
                Вот несколько вакансий для тебя:
                Профессия: {{$vacancies.response.data.position}}
                Компания: {{$vacancies.response.data.company}}
                Город: {{$vacancies.response.data.location}}
                Зарплата: от {{$vacancies.response.data.from_salary}} до {{$vacancies.response.data.to_salary}} {{$vacancies.response.data.currency}}
        else:
            # Если запрос не успешен, выводим ошибку
            a: Не удалось найти вакансии. Попробуй ещё раз.
            
    state: Найденные вакансии
        event: noMatch || toState = "./"
        a: |
            Найденные вакансии:
            Профессия: {{$session.position}}
            Компания: {{$session.company}}
            Город: {{$session.location}}
            Зарплата: от {{$session.from_salary}} до {{$session.to_salary}} {{$session.currency}}
                
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