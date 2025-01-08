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
                        text: $session.profession + " " + $session.location
                    },
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            );
    
        if: $temp.response.isOk && $temp.response.data.length > 0
            script:
                $temp.vacancyMessages = "";
                $temp.index = 0;
                while ($temp.index < $temp.response.data.length) {
                    $temp.vacancyMessages += "---\n";
                    $temp.vacancyMessages += "Профессия: " + $temp.response.data[$temp.index].position + "\n";
                    $temp.vacancyMessages += "Компания: " + $temp.response.data[$temp.index].company + "\n";
                    $temp.vacancyMessages += "Город: " + $temp.response.data[$temp.index].location + "\n";
                    $temp.vacancyMessages += "Зарплата: от " + $temp.response.data[$temp.index].from_salary + 
                        " до " + $temp.response.data[$temp.index].to_salary + 
                        " " + $temp.response.data[$temp.index].currency + "\n";
                    $temp.index++;
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
