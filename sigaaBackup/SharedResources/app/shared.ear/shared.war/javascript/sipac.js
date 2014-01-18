
// Bloco de código para esconder e mostrar form
var Ver4 = parseInt(navigator.appVersion) >= 4
var IE4 = ((navigator.userAgent.indexOf("MSIE") != -1) && Ver4)
var block = "formulario";
function esconde() { document.form.style.visibility = "hidden" }
function mostra()  { document.form.style.visibility = "visible" }
// Bloco de código para esconder e mostra form



// Fun??o para confirmar opera??es
function confirmarOperacao(mensagem) {
	if (confirm(mensagem))
		return true;
	else
		return false;
}
// Fun??o para confirmar opera??es



// Bloco para Impressão
function printPage() {

  if ((window.print) ? 1 : 0) // NS4, IE5
    window.print()
  else if ((document.all) ? 1 : 0 && !(navigator.userAgent.indexOf("Mac") != -1)) // IE4 (Windows)
    vbPrintPage()
  else // other browsers
    alert("Desculpe seu browser não suporta esta função. Por favor utilize a barra de trabalho para imprimir a página.");
  return false;
  
}
// Bloco para Impressão



/*
 * In?cio da Defini??o de Teclas Especiais
 * @author David Pereira
 * @date   26/07/2006
 */
Event.KEY_SHIFT = 16;
Event.KEY_END 	= 35;
Event.KEY_HOME 	= 36;

var teclasEspeciais = new Array(
	Event.KEY_BACKSPACE,	// 08 - Backspace
	Event.KEY_TAB, 			// 09 - Tab
	Event.KEY_RETURN, 		// 13 - Enter
	Event.KEY_SHIFT,		// 16 - Shift
	Event.KEY_ESC,			// 27 - Esc
	Event.KEY_END, 			// 35 - End
	Event.KEY_HOME, 		// 36 - Home
	Event.KEY_LEFT, 		// 37 - Seta esquerda
	Event.KEY_UP, 			// 38 - Seta para cima
	Event.KEY_RIGHT, 		// 39 - Seta direita
	Event.KEY_DOWN, 		// 40 - Seta abaixo
	Event.KEY_DELETE  		// 46 - Delete
);

/* Fim da Defini??o de Teclas Especiais */

/* Função usada para so permirtir que o usuario digite numeros */
function permiteNumero(campo, event) {

    var tecla = (event!=null)? event.keyCode: 0;

	if ( ((tecla < 48) || (tecla > 57))  &&
		 ((tecla < 96) || (tecla > 105)) &&
		 (tecla != 0) )

    var rExp = /[^\0-\9]|\-|,|:|;|\./g;
    var vr = campo.value.replace(rExp, "");
    campo.value = vr;
}


function procuraCodigo(form, campo, codigo, tecla ) {

	valorCodigo = form[codigo].value;
	opcoes = form[campo].options;

	for (a = 0; a < form[campo].length; a++ ) {
		if ( valorCodigo == opcoes[a].value ) {
			form[campo].focus();
			form[campo].selectedIndex = a;
			window.status = "Campo Encontrado com Sucesso.";
			return;
		}
	}
	alert("C?digo n?o encontrado na lista");


}

// N?o emite o alerta
function procuraCodigoSilent(form, campo, codigo, tecla ) {

	valorCodigo = form[codigo].value;
	opcoes = form[campo].options;

	for (a = 0; a < form[campo].length; a++ ) {
		if ( valorCodigo == opcoes[a].value ) {
			form[campo].focus();
			form[campo].selectedIndex = a;
			window.status = "Campo Encontrado com Sucesso.";
			return;
		}
	}
	window.status = "C?digo n?o encontrado";
}

/**
 * Fun??o para marcar o valor de um check box. Uma string com o id da
 * checkBox deve ser passado como argumento
 * @author Rafael Borja
 */
function marcaCheckBox(id) {
	var box = document.getElementById(id);
	box.checked = true;
}


/**
 * Marca/desmarca todos os checkboxs do campo passado como par?metro.
 * Marca/desmarca apenas os checkboxs disabled = false
 *
 */
var checkflag = false;
function marcarTodosCheckbox(field) {
  if (field.length != undefined) {
  	for (i = 0; i < field.length; i++) {
  		if (field[i].disabled != true)
	  		field[i].checked = !checkflag;
  	}
  }
  else if (field.disabled != true) {
  	field.checked = !checkflag;
  }

  checkflag = !checkflag;
  return checkflag;
}


//Para selecionar um radio button ap?s clicar em um campo do formul?rio referente a ele. Usado quando
//o atributo referente ao nome do campo ? do tipo <atributo>.<campo>
function seleciona(value) {
	var element;
	var form = document.getElementById("formTeste");

	for (i = 0; i < form.elements.length; i++) {
		if (form.elements[i].value == value)
			element = form.elements[i];
	}

	element.checked = true;
}

// Marcar o checkbox de acordo com seu valor (Consultas Patrimonio)
function marcarOpcao(campo, valor) {
	var element;
	var field = document.forms[0].opcoesBusca;

	i = 0;
	achou = false;

	while (i < field.length && achou == false) {
		if (field[i].value == valor ) {
			element = field[i];
			achou = true;
		}
		i++;
	}

	if (campo.value == '') {
		element.checked = false;
	} else {
		element.checked = true;
	}

}


/**
  * Marcara cpf, pulando automanticamente para pr?ximo campo
  */
function formataCPF(campo, event, proximoCampoId) {
	
	var tecla = event.keyCode ? event.keyCode : event.charCode;
    
	if (teclasEspeciais.indexOf(tecla) != -1) {
	   	return true;
	}
	    
	if ( '0123456789'.indexOf(String.fromCharCode(tecla)) == -1 ) {
		return false;
	}

    var rExp = /[^\0-\9]|\-|\.|\//g;
    var vr = campo.value.replace(rExp, "")

  	vr = vr.replace( "/", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( "-", "" );

	var saida = vr.substring(0, 3);
	if (campo.value.length >= 3)
		saida += "." + vr.substring(3, 6);
	if (campo.value.length >= 5)
		saida +=  "." + vr.substring(6, 9);
	if (campo.value.length >= 9)
		saida +=  "-" + vr.substring(9, 11);

     campo.value = saida;

    if ((proximoCampoId != null) && (campo.value.length == 14))
       document.getElementById(proximoCampoId).focus();
}

/**
  * Marcara cpf, pulando automanticamente para pr?ximo campo, sem utilizar
  * nenhum evento, recebendo o cpf sem ser formatado
  */
function parseIntToCPF(cpfInt)
{
	var cpfStr = "" + cpfInt;
    var rExp = /[^\0-\9]|\-|\.|\//g;
    var vr = cpfStr.replace(rExp, "");

  	while(vr.length<11){
  		vr = "0" + vr;
  	}
  	
	var saida = vr.substring(0, 3);
	if (vr.length >= 3)
		saida += "." + vr.substring(3, 6);
	if (vr.length >= 5)
		saida +=  "." + vr.substring(6, 9);
	if (vr.length >= 9)
		saida +=  "-" + vr.substring(9, 11);

	 return saida;
}

/**
  * Marcara cnpj, pulando automanticamente para pr?ximo campo
  */
function formataCNPJ(campo, event, proximoCampoId)
{
    var tecla = (event!=null)? event.keyCode: 0;

	if ( ((tecla < 48) || (tecla > 57))  &&
		 ((tecla < 96) || (tecla > 105)) &&
		 (tecla != 0) )
			return;

    var rExp = /[^\0-\9]|\-|\.|\//g;
    var vr = campo.value.replace(rExp, "")

	vr = vr.replace( "/", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( "-", "" );

	var saida = vr.substring(0, 2);
	if (campo.value.length >= 2)
		saida += "." + vr.substring(2, 5);
	if (campo.value.length >= 4)
		saida +=  "." + vr.substring(5, 8);
	if (campo.value.length >= 8)
		saida +=  "/" + vr.substring(8, 12);
	if (campo.value.length >= 11)
		saida +=  "-" + vr.substring(12, 14);

     campo.value = saida;

     if ((proximoCampoId != null) && (campo.value.length == 18))
       document.getElementById(proximoCampoId).focus();
}

/**
  * Marcara cpf/cnpj, pulando automanticamente para pr?ximo campo.
  * Depende do tamanho da string passada, escolhe se CPF ou se CNPJ
  */
function formataCpfCnpj(campo, event, proximoCampoId) {

	// o que nao for ".", "-", "/" e "digito" remova
	campo.value = campo.value.replace(/[^\.\-\/\d]/g, "")
	
	if (campo.value.length < 15){
		formataCPF(campo, event, null);
	}else{
		formataCNPJ(campo, event, proximoCampoId)
	}
}

/**
  * Marcara para CEP, pulando automanticamente para próximo campo
  */
function formataCEP(campo, event, proximoCampoId){
	
	//Obtém o código da tecla digitada
    var charCode = (event.which) ? event.which : event.keyCode

    //Caso não tenha digitado alguma das setas (Cod:37-40)
    if ((charCode < 37 || charCode > 40) && charCode != 8){
	
	    var rExp = /\D+|\-|\./g;
	    var vr = campo.value.replace(rExp, "")
	    vr = vr.replace( "-", "" );
	
		var saida = vr.substring(0, 5);
		if (campo.value.length >= 5)
			saida += "-" + vr.substring(5, 8);
	
	    campo.value = saida;
	
	    if ((proximoCampoId != null) && (campo.value.length == 9))
	       document.getElementById(proximoCampoId).focus();
    }
}

// Fun??o que recebe um valor(float) e retornar? o valor formatado.
function formatarValor(valor,tammax) {
	var valorFormatado ="";
	vr = valor.toString();

	if(vr.indexOf(".") != -1){
		centavos = vr.substring(vr.indexOf(".")+1,vr.length);
		if(centavos.length == 1){
			vr = vr.concat("0");
		}
	}
	else{
		vr = vr.concat(".00");
	}

	vr = vr.replace( "/", "" );

	vr = vr.replace( "/", "" );
	vr = vr.replace( ",", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( ".", "" );
	tam = vr.length;

	if ( tam <= 2 ){
 		valorFormatado = vr ; }
 	if ( (tam > 2) && (tam <= 5) ){
 		valorFormatado = vr.substr( 0, tam - 2 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 6) && (tam <= 8) ){
 		valorFormatado = vr.substr( 0, tam - 5 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 9) && (tam <= 11) ){
 		valorFormatado = vr.substr( 0, tam - 8 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 12) && (tam <= 14) ){
 		valorFormatado = vr.substr( 0, tam - 11 ) + '.' + vr.substr( tam - 11, 3 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 15) && (tam <= 17) ){
 		valorFormatado = vr.substr( 0, tam - 14 ) + '.' + vr.substr( tam - 14, 3 ) + '.' + vr.substr( tam - 11, 3 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ;}

 	return valorFormatado;
}

//Fun??o que calcula o imposto de renda, imposto sobre o servi?o e o valor gasto com a seguridade social.
function calculoDespesa(form)
{
	var c = form.elements[0].value;
	c = c.replace( ".", "" );
	c = c.replace( ".", "" );
	c = c.replace( ",", "." );
	if(c.length==0){valor = parseFloat(0);}
	else{valor = parseFloat(c);}

	var inssEmpregado = 0;
	var inssEmpregador = (Math.round(valor * 0.2 * 100))/100;

	if (valor>=2508.72){
		inssEmpregado = 275.96;
	}else{
		inssEmpregado = (Math.round(valor * 0.11 * 100))/100;
	}

	var servico = (Math.round(valor * 0.05 * 100))/100;
	var renda = 0;
	if((valor-inssEmpregado)<=1164.00)
	{
		renda=0;
	}
	if((valor-inssEmpregado)<=2326.00 && (valor-inssEmpregado)>=1164.01)
	{
		renda = (Math.round(((valor-inssEmpregado) * 0.15 - 174.60) * 100))/100;
	}
	if((valor-inssEmpregado)>=2326.01)
	{
		renda = (Math.round(((valor-inssEmpregado)*0.275 - 465.35) * 100))/100;
	}

	liquido = (Math.round((valor - (renda + servico+inssEmpregado)) * 100))/100;

	renda = formatarValor(renda,14);
	form.elements[1].value = renda;
	form.elements[1].style.color='#0000FF';

	servico=formatarValor(servico,14);
	form.elements[2].value = servico;
	form.elements[2].style.color='#0000FF';

	inssEmpregado=formatarValor(inssEmpregado,14);
	form.elements[3].value = inssEmpregado;
	form.elements[3].style.color='#0000FF';

	liquido=formatarValor(liquido,14);
	form.elements[4].value = liquido;
	form.elements[4].style.color='#0000FF';

	inssEmpregador=formatarValor(inssEmpregador,14);
	form.elements[5].value = inssEmpregador;
	form.elements[5].style.color='#0000FF';


}

//Fun??o que escreve um valor por extenso
function extenso(form, origem, destino){
	if(origem == null){
		c = form.elements[0].value;
	}else{
		c = $(origem).value;
	}
	c = c.replace( ".", "" );
	c = c.replace( ".", "" );
	if(c == "" || c <= 0 || c >=10000000){
		if(destino == null){
			form.elements[6].value="ZERO";
		}else{
			c = $(destino).value;
		}
		return(-1);
	}else{
		if(c == 0){
			return(" ZERO ");}
		else{
			aUnid = new Array();
			aDezena = new Array();
			aCentena = new Array();
			aGrupo = new Array();
			aTexto = new Array();

			aUnid[1] = "UM ";
			aUnid[2] = "DOIS ";
			aUnid[3] = "TRÊS ";
			aUnid[4] = "QUATRO ";
			aUnid[5] = "CINCO ";
			aUnid[6] = "SEIS ";
			aUnid[7] = "SETE ";
			aUnid[8] = "OITO ";
			aUnid[9] = "NOVE ";
			aUnid[10] = "DEZ ";
			aUnid[11] = "ONZE ";
			aUnid[12] = "DOZE ";
			aUnid[13] = "TREZE ";
			aUnid[14] = "QUATORZE ";
			aUnid[15] = "QUINZE ";
			aUnid[16] = "DEZESSEIS ";
			aUnid[17] = "DEZESETE ";
			aUnid[18] = "DEZOITO ";
			aUnid[19] = "DEZENOVE ";

			aDezena[1] = "DEZ ";
			aDezena[2] = "VINTE ";
			aDezena[3] = "TRINTA ";
			aDezena[4] = "QUARENTA ";
			aDezena[5] = "CINQUENTA ";
			aDezena[6] = "SESSENTA ";
			aDezena[7] = "SETENTA ";
			aDezena[8] = "OITENTA ";
			aDezena[9] = "NOVENTA ";

			aCentena[1] = "CENTO ";
			aCentena[2] = "DUZENTOS ";
			aCentena[3] = "TREZENTOS ";
			aCentena[4] = "QUATROCENTOS ";
			aCentena[5] = "QUINHENTOS ";
			aCentena[6] = "SEISCENTOS ";
			aCentena[7] = "SETECENTOS ";
			aCentena[8] = "OITOCENTOS ";
			aCentena[9] = "NOVECENTOS ";

			if(c.indexOf(".") != -1){
				aGrupo[4] = c.substring(c.indexOf(".")+1,c.length);
				aGrupo[4] = aGrupo[4].substring(0,2);
				ct = c.substring(0,c.indexOf("."));
			}
			else{
				if(c.indexOf(",") != -1)	{
					aGrupo[4] = c.substring(c.indexOf(",")+1,c.length);
					aGrupo[4] = aGrupo[4].substring(0,2);
					ct = c.substring(0,c.indexOf(","));
				}
				else{
					aGrupo[4] = "00";
					ct = c;
				}
				tt = "";
				for(f=0;f<(10-ct.length);f++){tt += "0";}
				tt += ct;
			}
			aGrupo[1] = tt.substring(1,4);
			aGrupo[2] = tt.substring(4,7);
			aGrupo[3] = tt.substring(7,10);
			aGrupo[4] = "0"+aGrupo[4];

			for(f=1;f<5;f++){
				cParte = aGrupo[f];
				if(parseFloat(cParte) < 10){nTamanho = 1;}
				else{
					if(parseFloat(cParte) < 100){nTamanho = 2;}
					else{
						if(parseFloat(cParte) < 1000){nTamanho = 3;}
						else{nTamanho = 0;}
					}
				}
				aTexto[f] = "";
				if(nTamanho == 3){
					if(cParte.substring(1,3) != "00"){
						aTexto[f] += aCentena[cParte.substring(0,1)] + "E ";
						nTamanho = 2;
					}
					else{
						if(cParte.substring(0,1) == "1"){aTexto[f] += "CEM ";}
						else{aTexto[f] += aCentena[cParte.substring(0,1)];}
					}
				}
				if(nTamanho == 2){
					if(parseFloat(cParte.substring(1,3)) < 10){aTexto[f] += aUnid[cParte.substring(2,3)];}
					else{
						if(parseFloat(cParte.substring(1,3)) < 20){aTexto[f] += aUnid[cParte.substring(1,3)];}
						else{
							aTexto[f] += aDezena[cParte.substring(1,2)];
							if(cParte.substring(2,3) != "0"){
								aTexto[f] += "E ";
								nTamanho = 1;
							}
						}
					}
				}
				if(nTamanho == 1){aTexto[f] += aUnid[cParte.substring(2,3)];}
			}
			if(parseFloat(aGrupo[1] + aGrupo[2] + aGrupo[3]) == 0 && parseFloat(aGrupo[4]) != 0){
				cFinal = aTexto[4];
				if(parseFloat(aGrupo[4]) == 1){cFinal +=  "CENTAVO";}
				else{cFinal +=  "CENTAVOS";}
			}
			else{
				if(parseFloat(aGrupo[1]) != 0){
					cFinal = aTexto[1];
					if(parseFloat(aGrupo[1]) > 1){cFinal += "MILHÕES ";}
					else{cFinal += "MILHÃO ";}
					if(parseFloat(aGrupo[2] + aGrupo[3]) == 0){cFinal += "DE ";}
					else{cFinal += "E ";}
				}
				else{cFinal = "";}
				if(parseFloat(aGrupo[2]) != 0){
					cFinal += aTexto[2] + "MIL ";
					if(parseFloat(aGrupo[3]) != 0){cFinal += "E ";}
				}
				if(parseFloat(aGrupo[3]) != 0){cFinal += aTexto[3];}
				if(parseFloat(aGrupo[1] + aGrupo[2] + aGrupo[3]) == 1){cFinal += "REAL";}
				else{cFinal += "REAIS";}
				if(parseFloat(aGrupo[4]) != 0){
					cFinal += " E "+aTexto[4];
					if(parseFloat(aGrupo[4])==1){cFinal += "CENTAVO";}
					else{cFinal += "CENTAVOS";}
				}
			}
			if(destino == null){
				form.elements[6].value=cFinal;
			}else{
				$(destino).value=cFinal;
			}
			return(cFinal);
		}
	}
}

//-->
// Fun??o para compatibilidade do menu de unidade com o IE
startList = function() {
	if (document.all&&document.getElementById) {
		navReq = document.getElementById("menu_requisicoes");
		for (i=0; i<navReq.childNodes.length; i++) {
			node = navReq.childNodes[i];
			if (node.nodeName=="LI") {
				node.onmouseover=function() {
					this.className+=" over";
				}
				  node.onmouseout=function() {
					  this.className=this.className.replace
						(" over", "");
				  }
			}
		}

		navPat = document.getElementById("menu_patrimonio");
		for (i=0; i<navPat.childNodes.length; i++) {
			node = navPat.childNodes[i];
			if (node.nodeName=="LI") {
				node.onmouseover=function() {
					this.className+=" over";
				}
				  node.onmouseout=function() {
					  this.className=this.className.replace
						(" over", "");
				  }
			}
		}

		navCom = document.getElementById("menu_compras");
		for (i=0; i<navCom.childNodes.length; i++) {
			node = navCom.childNodes[i];
			if (node.nodeName=="LI") {
				node.onmouseover=function() {
					this.className+=" over";
				}
				  node.onmouseout=function() {
					  this.className=this.className.replace
						(" over", "");
				  }
			}
		}

		navCom = document.getElementById("menu_bolsas");
		for (i=0; i<navCom.childNodes.length; i++) {
			node = navCom.childNodes[i];
			if (node.nodeName=="LI") {
				node.onmouseover=function() {
					this.className+=" over";
				}
				  node.onmouseout=function() {
					  this.className=this.className.replace
						(" over", "");
				  }
			}
		}
	}
}


//
// FUN?ES PARA TAG sipac:textArea
//
/**
 * Fun??o prepara um textArea para contagem de caracteres para
 * posterior valida??o.
 * - Seta o m?todo checkMaxLength para chamada onkeyup e onchange do textArea
 * passado como parametro.
 * - Seta a o m?todo validateTextAreas
 * para a chamada onsubmit do form se este n?o estiver setado.
 *
 * @param textArea objeto textArea a ser limitado
 * @see br.ufrn.sipac.arq.tags.TextareaTag
 * @author Rafael Borja
 */
function setMaxLength(textArea, validar)
{
	var contadorDiv = document.getElementById(textArea.id + '.contador');
		if (textArea.getAttribute('maxlength'))
		{
			contadorDiv.innerHTML = '<span>0</span>/'+textArea.getAttribute('maxlength');

			textArea.contador = contadorDiv.getElementsByTagName('span')[0];
			textArea.divContador = contadorDiv;
			textArea.onkeyup = textArea.onchange = checkMaxLength;

			if (validar == true)
				textArea.form.onsubmit = validateTextAreas;

			textArea.onkeyup();
		}
}

/**
 * Fun??o para contagem de caracteres em um textArea
 *
 * @see br.ufrn.sipac.arq.tags.TextareaTag
 * @author Rafael Borja
 */
function checkMaxLength()
{
	var currentLength = this.value.length;
	var maxLength = this.getAttribute('maxlength');

	// Formata valor exibido (vermelho se ultrapassar maxlength
	if (currentLength > maxLength)
		this.divContador.style.color= 'red';
	else
		this.divContador.style.color= 'black';

	// Altera o valor exibido
	this.contador.tamanhoAtual = currentLength;
	currentLength = currentLength + '';
	for (i=0; i < maxLength.length && currentLength.length < maxLength.length; i++)
		currentLength = '0' + currentLength;

	this.contador.firstChild.nodeValue = currentLength;
}

/**
 * Fun??o valida o tamanho de todos os texts areas que possuem
 * o atributo maxlength.
 *
 * @see setMaxLength
 * @see checkMaxLength
 * @auhor Rafael Borja
 */
function validateTextAreas()
{
	var x;
	try {
		x = form.getElementsByTagName('textarea');
	} catch (e) {
		form = this;
		x = form.getElementsByTagName('textarea');
	}

	for (var i=0;i<x.length;i++)
	{
		var tamanhoMax = x[i].getAttribute('maxlength');
		if (x[i].contador.tamanhoAtual > tamanhoMax) {
			alert('Texto ultrapassa tamanho permitido');
			return false;
		}
		else
			i = i;
	}
	return true;
}

function calculaDiarias(idCampo) {
	var strDataEntrada = document.getElementById('dataEntrada').value;
	var strDataSaida = document.getElementById('dataSaida').value;

	if (strDataEntrada.length == 10 && strDataSaida.length == 10)  {
		strDataEntrada = strDataEntrada.substr(3,2) + "/" + strDataEntrada.substr(0,2) + "/" + strDataEntrada.substr(6,4);
		strDataSaida = strDataSaida.substr(3,2) + "/" + strDataSaida.substr(0,2) + "/" + strDataSaida.substr(6,4);

		var entradaMs = Date.parse(strDataEntrada);
		var saidaMs = Date.parse(strDataSaida);
		var dataEntrada = new Date(entradaMs);
		var dataSaida = new Date(saidaMs);

		var day = 1000*60*60*24;
		var qtdeDiarias = (dataSaida - dataEntrada)/day + 1;
		if (qtdeDiarias > 0) {
			var campo = document.getElementById(idCampo);
			campo.value = qtdeDiarias;
		} else {
			alert("A data de entrada deve ser anterior ? data de sa?da!");
		}
	}
}

//
// FIM DE FUN??ES PARA TAG sipac:textArea
//


Object.extend(Field,  {
	check : function(element) {
		var field = $(element);
		if (field.type == 'checkbox' || field.type == 'radio') {
			field.checked = true;
		}
	}
});

function apenasNumeros(campo, event) {
     var point = '.';
     var comma = ',';
     var sep = 0;
     var key = '';
     var i = j = 0;
     var len = len2 = 0;
     var strCheck = '0123456789';
     var aux = aux2 = '';
     var rcode = event.which ? event.which : event.keyCode;

     if (teclasEspeciais.indexOf(rcode) != -1) {
          return true; // Teclas especiais
     }

     if (rcode >= 96 && rcode <= 105)
        rcode -= 48; // Teclado numérico, código diferente

     key = String.fromCharCode(rcode); // Pega o valor da tecla pelo código

     if (strCheck.indexOf(key) == -1){
          return false; // Filtra teclas inválidas
     }
     return true;
}


/**
 * Função utilizada para a leitura de Memorando Eletrônico (popup).
 * 
 * @param idMemorandoEletronico
 */
function lerMemorandoEletronico(idMemorandoEletronico) {
	window.open('/sipac/protocolo/memorando_eletronico/memorando_eletronico.jsf?idMemorandoEletronico='+idMemorandoEletronico,'','width=800,height=600, scrollbars');
}

/**
 * Função utilizada para visualizar as leituras realizadas em um Memorando Eletrônico (popup).
 * 
 * @param idMemorandoEletronico
 */
function leiturasMemorandoEletronico(idMemorandoEletronico) {
	window.open('/sipac/protocolo/memorando_eletronico/cadastro/leituras_memorando_eletronico.jsf?idMemorandoEletronico='+idMemorandoEletronico,'','width=800,height=600, scrollbars');
}

/**
 * Função utilizada para visualizar as movimentações realizadas em um Documento (popup).
 * 
 * @param idDocumento
 */
function documentoDetalhado(idDocumento) {
	window.open('/sipac/protocolo/consulta/info_documento.jsf?idDoc='+idDocumento,'','width=800,height=500, scrollbars');
}

/**
 * Função utilizada para visualizar um processo detalhadamente (popup).
 * 
 * @param idProcesso
 */
function processoDetalhado(idProcesso) {
	window.open('/sipac/protocolo/processo/processo_detalhado.jsf?id='+idProcesso,'','width=800,height=600, scrollbars');
}

/**
 * Função utilizada para visualizar os detalhes de um empenho (popup).
 * 
 * @param numero
 * @param ano
 * @param idUnidadeGestora
 */
function consultaEmpenho(numero, ano, idUnidadeGestora) {
	//ConstantesAction.BUSCAR == 13 (acao)
	window.open('/sipac/consultaEmpenho.do?numero='+numero+'&ano='+ano+'&idUnidadeGestora='+idUnidadeGestora+'&acao=13&popup=true', '', 'width=780,height=600, scrollbars=yes');
}

/**
 * Função utilizada para visualizar uma notificação de fornecedor.
 * 
 * @param id
 */
function imprimeNotificacaoFornecedor(id) {
	//TiposCmdLiquidacao.IMPRIME_NOTIFICACAO == 110042 (acao)
	window.open('/sipac/imprimeNotificacao.do?id='+id+'&acao=110042', '', 'width=700,height=450,left=120,top=300, scrollbars');
}

