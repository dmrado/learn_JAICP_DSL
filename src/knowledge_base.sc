theme: /
    # мини база знаний
   
    state: ProtPos
        q!: эй
        q!: * {прот* * посу*} *
        a: Инструкция по протирке посуды. Ссылка: https://447906.selcdn.ru/%D0%9A%D0%BD%D0%B8%D0%B3%D0%B8/telephone_receiver__%20(1)%20(1)%20(1).pdf 
        inlineButtons:
            {text:"Вернуться в меню", callback_data: "backToMenu"}
    
    state: ProtPrib
        q!: * {прот* * прибор*} *
        a: Инструкция по протирке приборов. Ссылка: https://447906.selcdn.ru/%D0%9A%D0%BD%D0%B8%D0%B3%D0%B8/telephone_receiver__%20(1)%20(1)%20(1).pdf     
        inlineButtons:
            {text:"Вернуться в меню", callback_data: "backToMenu"}
        
    state: ProtPover
        q!: * {прот* * (повер*|стол*)} *
        a: Инструкция по протирке поверхностей. Ссылка: https://447906.selcdn.ru/%D0%9A%D0%BD%D0%B8%D0%B3%D0%B8/telephone_receiver__%20(1)%20(1)%20(1).pdf     
        inlineButtons:
            {text:"Вернуться в меню", callback_data: "backToMenu"}
        
    state: InstAll
        a: Инструкция по ... Ссылка: https://447906.selcdn.ru/%D0%9A%D0%BD%D0%B8%D0%B3%D0%B8/telephone_receiver__%20(1)%20(1)%20(1).pdf     
        inlineButtons:
            {text:"Вернуться в меню", callback_data: "backToMenu"}  
            
            
            