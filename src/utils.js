function prepareForChecklist() {
    // объявление переменных и присвоение им значений, для более короткого обращения к ним
    
    // Объект для сохранения любых сессионных данных. 
    // После завершения всех реакций бота структура $session сохраняется во внутренней базе данных. 
    // При начале новой сессии все данные будут обнулены.
    // внутри js файлов обращение к переменно осуществляется через $jsapi.context()
    var session = $jsapi.context().session;
    
    // Объект для сохранения постоянных данных о клиенте. После завершения всех реакций бота структура $client сохраняется во внутренней базе данных. Не имеет особых полей.
    // внутри js файлов обращение к переменно осуществляется через $jsapi.context()
    var client = $jsapi.context().client;
    
    // Переменная $request содержит данные запроса клиента.
    // внутри js файлов обращение к переменно осуществляется через $jsapi.context()
    // Объект запроса в исходном виде для последующей обработки в сценарии.
    var request = $jsapi.context().request.rawRequest;
    // указание id клмента
    session.clientChatId = request.message.chat.id
    // установка таймзоны Москва для напоминаний-будильников
    $reactions.setClientTimezone("Europe/Moscow");
    // установка английского языка для функции  moment, которая в боте получает название дня недели
    moment.locale('en');
    // указание ника пользователя
    client.username = request.message.from.username;
    if (client.username === undefined){
       client.username = "nonickname"; 
    }else{
       client.username = "@"+client.username; 
    }
    // указание имени пользователя  
    client.name = request.message.from.first_name;
    // id чата с клиентом
    client.id = request.message.from.id
    // создание массива под отчет
    client.report = [];
    // шаг начала цикла по смене
    session.stepNumber = 0;
    // счетчик ошибок
    session.ErrorCounter = 0;
    // переменная для запоминания id сообщения, которое нужно будет удалить при срабаытывании напомнинаия (придет такое же новое)
    session.early_id!="";
    
    // // переменная для запоминания текста сообщения, которое нужно будет удалить при срабаытывании напомнинаия (придет такое же новое)
    session.early_text!="";
    
    // шаг выполнения чек-листа
    session.stepNumber = 0;
    // количество возникших трудностей по ходу выполнения шага чек-листа
    session.ErrorCounter = 0;
    session.checklistType = moment($jsapi.dateForZone("Europe/Moscow", "YYYY-MM-dd")).format("dddd");
}