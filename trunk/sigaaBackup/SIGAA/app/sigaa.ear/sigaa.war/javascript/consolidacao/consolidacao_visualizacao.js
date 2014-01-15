var permiteTutor = null;
if (getEl('permiteTutor') != null)
	permiteTutor = getEl('permiteTutor').dom.value;

function formataValorNota(campo, event, casas) {
	if ($(campo).readAttribute('maxlength') && campo.value.length >= $(campo).readAttribute('maxlength')) return;

    var point = '.';
    var comma = ',';
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
		rcode -= 48; // Teclado num?rico, c?digo diferente

    key = String.fromCharCode(rcode); // Pega o valor da tecla pelo c?digo

    if (strCheck.indexOf(key) == -1 && ((ctrlCmd != 118) && (ctrlCmd  != 99))){
         return false; // Filtra teclas inv?lidas
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
        	 campo.value += "10,0";
    }

    return false;
}

// Esconde a nota atrav�s do JSP
function isExibirNota(idUnidade,ead) {
	var isEad = (ead === "true" );
	var isPermiteTutor = (permiteTutor == "true");
	var isAvaliacaoVazia = false;
	var notaUnidade = $('nota_' + idUnidade);
	var resultadoEad = null;

	if (isEad && isPermiteTutor)
		resultadoEad = document.getElementsByClassName('resultadoUnidEad_' + idUnidade)[0];
	
	// Como o IE n�o implementa o m�todo document.getElementsByClassName � nescess�rio fazer um tratamento.
	var isMicrosoft = false;
	if ( navigator.appName.indexOf("Microsoft") != -1 )
		isMicrosoft = true;
	
	if ( isMicrosoft )
	{
		document.getElementsByClassName = function(className)
		{
			var hasClassName = new RegExp("(?:^|\\s)" + className + "(?:$|\\s)");
			var allElements = document.getElementsByTagName("*");
			var results = [];
	
			var element;
			for (var i = 0; (element = allElements[i]) != null; i++) {
				if (hasClassName.test(element.className))
					results.push(element);
			}
	
			return results;
		}
	}	
	$A(document.getElementsByClassName('avaliacao' + idUnidade)).each(
		function(item) {
			if ( item.value == '' )
				isAvaliacaoVazia = true;
		}	
	);
	
	if ( isAvaliacaoVazia ){
		notaUnidade.value = '';
	}	
	if ( isEad && isPermiteTutor && notaUnidade.value == ''){
			resultadoEad.innerHTML = '';
	}
}
