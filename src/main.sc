require: requirements.sc

theme: /

    # стартовый стейт, с него начинается общение с пользователем, если через ссылку не было передано
    # дополнительных параметров. 
    # например: https://t.me/bot_name
    state: Start
        # тег "q!" - (от англ. "question" — вопрос ) тег для паттернов
        # Это глобальный тег: переход по нему возможен из любого другого стейта в сценарии.
        # $regex - паттерн для обработки регулярных выражений. Подробнее тут: https://help.just-ai.com/docs/ru/Patterns/advanced_patterns#regexpregexp_i
        q!: $regex</start>
        a: Начнём, а?.
    
    state: MainMenu
        # тег "q!" - (от англ. "question" — вопрос ) тег для паттернов
        # Это глобальный тег: переход по нему возможен из любого другого стейта в сценарии.
        q!: * (настав*/меню) *
        # тег "script" предназначен для написания js кода, который должен отработать в стейте
        script:
            # очищаем/дополнительно очищаем переменные шага и ошибок
            $session.stepNumber = 0;
            $session.ErrorCounter = 0;
        # тег "a" предназначени для вывода сообщения ползователю
        a: Выбери пункт меню
        # тег "inlineButtons" выводит кнопки пользователю внутри сообщения
        # при нажатии кнопки пользоватлем в телеграм, срабатывает сообытие telegramCallbackQuery.
        # Чтобы отловиить данное сообытие, использзуется тег "event!". 
        # Обработку этого сообытия, можно посмотреть в файле callback.sc
        # callback_data - сюда помещаются данные, которые придут в $request.rawRequest.callback_query.data при нажатии на эту кнопку
        inlineButtons:
            {text:"База знаний", callback_data: "knowledgeBase"}
    
    
    # стейт для запуска чек-листа чистоты. при переходже по ссылке ввида: https://t.me/bot_name?start=cleaning-checklist
    state: WeekdayCheck
        # тег "q" - (от англ. "question" — вопрос ) тег для паттернов
        # Это локальный тег: переход по нему возможен только из ближайшего родительского, из соседних или дочерних стейтов.
        # $regex - паттерн для обработки регулярных выражений. Подробнее тут: https://help.just-ai.com/docs/ru/Patterns/advanced_patterns#regexpregexp_i
        q: $regex</start cleaning-checklist>
        # тег "script" предназначен для написания js кода, который должен отработать в стейте
        script:
            # вызов функции подговки к запуску чек-листа
            # функция объявлена в файле utils.js
            # подключение файла utils.js осуществляется в requirements.sc
            prepareForChecklist();
        # тег "go!" осуществляет переход в указанный стейт и запускает все теги реакции перечисленные в нем.
        # тег "go" осуществляет переход в указанный стейт без запуска перечисленных в нем тегов реакций
        go!: /Checklist/Start
    
    state: KnowledgeBase
        q!: база
        # тег "a" предназначени для вывода сообщения ползователю
        a: Ссылка на базу знаний: https://coda.io/d/CINNABON_doT76Y3ewFx/_surLS#_luKhB
        # тег "inlineButtons" выводит кнопки пользователю внутри сообщения
        # при нажатии кнопки пользоватлем в телеграм, срабатывает сообытие telegramCallbackQuery.
        # Чтобы отловиить данное сообытие, использзуется тег "event!". 
        # Обработку этого сообытия, можно посмотреть в файле callback.sc
        # callback_data - сюда помещаются данные, которые придут в $request.rawRequest.callback_query.data при нажатии на эту кнопку
        inlineButtons:
            {text:"Вернуться в меню", callback_data: "backToMenu"}

    # стейт для обработки события "Отправленный текст не распознан"
    state: NoMatch
        # Тег активации event! задает событие, по которому диалог может перейти в стейт.
        # Это глобальный тег: переход по нему возможен из любого другого стейта в сценарии.
        event!: noMatch
        # тег "a" предназначени для вывода сообщения ползователю
        # с помощью двойных фигурных скобок, можно подставлять js-выражения
        a: Я не понял. Вы сказали: {{$request.query}}