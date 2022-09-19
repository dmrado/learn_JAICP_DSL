theme: /
    
    state: Cancel
        # тег "q!" - (от англ. "question" — вопрос ) тег для паттернов
        # Это глобальный тег: переход по нему возможен из любого другого стейта в сценарии.
        q!: удалить напоминания
        if: !$client.eventId
            script:
                $reactions.answer("Все напоминания удалены");
        else:
            script:
                // $pushgate.cancelEvent - удаляет напоминание по указанному id
                try {
                    $pushgate.cancelEvent($client.eventId);
                }
                catch(e) {
                    log("ERROR: " + e.name + "\tMESSAGE: " + e.message);
                }
                // удаление свойства из переменной &client
                delete $client.eventId;
                // отправка сообщения пользователю
                $reactions.answer("Все напоминания удалены");

    state: Reminder
        # тег "q!" - (от англ. "question" — вопрос ) тег для паттернов
        # Это глобальный тег: переход по нему возможен из любого другого стейта в сценарии.
        q!: запустить напоминания
        # если напоминание уже запущено, то удлаем его
        if: $client.eventId
            script:
                // $pushgate.cancelEvent - удаляет напоминание по указанному id
                log('reminder $client.eventId: '+$client.eventId);
                try {
                    $pushgate.cancelEvent($client.eventId);
                }
                catch(e) {
                    log("ERROR: " + e.name + "\tMESSAGE: " + e.message);
                }
                // удаление свойства из переменной &client
                delete $client.eventId;
        script:
            // устанавливаем таймзоны Москва
            $reactions.setClientTimezone("Europe/Moscow");
            // формируем строку сообщения напоминания
            var reminderText = "Для запуска чек-листа Задачи чистоты  - перейдите по ссылке https://t.me/" + 
                $injector.bot_name + "?start=cleaning-checklist"
                
            //формирование полной даты для напоминания
            var dateCheckListDay = $jsapi.dateForZone("Europe/Moscow", "yyyy-MM-dd") + "T" + $injector.time_of_cleaning_checklist;

            //создание самого напоминания
            $temp.event = $pushgate.createEvent(
                dateCheckListDay,
                "reminderEvent",
                {
                    text: reminderText ///текст напоминания
                }
            );
            
            // запоминаем id напоминания, чтобы если будет повторный запуск напоминания, удалить старое напоминание
            $client.eventId = $temp.event.id; 
            // оповещаем клиента о запуске напоминания
            $reactions.answer("Запуск напоминаний! Старт напоминаний: "+dateCheckListDay); 
            

    # стейт обработки напоминаний
    state: Remind
        # Тег активации event! задает событие, по которому диалог может перейти в стейт.
        # Это глобальный тег: переход по нему возможен из любого другого стейта в сценарии.
        # reminderEvent - событие сформированное напоминанием через $pushgate.createEvent
        event!: reminderEvent
        script:
            // достаем сообщение, которое находится в eventData
            var message = $request.rawRequest.eventData.text;
            
            if (checklist[moment($jsapi.dateForZone("Europe/Moscow", "YYYY-MM-dd")).format("dddd")]!=undefined) {
                    $temp.response = sendMessageToGroupChat(message);
            }
            //формирование полной даты для напоминания
            var dateCheckListDay = $jsapi.dateForZone("Europe/Moscow", "yyyy-MM-dd") + "T" + $injector.time_of_cleaning_checklist;
            
            // формируем строку сообщения напоминания
            var reminderText = "Для запуска чек-листа Задачи чистоты  - перейдите по ссылке https://t.me/" + 
                $injector.bot_name + "?start=cleaning-checklist"
                
            //создание напоминания
            //таким способом мы формируем регулярное напоминание (при срабатывании напоминания, заводим новое)
            // к текущей дате прибавляем 1 день, так как напоминание заводим на следующий день
            var nextDay = moment(dateCheckListDay).add(1, "day").format();
            $temp.event = $pushgate.createEvent(
                nextDay,
                "reminderEvent",
                {
                    text: reminderText ///текст напоминания
                }
            );
            // запоминаем id напоминания, чтобы если будет повторный запуск напоминания, удалить старое напоминание
            $client.eventId = $temp.event.id; 
