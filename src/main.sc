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
        q!: * [в] $City *
        script:
            # Проверим, что город указан
            if: $parseTree._City.name
                $session.City = $parseTree._City.name  # Сохраняем город в сессии
            else:
                a: Пожалуйста, укажите город для поиска.
                go!: /Какой город
        a: В каком городе вы ищете работу?
        go!: /проверка_города
    
    state: проверка_города
        q!: * [в] $session.City *  # Используем город из сессии
        script:
            # Формируем запрос к VK API с использованием города из сессии
            $temp.response = $http.get("https://api.vk.com/method/database.getCities", {
                params: {
                    country_id: 1,
                    q: $session.City,  # Запрашиваем город, сохраненный в сессии
                    access_token: "c3ef704dc3ef704dc3ef704d11c0c84230cc3efc3ef704da4914449d51cf41c57b92eb3",
                    v: "5.131"
                }
            });
    
        if: $temp.response.isOk
            if: $temp.response.data.response.items.length > 0
                script:
                    # Если город найден
                    $temp.cityList = $temp.response.data.response.items.map(function(item) {
                        return item.title;
                    }).join(", ");
                a: Город найден! Продолжаем. Вот что нашлось: {{ $temp.cityList }}
                go!: /Зарплата
            else:
                a: Город "{{ $session.City }}" не найден. Попробуйте снова.
                go!: /проверка_города
        else:
            a: Не удалось проверить город. Попробуйте позже.

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