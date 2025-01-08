require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /
    
    state: NewState
        script:
            # Отправляем запрос на внешний API для поиска вакансий
            $temp.response = $http.post(
                "http://185.242.118.144:8000/find_jobs", 
                {
                    body: {
                        salary: $session.salary,
                        text: $session.profession
                    },
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            );
    
        if: $temp.response.isOk && $temp.response.data.length > 0
            script:
                $temp.vacancyMessages = "";
                for (let vacancy of $temp.response.data) {
                    $temp.vacancyMessages += `---\nПрофессия: ${vacancy.position}\nКомпания: ${vacancy.company}\nГород: ${vacancy.location}\nЗарплата: от ${vacancy.from_salary} до ${vacancy.to_salary} ${vacancy.currency}\n`;
                }
            a: |
                Вот несколько вакансий для тебя:
                {{$temp.vacancyMessages}}
        else:
            # Если вакансии не найдены или произошла ошибка
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
