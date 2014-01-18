
var teclasEspeciais = new Array(Event.KEY_BACKSPACE, Event.KEY_TAB, Event.KEY_RETURN, Event.KEY_SHIFT, Event.KEY_ESC, Event.KEY_END, Event.KEY_HOME, Event.KEY_LEFT, Event.KEY_UP, Event.KEY_RIGHT, Event.KEY_DOWN, Event.KEY_DELETE);

/***
Descricao.: formata um campo do formulario de acordo com a mascara informada...

Parametros:
	- objForm (o Objeto Form)
	- strField (string contendo o nome do textbox)
	- sMask (mascara que define o formato que o dado sera apresentado, usando o
	algarismo "9" para definir numeros e o simbolo "!" para qualquer caracter...
	- evtKeyPress (evento)

Forma de uso: <input type="textbox" name="xxx"..... onkeypress="return txtBoxFormat(document.rcfDownload, 'str_cep', '99999-999', event);">

Observacao: As mascaras podem ser representadas como os exemplos abaixo:
	- CEP -> 99.999-999
	- CPF -> 999.999.999-99
	- CNPJ -> 99.999.999/9999-99
	- Data -> 99/99/9999
	- Tel Resid -> (99) 999-9999
	- Tel Cel -> (99) 9999-9999
	- Processo -> 99.999999999/999-99
	- C/C -> 999999-!
	- E por ai vai...

Ricardo Medeiros
***/

function txtBoxFormat(objForm, strField, sMask, evtKeyPress) {
	var i, nCount, sValue, fldLen, mskLen,bolMask, sCod, nTecla;

	if(document.all) { // Internet Explorer
	nTecla = evtKeyPress.keyCode; }
	else if(document.layers) { // Nestcape
	nTecla = evtKeyPress.which;
	}

	sValue = objForm[strField].value;

	// Limpa todos os caracteres de formatacao que
	// j? estiverem no campo.
	sValue = sValue.toString().replace( "-", "" );
	sValue = sValue.toString().replace( "-", "" );
	sValue = sValue.toString().replace( ".", "" );
	sValue = sValue.toString().replace( ".", "" );
	sValue = sValue.toString().replace( "/", "" );
	sValue = sValue.toString().replace( "/", "" );
	sValue = sValue.toString().replace( "(", "" );
	sValue = sValue.toString().replace( "(", "" );
	sValue = sValue.toString().replace( ")", "" );
	sValue = sValue.toString().replace( ")", "" );
	sValue = sValue.toString().replace( " ", "" );
	sValue = sValue.toString().replace( " ", "" );
	fldLen = sValue.length;
	mskLen = sMask.length;

	i = 0;
	nCount = 0;
	sCod = "";
	mskLen = fldLen;

	while (i <= mskLen) {
	bolMask = ((sMask.charAt(i) == "-") || (sMask.charAt(i) == ".") || (sMask.charAt(i) == "/"))
	bolMask = bolMask || ((sMask.charAt(i) == "(") || (sMask.charAt(i) == ")") || (sMask.charAt(i) == " "))

	if (bolMask) {
	sCod += sMask.charAt(i);
	mskLen++; }
	else {
	sCod += sValue.charAt(nCount);
	nCount++;
	}

	i++;
	}

	objForm[strField].value = sCod;

	if (nTecla != 8) { // backspace
	if (sMask.charAt(i-1) == "9") { // apenas numeros...
	return ((nTecla > 47) && (nTecla < 58)); } // numeros de 0 a 9
	else { // qualquer caracter...
	return true;
	} }
	else {
	return true;
	}
}
//Fim da Funcao Mascaras Gerais

/**
 *
 * function mask(_mask, val)
 *
 * _mask = Mascara Exemplo: ##/##/#### ou ###.###.###-##
 * val   = Valor a ser formatado.
 *
 * Formata um valor  para a mascara definida.
 *
 * pedro.leao@ig.com.br 2003/08/16
 *
 * Acrescentado o tamanho maximo da data.
 * andre sipac
 */
function mask(field, _mask, val) {
	var i, mki;
	var aux="";

	/*limite para o tamanho maximo da data. Aceita o tamanho maximo 10 -> xx/xx/xxxx */
	if (val.length >= field.maxLength){
		return val.substr(0,field.maxLength);
	}else{
		for(i=mki=0; i<val.length; i++, mki++) {
			if(_mask.charAt(mki)=='' || _mask.charAt(mki)=='#' || _mask.charAt(i)==val.charAt(i)) {
				aux+=val.charAt(i);
			} else {
				aux+=_mask.charAt(mki)+val.charAt(i);
				mki++;
			}
		}
	}
	return aux;
}

/**
 * function maskEvent(field, _mask, event)
 *
 * field = Objeto que esta enviando o evendo onKeyPress()
 * _mask = Mascara Exemplo: ##/##/#### ou ###.###.###-##
 * event = Evento a ser observado.
 *
 * Formata um valor para a mascara definida conforma o valor vai sendo digitado.
 *
 * pedro.leao@ig.com.br 2003;08/16
 */
function maskEvent(field, _mask, event) {

		var key ='';
	var aux='';
	var len=0;
	var i=0;
	var strCheck = '0123456789';
	var rcode = (window.Event) ? event.which : event.keyCode;

	// Enter, Backspace, e delete
	if ((rcode == 13) || (rcode == 8) || (rcode == 46)) {
		return true;
	}

	//Get key value from key code
	key=String.fromCharCode(rcode);

	if(strCheck.indexOf(key)==-1) {
		//Not a valid key
		return false;
	}

	aux=field.value+key;
	//window.alert(aux);
	aux=mask(field, _mask,aux);
	//window.alert(aux);
	field.value=aux;
	return false;
}

function maskEventJump(field, _mask, event, nextField) {

		var key ='';
	var aux='';
	var len=0;
	var i=0;
	var strCheck = '0123456789';
	var rcode = (window.Event) ? event.which : event.keyCode;

	// Enter, Backspace, e delete
	if ((rcode == 13) || (rcode == 8) || (rcode == 46)) {
		return true;
	}

	//Get key value from key code
	key=String.fromCharCode(rcode);


	if (field.value.length >= field.maxLength){
		nextField.focus();
	}

	if(strCheck.indexOf(key)==-1) {
		//Not a valid key
		return false;
	}

	aux=field.value+key;
	//window.alert(aux);
	aux=mask(field, _mask,aux);
	//window.alert(aux);
	field.value=aux;
	return false;
}

function formatarMascara(src, event, mask) {
	var i;

	if (document.selection) // Internet Explorer
		i = Math.abs(document.selection.createRange().moveStart("character", -1000000));
	else // Firefox
		i = src.selectionStart;

	var saida = mask.substring(0,1);
	var texto = mask.substring(i);
	var rcode = event.which ? event.which : event.keyCode;

	if (isSpecialCharacterFromKeyboard(event)) {
		return false;
	}
	
	// Enter, backspace, delete e setas direcionais
	if (teclasEspeciais.indexOf(rcode) != -1) {
		return true;
	}

	if (rcode >= 48 && rcode <= 57) {
		if (texto.substring(0,1) != saida)
			src.value = src.value.substring(0,i) + texto.substring(0,1) + src.value.substring(i);
		return true;
	} else {
		return false;
	}
}

/**
 * Verifica se a tecla digitada é de um caracter especial, como
 * #, $, %, &, ( ou ' (apóstrofe).
 * 
 * Para os caracteres "#, $, %, &, (", é verificado se a tecla shift foi pressionada.
 * 
 * @param event
 * @return true se algum caracter especial for encontrado.
 */
function isSpecialCharacterFromKeyboard(event) {
	
	var specialChars = "#$%&(";
	
	var shift = event.shiftKey;
	var charCode = event.charCode || event.keyCode;
	var index = -1;
	
	if (shift) {
		index = specialChars.indexOf(String.fromCharCode(charCode));
	}
	
	return (index != -1) || charCode == 39; //Apóstrofe
}

/**
 * Usado no evento "onblur" de elementos input[type="text"].
 * Remove caracteres especiais do texto.
 * 
 * @param src - Elemento HTML input[type="text"]
 * @return
 */
function removeNonNumbersCharacters(src) {
	
	src.value = src.value.replace(/\D/g, function(match) {
		return (match != ".") ? "" : match;
	});
}

function formataData(src, event) {
	return formatarMascara(src, event, '##/##/####');
}

function formataDataJump(src, nextField, event) {
	var rcode = (window.Event) ? event.which : event.keyCode;
	var result = formatarMascara(src, event, '##/##/####');

	// Enter, backspace, delete e setas direcionais
	if ((rcode != 13) && (rcode != 0) && (rcode != 8) && (rcode != 46)) {
		if ((src.value.length >= 9) && (nextField != null))
			setTimeout("changeFocus('"+nextField.id+"')", 100);
	}

	return result;
}

function formataHora(campo, event, proximoCampoId){
    var tecla = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    var length = campo.value.length + 1;
    if ((tecla == Event.KEY_RETURN) || (tecla == Event.KEY_BACKSPACE) || (tecla == Event.KEY_LEFT) || (tecla == Event.KEY_RIGHT) || (tecla == Event.KEY_DELETE))
        return true;
    if ((length == 1 || length == 6) && tecla == Event.KEY_TAB)
        return true;
    if (((tecla < 48) || (tecla > 57)) && (tecla != 0))
        return false;
    var rExp = /[^\0-\9]|\-|\./g;
    var vr = campo.value.replace(rExp, "");
    vr = vr.replace(":", "");
    if (vr.length + 1 >= 5)
        return false;
    var saida = vr.substring(0, 2);
    if (length >= 3)
        saida += ":" + vr.substring(2, 4);
    campo.value = saida;
    if ((proximoCampoId != null) && (length == 6))
        document.getElementById(proximoCampoId).focus();
    if (length == 5) {
        var valor = campo.value;
        var hora = valor.substring(0, 2);
        var min = valor.substring(3, 4);
        if (hora >= 24 || hora < 0) {
            window.alert('Hora inválida.');
            campo.value = '';
            return false;
        }
        if (min >= 6 || min < 0) {
            window.alert('Hora inválida.');
            campo.value = '';
            return false;
        }
    }
    
    var ie = navigator.userAgent.indexOf("Firefox") == -1;  
	   
    if (ie){  
    	// não funciona no IE
   	}else{  
   		if ((proximoCampoId != null) && (campo.value.length == 4) ){
   	      	document.getElementById(proximoCampoId).focus();
   	    }
	}
    
}

function formataCPF(campo, event, proximoCampoId) {
	var tecla = event.keyCode ? event.keyCode : event.charCode;
	    
	if (teclasEspeciais.indexOf(tecla) != -1) {
	   	return true;
	}
	    
	if ( '0123456789'.indexOf(String.fromCharCode(tecla)) == -1 ) {
		return false;
	}
	
	var rExp=/[^\0-\9]|\-|\.|\//g;var vr=campo.value.replace(rExp,"")
	vr=vr.replace("/","");
	vr=vr.replace(".","");
	vr=vr.replace("-","");

	var saida=vr.substring(0,3);

	var eventoApagar = (tecla == 8 || tecla == 46);

	var length = vr.length;

	if(eventoApagar){
		if(campo.value.length>=3 && length > 3) saida+="."+vr.substring(3,6);
		if(campo.value.length>=5 && length > 6) saida+="."+vr.substring(6,9);
		if(campo.value.length>=9 && length > 9) saida+="-"+vr.substring(9,11);
	}else{
		if(campo.value.length>=3) saida+="."+vr.substring(3,6);
		if(campo.value.length>=5 && length >= 6) saida+="."+vr.substring(6,9);
		if(campo.value.length>=9 && length >= 9) saida+="-"+vr.substring(9,11);
	}

	campo.value=saida;
	if((proximoCampoId!=null)&&(campo.value.length==14))
		document.getElementById(proximoCampoId).focus();
}

function formataCNPJ(campo,event,proximoCampoId){
	var tecla=event.keyCode?event.keyCode:event.which?event.which:event.charCode;
	if((tecla==Event.KEY_RETURN) || (tecla==Event.KEY_BACKSPACE)||(tecla==Event.KEY_TAB)
		||(tecla==Event.KEY_LEFT)||(tecla==Event.KEY_RIGHT)	||(tecla==Event.KEY_DELETE)){

		return true;
	}
	var vr=campo.value.replace(/[\D]+/g,"");
	var saida=vr.substring(0,2);

	if(vr.length>=2){ saida+="."+vr.substring(2,5);}
	if(vr.length>=4){ saida+="."+vr.substring(5,8);}
	if(vr.length>=8){ saida+="/"+vr.substring(8,12);}
	if(vr.length>=11){ saida+="-"+vr.substring(12,14);}

	campo.value=saida;

	if((proximoCampoId!=null) && (campo.value.length==18))
		document.getElementById(proximoCampoId).focus();
}

function formataCpfCnpj(campo,event,proximoCampoId){
	if(campo.value.length<15){
		formataCPF(campo,event,null);
	}else{
		formataCNPJ(campo,event,proximoCampoId)
	}
}

function formataCEP(campo,event,proximoCampoId)
{
	var tecla=(event!=null)?event.keyCode:0;if(((tecla<48)||(tecla>57))&&((tecla<96)||(tecla>105))&&(tecla!=0))
	var rExp=/[^\0-\9]|\-|\./g;var vr=campo.value.replace(rExp,"")
	vr=vr.replace("-","");var saida=vr.substring(0,5);if(campo.value.length>=5)
	saida+="-"+vr.substring(5,8);campo.value=saida;if((proximoCampoId!=null)&&(campo.value.length==9))
	document.getElementById(proximoCampoId).focus();
}

function formatarProtocolo(campo, event) {
	campo.value = campo.value.replace(/[^0-9|^\.|^/|^-]/, '');
	var rExp=/[^\0-\9]|\-|\.|\//g;
	var vr=campo.value.replace(rExp,"");

	var saida=vr.substring(0,5);

	if (campo.value.length >=  6) saida += "." + vr.substring( 5, 11);
	if (campo.value.length >= 13) saida += "/" + vr.substring(11, 15);
	if (campo.value.length >= 18) saida += "-" + vr.substring(15, 17);

	campo.value=saida;
}

function formataURL(campo, event){
	var rcode = event.which ? event.which : event.keyCode;

	// Enter, backspace, delete e setas direcionais
	if (teclasEspeciais.indexOf(rcode) != -1) {
		return true;
	}

	//(./:?a-z0-9)
	if ((rcode >= 46 && rcode <= 58) || (rcode >= 97 && rcode <= 122)
			||  (rcode >= 65 && rcode <= 90) ) {
		return true;
	} else {
		return false;
	}
}

/*
 * Início da função para formatação de valor
 * @author 	David Pereira
 * @date 	26/07/2006
 */
function formataValor(campo, event, casas) {
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

    len = aux.length + 1;

	if (len == 0)     { campo.value = ''; }
    if (len <= casas) { campo.value = aux; }
    if (len > casas) {
         aux2 = '';
         for (j = 0, i = len - (casas + 1); i >= 0; i--) {
              if (j == 3) { // um ponto a cada três dígitos
                   aux2 += point;
                   j = 0;
              }
              aux2 += aux.charAt(i);
              j++;
         }
         campo.value = '';
         len2 = aux2.length;
         for (i = len2 - 1; i >= 0; i--){
              campo.value += aux2.charAt(i);
         }
         campo.value += comma + aux.substr(len - casas, len);
    }

    return true;
}
/* Fim da função para formatação de valor */


function changeFocus(elmId) {
	var elm = document.getElementById(elmId);
	elm.focus();
}

function formataNumeroProjeto(campo, event, proximoCampoId) {
	var tecla = event.keyCode ? event.keyCode : event.charCode;
    
	if (teclasEspeciais.indexOf(tecla) != -1) {
	   	return true;
	}
	    
	if ( '0123456789'.indexOf(String.fromCharCode(tecla)) == -1 ) {
		return false;
	}
	
	var rExp = /[^\0-\9]|\-|\.|\//g;
	var vr = campo.value.replace(rExp, "")
	vr = vr.replace("/", "");
	vr = vr.replace(".", "");
	vr = vr.replace("-", "");

	var saida = "";

	var length = vr.length;
	
	if (length < campo.maxLength - 2) {
		if (length < 4) saida = vr;
		else if (length == 4) saida = vr.substring(0, 1) + "." + vr.substring(1, 5);
		else if (length == 5) saida = vr.substring(0, 2) + "." + vr.substring(2, 6);
		else if (length > 5) saida = vr.substring(0, length - 5) + "." + vr.substring(length - 5, length - 3) + "." + vr.substring(length - 3, length);

		campo.value = saida;
	}
}

function formataRIP(campo, event, proximoCampoId) {
	
	var tecla = event.keyCode ? event.keyCode : event.charCode;
    
	if (teclasEspeciais.indexOf(tecla) != -1) {
		return true;
	}
	
	var rExp = /\D+|\-|\./g;
	var vr = campo.value.replace(rExp, "")
	vr = vr.replace(".", "");
	vr = vr.replace("-", "");

	var saida = "";
	
	var length = vr.length;
	
	if (length < campo.maxLength - 2) {
		if (length < 4) saida = vr;
		else if (length == 4) saida = vr.substring(0, 3) + "-" + vr.substring(3, 4);
		else if (length <= 9) saida = vr.substring(0, length - 4) + "." + vr.substring(length - 4, length - 1) + "-" + vr.substring(length - 1, length);
		else if (length > 9) saida = vr.substring(0, length - 9) + "." + vr.substring(length - 9, length - 4) + "." + vr.substring(length - 4, length - 1) + "-" + vr.substring(length - 1, length);

		campo.value = saida;
	}
}

/**
 * Formata o NIT ou o número inscrição do PIS/PASEP de um trabalhador.
 * Todos seguem a mesma numeração, com 11 dígitos sendo o último o dígito verificador.
 * <br/><br/>
 * <b>Formato:</b> <i>999.99999.99-9</i>
 */
function formatarNIT_PIS_PASEP(campo, event, proximoCampoId) {
	
	//Obtém o código da tecla digitada
    var charCode = (event.which) ? event.which : event.keyCode

    //Caso não tenha digitado alguma das setas (Cod:37-40)
    if ((charCode < 37 || charCode > 40) && charCode != 8){
    	
	 	var rExp = /\D+|\-|\./g;
	    var vr = campo.value.replace(rExp, "")

	  	vr = vr.replace( ".", "" );
		vr = vr.replace( "-", "" );

		//Formato: 999.99999.99/9 
		
		var saida = vr.substring(0, 3); //Até os 2 primeiros dígitos
		
		if (campo.value.length >= 3) //Caso tenhas mais de 2 dígitos
			saida += "." + vr.substring(3, 8);
		if (campo.value.length >= 9) //Caso tenhas mais de 9 dígitos + pontuação
			saida +=  "." + vr.substring(8, 10);
		if (campo.value.length >= 12)//Caso tenhas mais de 12 dígitos + pontuação
			saida +=  "-" + vr.substring(10, 12);
		
	    campo.value = saida;
	    
	    //Caso tenha digitado um dígito 0-9 (Cod:48-57) NUM 0-9 (Cod:96-105)
	    if ((charCode > 47 && charCode < 58) || (charCode > 95 && charCode < 106)){
		
	    	//Caso tenha informado o próximo campo e tenha sido o ultimo número informado
			if ((proximoCampoId != null) && (campo.value.length == 14)){
				//Pula para o próximo campo	
		       	document.getElementById(proximoCampoId).focus();
		    }
	    }
    }
}
