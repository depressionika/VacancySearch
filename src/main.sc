state: Start
  q!: $regex</start>
  a: Привет
  intent: /привет
  toState: /JobSearchIntent
  event: noMatch
  toState: ./

state: JobSearchIntent
  intent: /jobSearch_usersays_ru
  a: Отлично! В каком городе ты ищешь работу?
  toState: /GetCity
  event: noMatch
  toState: ./

state: GetCity
  intent: /jobSearchLocation-yes_usersays_ru
  a: Хорошо! А на какую должность ты ищешь работу?
  toState: /GetProfession
  event: noMatch
  toState: ./

state: GetProfession
  intent: /jobSearchProfession-yes_usersays_ru
  a: Хорошо! Какая зарплата желательна для вас?
  toState: /SearchVacancies
  event: noMatch
  toState: ./

state: SearchVacancies
  event: jobSearch
  a: Ищу вакансии, подожди немного...
  # Дальше запрос к API для поиска вакансий

state: Bye
  intent: /пока
  a: Пока, пока

state: NoMatch
  event: noMatch
  a: Я не понял. Вы сказали: {{$request.query}}

state: Match
  event: match
  a: {{$context.intent.answer}}
