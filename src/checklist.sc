theme: /
    state: Checklist
        # стейт с кнопкой запуска чек-листа
        state: Start
            script:
                # отправляем стартовое сообщение пользователю из словаря с фразами по чек-листу.
                # $reactions.answer("какое-то сообщение") - Метод выводит простой текстовый ответ.
                $reactions.answer(checklist[$session.checklistType].startPhrase)
            
            # тег "inlineButtons" выводит кнопки пользователю внутри сообщения
            # при нажатии кнопки пользоватлем в телеграм, срабатывает сообытие telegramCallbackQuery.
            # Чтобы отловиить данное сообытие, использзуется тег "event!". 
            # Обработку этого сообытия, можно посмотреть в файле callback.sc
            # callback_data - сюда помещаются данные, которые придут в $request.rawRequest.callback_query.data при нажатии на эту кнопку
            inlineButtons:
                {text: "СТАРТ", callback_data: "shiftStep"}
            
            state: Error
                # Тег активации event задает событие, по которому диалог может перейти в стейт.
                # Это локальный тег: переход по нему возможен только из ближайшего родительского, из соседних или дочерних стейтов.
                # событие noMatch - "Отправленный текст не распознан"
                event: noMatch
                # как только количество ошибок (в данном случаче попыток ввода в чат неожидаемой информации) равно 3, отправляем в группу сообщение
                if: $session.ErrorCounter === 3
                    script:
                        # формирование сообщения
                        var message = "У сотрудника " + $client.name +" "+ $client.username + " сложность с чек-листом чистоты.";
                        # отправка сообщения в групповой чат
                        $temp.response = sendMessageToGroupChat(message);
                # тег "a" предназначени для вывода сообщения ползователю
                # с помощью двойных фигурных скобок, можно подставлять js-выражения
                a: Я не понял. Вы сказали: {{$request.query}}. Начните пожалуйста выполнять чек-лист.
                script: 
                    #увеличение ошибка на +1
                    $session.ErrorCounter++
                # тег "go!" осуществляет переход в указанный стейт и запускает все теги реакции перечисленные в нем.
                go!: /Checklist/Start
    
        state: LoopBySteps
            script:
                # если щаг чек-листа меньше общего количества шагов, то продолжаем цикл
                # иначе отправляемся формировать отчет
                if ($session.stepNumber < checklist[$session.checklistType].steps.length) {
                    // достаем сообщени согласно номеру шага
                    var message = checklist[$session.checklistType].steps[$session.stepNumber];
                    // отправляем сообщение пользователю
                    $temp.response = sendMessageToUserChat(message);
                    
                    // если ранее такое сообщение уже отправляли, то предыдущее с таким же текстом удаляем
                    if ($session.early_id!="" && $session.early_text!="" && $session.early_text === message) {
                        deleteMessegeFromChat($session.early_id, $client.id, $injector.bot_token);
                    }
                    // запоминаем id - сообщения и текст, на случай повоторного отправления этого сообщения
                    $session.early_id = $temp.response ? $temp.response.result.message_id : null;
                    $session.early_text = message;
                }
                else {
                    // $reactions.transition() - осуществляет переход в указанный стейт с выполнением тегов реакций
                    $reactions.transition('/Checklist/Report');
                }
            
            #  если накопилось 3 ошибки, отправляем в группу с куратором сообщение     
            if: $session.ErrorCounter === 3 
                script:
                    var message = "У сотрудника " + $client.name + " " + 
                        $client.username + " сложность с чек-листом чистоты с пунктом " + 
                        ($session.stepNumber + 1) + "." + 
                        checklist[$session.checklistType].stepNames[$session.stepNumber];
                    // отправка сообщения в груповой чат
                    $temp.response = sendMessageToGroupChat(message);
            else:
                script:
                    # ожидаем ответа от пользовтеля в течении указанного времени в $injector.Timeout
                    # если сообщение от пользователя нет, то переходим в стейт '/Checklist/LoopBySteps/timeout'
                    $reactions.timeout({interval: $injector.Timeout, targetState: '/Checklist/LoopBySteps/timeout'});
    
            
            state: ShiftfileEvent
                # Тег активации event задает событие, по которому диалог может перейти в стейт.
                # Это локальный тег: переход по нему возможен только из ближайшего родительского, из соседних или дочерних стейтов.
                # событие fileEvent - событие отправки файла пользователем
                event: fileEvent
                script:
                    // если пользователь еще не отправлял файл, то сохроняем его в $client.report
                    // иначе сообщаем ему, что отправлять больше одного файла нельзя
                    if (!$client.report[$session.stepNumber]) {
                        $client.report[$session.stepNumber] = $request.data.eventData[0].url;
                        $session.stepNumber++; ///шаг увеличиваем +1
                        $session.ErrorCounter = 0; /// ошибки обнуляем
                        // после 3 секунд переходим в стейт обработки шага чек-листа
                        $reactions.timeout({interval: "3 sec", targetState: '/Checklist/LoopBySteps'});
                    } 
                    else {
                        var message = "Запрещено присылать больше одного фото. Необходимо повторить пункт.\nНажмите на кнопку \"Повторить\"";
                        $temp.response = sendMessageToUserChatWithButton(message,'Повторить', 'repit_step');
                        // если ранее такое сообщение отправляли, то удаляем его
                        if ($session.early_id!="" && $session.early_text!="" && $session.early_text === message) {
                            deleteMessegeFromChat($session.early_id, $client.id, $injector.bot_token);
                        }
                        $session.early_id = $temp.response.result.message_id;
                        $session.early_text = message;
                    }
            
            state: Error
                # Тег активации event задает событие, по которому диалог может перейти в стейт.
                # Это локальный тег: переход по нему возможен только из ближайшего родительского, из соседних или дочерних стейтов.
                # событие noMatch - "Отправленный текст не распознан"
                event: noMatch
                # если ошибок ровно три, то отправляем в общий чат, что у сотрудника проблемы с выполнением этого пункта
                script:
                    if ($session.ErrorCounter === 3) {
                        var message = "У сотрудника " + $client.name + " " + 
                            $client.username + " сложность с чек-листом чистоты с пунктом " + 
                            ($session.stepNumber + 1) + ". " + 
                            checklist[$session.checklistType].stepNames[$session.stepNumber];
                        $temp.response = sendMessageToGroupChat(message);
                    }
                    else {
                        var message = "Я не понял. Вы сказали: " + $request.query + ". Продолжите пожалуйста выполнять чек-лист.";
                        sendMessageToUserChat(message);
                    }
                    // увеличиваем количество ошибок на единицу
                    $session.ErrorCounter++
                go!: /Checklist/LoopBySteps
            
            # стейт для обработки таймаута, если пользователь долго не отправляет фото
            state: timeout
                script:
                    $session.timeout = true;
                    $session.ErrorCounter++;
                go!: /Checklist/LoopBySteps
        
        state: Report
            script:
                ///получаем время окончания (когда все пункты выполнены)
                var amountEndTimeInMilliseconds = $jsapi.timeForZone("Europe/Moscow");
                
                //разница между концом и началом в минутых
                var interval = Math.round((amountEndTimeInMilliseconds - $client.amountStartTimeInMilliseconds)/60000);
                
                // формируем день недели в родительном падеже
                var dayInGenitive = $nlp.inflect(checklist[$session.checklistType].dayName, "gent");
                // формируем день недели в винительном падеже
                var dayInAccs = $nlp.inflect(checklist[$session.checklistType].dayName, "accs");
                //420 для чек-листа чистоты  
                // если сотрудник уложился в это время, то чек-лист выполнен
                if (interval > 420){
                    $reactions.answer('Чек лист \«Задачи чистоты ' + dayInGenitive + '\» провален!');
                } else {
                    $reactions.answer('Задачи чистоты ' + dayInGenitive + ' выполнены!');
                    // формируем сообщение о выполнении чек-листа
                    var message = "Сотрудник " + $client.name +" "+ $client.username + " справился с чек-листом чистоты за " + dayInAccs + ". время выполнения - " + interval + " " + $caila.conform("минута", interval) + " \nОтчёт:";
                    $temp.response = sendMessageToGroupChat(message);
                    for (var i = 0; i < $client.report.length; i++) {
                        var message = " " + $client.name +" "+ $client.username + " "  + "Пункт " + (i + 1) + "." + checklist[$session.checklistType].stepNames[i] + ": " + $client.report[i]
                        $temp.response = sendMessageToGroupChat(message);
                    }
                }
        