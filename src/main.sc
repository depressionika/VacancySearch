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
                        text: $session.profession + " " + $session.city
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
                    $temp.vacancyMessages += "Ссылка: " + $temp.response.data[$temp.index].link + "\n";
                    $temp.index++;
                }
            a: |
                Вот несколько вакансий для тебя:
                {{$temp.vacancyMessages}}
        else:
            # Если вакансии не найдены или произошла ошибка
            a: Не удалось найти вакансий по вашим параметрам. Попробуй ещё раз.

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
        a: В каком городе вы ищете работу?
        InputText:
            varName = city
            prompt = Пожалуйста, укажите город.
            then = /проверка_города

    state: проверка_города
        HttpRequest:
            url = https://api.vk.com/method/database.getCities
            method = GET
            param:
                name = country_id
                value = 1
            param:
                name = q
                value = $session.city
            param:
                name = access_token
                value = c3ef704dc3ef704dc3ef704d11c0c84230cc3efc3ef704da4914449d51cf41c57b92eb3
            param:
                name = v
                value = 5.131
            okState = /город_найден
            errorState = /ошибка_города
    
    state: город_найден
        script:
            $context.output = "Город найден! Продолжаем.";
        go!: /Зарплата
    
    state: ошибка_города
        script:
            $context.output = "Такого города нет. Попробуйте снова.";
        go!: /проверка_города


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