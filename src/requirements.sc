# запускаемые модули/библиотеки
require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: dateTime/moment.min.js
    module = sys.zb-common 

# подключение словариков
#словарь для хранения текстов действий по чек-листу
require: dicts/checkLists.yaml
    var = checklist
    name = checklist
    
# файлы с функциями
require: telegram_functions.js 
require: utils.js

# файлы сценария (просто разбиты на несколько по смыслу)
# стейт для обработки события telegramCallbackQuery
require: callback.sc

# стейты для обработки выполнения чек-листа
require: checklist.sc

# стейты для запуска и обработки напоминаний
require: reminder.sc

require: knowledge_base.sc