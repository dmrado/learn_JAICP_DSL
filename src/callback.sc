theme: /
    #получение обратной даты от нажания инлайн кнопок
    state: ReceiptCallbackQuery
        # Тег активации event задает событие, по которому диалог может перейти в стейт.
        # Это локальный тег: переход по нему возможен только из ближайшего родительского, из соседних или дочерних стейтов.
        event: telegramCallbackQuery
        
        # if/else/elseif — теги для записи простых условий, вывода различных ответов в зависимости от условий, перехода в другие состояния по условиям.
        if: $request.rawRequest.callback_query.data === "knowledgeBase" //база знаний
            # тег "go!" осуществляет переход в указанный стейт и запускает все теги реакции перечисленные в нем.
            # тег "go" осуществляет переход в указанный стейт без запуска перечисленных в нем тегов реакций
            go!: /KnowledgeBase
        
        if: $request.rawRequest.callback_query.data == "backToMenu" //кнопка назад в меню
            # тег "go!" осуществляет переход в указанный стейт и запускает все теги реакции перечисленные в нем.
            # тег "go" осуществляет переход в указанный стейт без запуска перечисленных в нем тегов реакций
            go!: /MainMenu

        # начало цикла запускается кнопкой с callback_data: "shiftStep"
        if: $request.rawRequest.callback_query.data === "shiftStep" //реакция нажатия кнопки СТАРТ
            # тег "script" предназначен для написания js кода, который должен отработать в стейте
            script:  
                # Запоминаем, когда начал выполнение чек-листа пользователь
                $client.amountStartTimeInMilliseconds = $jsapi.timeForZone("Europe/Moscow");
                var dayInAccs = $nlp.inflect(checklist[$session.checklistType].dayName, "accs");
                var message = "Сотрудник " + $client.name +" "+ $client.username +" занимается чек-листом чистоты за "+dayInAccs;
                # Отправляем сообщени в общий чат, что пользователь приступил к выполнению чек-листа
                $temp.response = sendMessageToGroupChat(message);
                $session.ErrorCounter = 0;
            # тег "go!" осуществляет переход в указанный стейт и запускает все теги реакции перечисленные в нем.
            # тег "go" осуществляет переход в указанный стейт без запуска перечисленных в нем тегов реакций
            go!: /Checklist/LoopBySteps
        
        if: $request.rawRequest.callback_query.data === "repit_step" 
            go!: /Checklist/LoopBySteps
