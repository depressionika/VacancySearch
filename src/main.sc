require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Начнём.
        intent: /привет || toState = "/Hello"
        event: noMatch || toState = "./"

    state: Hello
        intent!: /привет
        a: Привет привет
        intent: /Поиск работы || toState = "/запрос профессии"
        event: noMatch || toState = "./"

    state: запрос города
        a: В каком городе вы хотите найти работу?
        buttons:
            "Не указывать" -> /запрос зарплаты
        script:
            $session.profession = "";
        intent: /Запрос о работе || toState = "/Определение города"
        event: noMatch || toState = "./"

    state: Определение города
        intent: /Запрос о работе
        a: ваш город {{$parseTree._City}}
                Какую зарплату вы хотите?
        script:
            $session.city = $parseTree._City;
        buttons:
            "Не указывать" -> /Обновление зп
        intent: /Запрос о зарплате || toState = "/Определение зарплаты"
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

    state: Определение зарплаты
        intent!: /Запрос о зарплате
        a: вы выбрали зарплату {{$parseTree._Salary}}
        script:
            $session.salary = $parseTree._Salary;
        buttons:
            "Найти вакансии" -> /NewState
        event: noMatch || toState = "/Инфо"

    state: NewState
        script:
            $temp.response = $http.post(
            "http://185.242.118.144:8000/find_jobs", 
            {
                body: {
            text: $session.city + " " + $session.profession,
            salary: $session.salary
                },
                headers: {
            "Content-Type": "application/json"
                }
            }
                );
        # Отправляем запрос на внешний API для поиска вакансий
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

    state: запрос профессии
        a: Какая профессия вас интересует?
        buttons:
            "Не указывать" -> /запрос города
        intent: /запрос о профессии || toState = "/определение профессии"
        event: noMatch || toState = "./"

    state: определение профессии
        intent!: /запрос о профессии
        script:
            $session.profession = $parseTree._Profession;
        if: $session.profession == ""
            go!: /запрос профессии
        a: Вы выбрали профессию {{$parseTree._Profession}}
        a: В каком городе вы хотите найти работу?
        buttons:
            "Не указывать" -> /запрос зарплаты
        intent: /Запрос о работе || toState = "/Определение города"
        event: noMatch || toState = "./"

    state: запрос зарплаты
        a: КАКУЮ зарплату вы хотите?
        buttons:
            "Не указывать" -> /Обновление зп
        script:
            $session.city = "";
        intent: /Запрос о зарплате || toState = "/Определение зарплаты"
        event: noMatch || toState = "./"

    state: Обновление зп
        script:
            $session.salary = null;
        if: $session.city == "" && $session.profession == "" && $session.salary == ""
            a: Необходимо указать хотя-бы 1 параметр
            buttons:
                "В начало" -> /запрос профессии
        else: 
            buttons:
                "Найти вакансии" -> /NewState
        event: noMatch || toState = "/Инфо"