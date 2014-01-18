/*Funcoes para exibir ou ocultar elementos da tabela*/

	function pegarClasse(clazz, key) {
		var retorno;

		$A(clazz.split(' ')).each(
			function(el) {
				if (el.indexOf(key) != -1) {
					retorno = el;
				}
			}
		);

		return retorno;
	}


	function exibirItens(clazz){
		var linhas = $A(document.getElementsByClassName(clazz));

		linhas.each(
			function(el) {
				if (Element.visible(el)) {
					$('seta_' + clazz).className = 'abrir';
					Element.hide(el);
				} else {
					$('seta_' + clazz).className = 'fechar';
					Element.show(el);
				}
			}
		);
	}

	function esconder(clazz, nomeID) {

		var linhas = $A(document.getElementsByClassName('escondido'));
		linhas.each(
			function(el) {

				if (clazz == 'elementoAberto') {
					Element.hide(el);
					$('seta_' + pegarClasse(el.className, nomeID)).className = 'abrir';
				} else {
					if (el.className.indexOf(clazz) == -1) {
						Element.hide(el);
						$('seta_' + pegarClasse(el.className, nomeID)).className = 'abrir';
					} else{
						Element.show(el);
						$('seta_' + pegarClasse(el.className, nomeID)).className = 'fechar';
					}
				}
			}
		);

	}

/*FIM: Funcoes para exibir ou ocultar elementos da tabela*/


/* Função para setar a aba atual de algum menu */
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
function setAbas(aba, subAba) {
	document.getElementById('aba').value = aba;
	document.getElementById('subAba').value = subAba;
}
/* FIM: Função para setar a aba atual de algum menu */

/* Abrir painel em DIV */
	function abrirPainelDiv(div, size) {
		dialog = YAHOO.ext.DialogManager.get(div);
		$(div).style.display='block';
		if(!size) size = 400;
		if ( !dialog ) {
			dialog = new YAHOO.ext.BasicDialog(div, {
			        modal:false,
			        width:size,
			        height:320,
			        shadow:true,
			        minWidth:400,
			        minHeight:0,
			        proxyDrag: true
			});
			dialog.addKeyListener(27, dialog.hide, dialog);
			dialog.addButton('Fechar Painel', dialog.hide, dialog);
		}
		dialog.show();
		YAHOO.ext.DialogManager.bringToFront(div);
	}
/* FIM: Abrir painel em DIV */

function formatarInteiro(campo, evt, keypressing) {
	if (keypressing) {
		var charCode = (evt.which) ? evt.which : event.keyCode
		if (charCode > 31 && (charCode < 48 || charCode > 57))
			return false;
		return true;
	} else {
		//var caretPos = doGetCaretPosition(campo);
		campo.value = campo.value.replace(/\D+/, '');
		//setCaretPosition(campo, caretPos);
	}
}

function CAPS(obj){
	var caretPos = doGetCaretPosition(obj);
	obj.value = obj.value.toUpperCase();
	setCaretPosition(obj, caretPos);
}

function doGetCaretPosition (ctrl) {
	var caretPos = 0;	// IE Support
	if (document.selection) {
		ctrl.focus ();
		var sel = document.selection.createRange();
		sel.moveStart ('character', -ctrl.value.length);
		caretPos = sel.text.length;
	} else if (ctrl.selectionStart || ctrl.selectionStart == '0') // Firefox support
		caretPos = ctrl.selectionStart;
	return caretPos;
}

function setCaretPosition(ctrl, pos){
	if(ctrl.setSelectionRange) {
		ctrl.focus();
		ctrl.setSelectionRange(pos,pos);
	} else if (ctrl.createTextRange) {
		var range = ctrl.createTextRange();
		range.collapse(true);
		range.moveEnd('character', pos);
		range.moveStart('character', pos);
		range.select();
	}
}
function exibirOpcoes(idTr){
	var linha = 'trOpcoes'+ idTr;
	$(linha).toggle();
}

function intdiv(op1, op2) {
	return (op1 / op2 - op1 % op2 / op2);
}
	
var Relogio = function() {
	var hora, min;
	
	return {
		init: function(tempo) {
			if ($('tempoSessao')) { $('tempoSessao').innerHTML = '<small><em>Tempo de Sessão:</em> <span id="spanRelogio" title="Tempo restante para a expiração da sessão."></span></small>'; }
			min = tempo;
			hora = intdiv(min, 60);
			min = min % 60;

			if (hora.toString().length == 1) hora = "0" + hora;
			if (min.toString().length == 1) min = "0" + min;
			if ($('spanRelogio')) { $('spanRelogio').innerHTML = hora + ":" + min; }
			new PeriodicalExecuter(this.show, 60);
		},
		show: function() {
			if((hora > 0) || (min > 0)){
	        	if(min == 0){
	        		min = 59;
	        		hora = hora - 1
	            } else {
	            	min = min - 1;
				}

	            if(hora.toString().length == 1){
	            	hora = "0" + hora;
	            }

	            if(min.toString().length == 1){
	            	min = "0" + min;
	            }

	            if ($('spanRelogio')) { 
	            	if (hora == 0 && min == 0) $('spanRelogio').innerHTML = 'Sessão Expirada';
	            	else $('spanRelogio').innerHTML = hora + ":" + min; 
	            }
			}
		}
	}
}();

function removeAccents(strAccents){
    strAccents = strAccents.split('');
    strAccentsOut = new Array();
    strAccentsLen = strAccents.length;
    var accents = 'ÀÁÂÃÄÅàáâãäåÒÓÔÕÕÖØòóôõöøÈÉÊËèéêëğÇçĞÌÍÎÏìíîïÙÚÛÜùúûüÑñŠšŸÿı';
    var accentsOut = ['A','A','A','A','A','A','a','a','a','a','a','a','O','O','O','O','O','O','O','o','o','o','o','o','o','E','E','E','E','e','e','e','e','e','C','c','D','I','I','I','I','i','i','i','i','U','U','U','U','u','u','u','u','N','n','S','s','Y','y','y','Z','z'];
    for (var y = 0; y < strAccentsLen; y++) {
        if (accents.indexOf(strAccents[y]) != -1) {
            strAccentsOut[y] = accentsOut[accents.indexOf(strAccents[y])];
        }
        else
            strAccentsOut[y] = strAccents[y];
    }
    strAccentsOut = strAccentsOut.join('');
    return strAccentsOut;
}