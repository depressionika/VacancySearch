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
        intent: /Поиск работы || toState = "/запрос города"
        event: noMatch || toState = "./"
    
    state: запрос города
        a: В каком городе вы хотите найти работу?
        intent: /Запрос о работе || toState = "/Определение города"
        event: noMatch || toState = "./"
    
    state: Определение города
        intent!: /Запрос о работе
        a: ваш город {{$parseTree._City}}
                Какую зарплату вы хотите?
        script:
            $session.city = $parseTree._City;
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
        event: noMatch || toState = "/NewState"

    state: NewState
        script:
            $temp.response = $http.post(
            "http://185.242.118.144:8000/find_jobs", 
            {
                body: {
                    text: $session.city,
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