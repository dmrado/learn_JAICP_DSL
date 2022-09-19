
//функция удаления сообщений  из чата
function deleteMessegeFromChat(messageId, chatId, token) {
    var url = "https://api.telegram.org/bot"+token+"/deleteMessage";
    var options = {
        dataType: "json",
        body: {
            "chat_id": chatId,
            "message_id": messageId
            
            }
    };
    var response = $http.post(url, options);
    log('delete method response:\n'+toPrettyString(response));
    return response.isOk ? response.data : false;
}

//функция отправки сообщений в группу куратору
function sendMessageToGroupChat(message) {
    var id = $jsapi.context().injector.group_chat_id;
    var token =  $jsapi.context().injector.bot_token;
    var url = "https://api.telegram.org/bot"+token+"/sendMessage";
    var options = {
        dataType: "json",
        body: {
            "chat_id": id, 
            "text": message 
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

//функция отправки сообщений себе в чат
function sendMessageToUserChat(message) {
    var chat_id = $jsapi.context().session.clientChatId;
    var token =  $jsapi.context().injector.bot_token;
    var url = "https://api.telegram.org/bot"+token+"/sendMessage";
    var options = {
        body: {
            "chat_id": chat_id,
            "text": message 
        }
    };
    
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

//функция отправки сообщений + кнопка себе в чат
function sendMessageToUserChatWithButton(message, message_with_button, callback_button) {
    var chat_id = $jsapi.context().session.clientChatId;
    var token =  $jsapi.context().injector.bot_token;
    var url = "https://api.telegram.org/bot"+token+"/sendMessage";
    var options = {
        body: {
            "chat_id": chat_id,
            "text": message,
            "reply_markup":{
                "inline_keyboard":[
                    [
                        {
                            "text": message_with_button,
                            "callback_data": callback_button
                        }
                    ]
                ]
            }
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}


