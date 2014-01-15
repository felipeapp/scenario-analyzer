
function formataValorNota(campo, event, casas) {
	if ($(campo).readAttribute('maxlength') && campo.value.length >= $(campo).readAttribute('maxlength')) return;

    var point = '.';
    var comma = '.';
    var sep = 0;
    var key = '';
    var i = j = 0;
    var len = len2 = 0;
    var strCheck = '0123456789';
    var aux = aux2 = '';
    var rcode = event.which ? event.which : event.keyCode;
	 casas = parseInt(casas);

	 var e = YAHOO.ext.EventObject;
	 e.setEvent(event);
	 var ctrlCmd = (e.ctrlKey || event.metaKey)

	 if (campo.value == '0,00'){
		campo.value = "";
	 }
	 
    if (teclasEspeciais.indexOf(rcode) != -1) {
         return true; // Teclas especiais
    }

	var ctrlCmd = (e.ctrlKey || event.metaKey)
	if ((ctrlCmd && rcode == 67) || (ctrlCmd && rcode == 86) || rcode == 13) {
		return true;
	}


	 if (rcode >= 96 && rcode <= 105)
		rcode -= 48; // Teclado numerico, codigo diferente

    key = String.fromCharCode(rcode); // Pega o valor da tecla pelo codigo

    if (strCheck.indexOf(key) == -1 && ((ctrlCmd != 118) && (ctrlCmd  != 99))){
         return false; // Filtra teclas invalidas
    }


    len = campo.value.length;
    for(; i < len; i++){
         if (strCheck.indexOf(campo.value.charAt(i))!=-1){
              aux += campo.value.charAt(i);
         }
    }

    aux += key;
    len = aux.length;

	if (len == 0)     { campo.value = ''; }
    if (len <= casas) { campo.value = aux; }
    if (len > casas) {
         aux2 = '';
         for (j = 0, i = len - (casas + 1); i >= 0; i--) {
              if (j == casas + 1) {
                   aux2 += point;
                   j = 0;
              }
              aux2 += aux.charAt(i);
              j++;
         }
         campo.value = '';
         aux3 = '';
         len2 = aux2.length;
         for (i = len2 - 1; i >= 0; i--){
              aux3 += aux2.charAt(i);
         }

         if( aux != 100 )
         	campo.value += aux3.charAt(0) + comma + aux.charAt(1);
         else if (aux == 100)
        	 campo.value += "10.0";
    }

    return false;
}


function formataFalta(campo, event) {
	if ($(campo).readAttribute('maxlength') && campo.value.length >= $(campo).readAttribute('maxlength')) return;

    var key = '';
    var i = j = 0;
    var len = 0;
    var strCheck = '0123456789';
    var aux = '';
    var rcode = event.which ? event.which : event.keyCode;

	 var e = YAHOO.ext.EventObject;
	 e.setEvent(event);
	 var ctrlCmd = (e.ctrlKey || event.metaKey)
 
    if (teclasEspeciais.indexOf(rcode) != -1) {
         return true; // Teclas especiais
    }

	var ctrlCmd = (e.ctrlKey || event.metaKey)
	if ((ctrlCmd && rcode == 67) || (ctrlCmd && rcode == 86) || rcode == 13) {
		return true;
	}
	
	if (campo.value == '0'){
		campo.value = "";
	}	

	if (rcode >= 96 && rcode <= 105)
		rcode -= 48; // Teclado numerico, codigo diferente

    key = String.fromCharCode(rcode); // Pega o valor da tecla pelo codigo

    if (strCheck.indexOf(key) == -1 && ((ctrlCmd != 118) && (ctrlCmd  != 99))){
         return false; // Filtra teclas invalidas
    }

    len = campo.value.length;
    for(; i < len; i++){
         if (strCheck.indexOf(campo.value.charAt(i))!=-1){
              aux += campo.value.charAt(i);
         }
    }

    aux += key;
	if (aux.length == 0) { 
		campo.value = ''; 
	} else 
		campo.value = aux;

    return false;
}
