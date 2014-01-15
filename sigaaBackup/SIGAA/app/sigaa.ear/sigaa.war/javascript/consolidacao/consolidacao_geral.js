
function calculaMedia(element,isEvent,musica) {	
	var tdElement = null;
	var trElement = null;
	
	if ( isEvent ){
		tdElement = element.parentNode;
		trElement =tdElement.parentNode;
	} else
		trElement = element[0];
	
	var somaNotas = 0.0;
	var somaPesos = 0;
	var recuperacao = 0.0;
	var isRecuperacao = false;
	var isExibir = true;
	$A(trElement.getElementsByTagName('input')).each(
		function(item) {
		
			if (item.className != 'recuperacao' && item.className != 'faltas' && item.className != 'faltas-calc' && item.className.indexOf('avaliacao') == -1 )	{
				valor = parseFloat(item.value.replace(',','.')); 
				if (isNaN(valor)) {
					isExibir = false;
					valor = 0.0;
				}	
				peso = parseFloat(item.className.replace(',','.'));
				somaNotas += valor * 10.0 * peso;
				somaPesos += peso;
			} else if (item.className == 'recuperacao') {
				isRecuperacao = true;
				recuperacao = parseFloat(item.value.replace(',','.'));
				if (isNaN(recuperacao))
					recuperacao = null;
			}
		}
	);

	var labels = trElement.getElementsByTagName('label');

	var situacaoImg = labels[0];
	var labelElement = labels[1];
	var situacaoElement = getEl(trElement).getChildrenByClassName('situacao')[0];
	var recuperacaoElement = getEl(trElement).getChildrenByClassName('recuperacao')[0];	
	var faltasElement = getEl(trElement).getChildrenByClassName('faltas')[0];	
	var mediaUnidades = (somaNotas / (somaPesos * 10.0));
	
	var numFaltas = '0';
	
	if (faltasElement != null)  {
		numFaltas = faltasElement.dom.value;
	}
	else {
		numFaltas = '0';
	}
	
	
	///////////////////////////////////////////////////////
	//   PARA RESOLVER OS PROBLEMAS DE PONTO FLUTUANTE,  //
	// OS VALORES FORAM MULTIPLICADOS E DIVIDIDOS POR 10 //
	///////////////////////////////////////////////////////
	
	mediaUnidades = Math.round(mediaUnidades * 10.0) / 10.0;
	
	var mediaFinal = mediaUnidades;
	
	if (isRecuperacao && recuperacao != null) {
		if (musica) {
			mediaFinal = (mediaUnidades * 10.0 * 2 + recuperacao * 10.0 * 3) / 50.0;
		} else {
			mediaFinal = ((mediaUnidades * ( pesoMedia * 10.0 ) + recuperacao * ( pesoRecuperacao * 10.0) )) / (( pesoMedia + pesoRecuperacao )*10.0);
		}
	}
	
	mediaFinal = Math.round(mediaFinal * 10.0) / 10.0;
	
	labelElement.innerHTML = (isNaN(mediaFinal) || !isExibir ? '--' : mediaFinal.toString().replace('.',','));	
	var situacaoSemRecuperacaoFaltas = !isExibir ? '--' : calculaSituacao(mediaUnidades, null, mediaFinal, 0);
	situacaoElement.dom.innerHTML = !isExibir ? '--' : calculaSituacao(mediaUnidades, recuperacao, mediaFinal, parseInt(numFaltas));
	
	if( (situacaoSemRecuperacaoFaltas == 'APR' || situacaoSemRecuperacaoFaltas == 'REP' || situacaoElement.dom.innerHTML == 'REPF' || situacaoElement.dom.innerHTML == 'REMF' ) && element.className != 'recuperacao' && element.className != 'faltas-calc' ) {
		// Lato ead não possui recuperação.
		if ( !isNaN(recuperacaoElement) )
			recuperacaoElement.dom.value = '';
		recuperacao = null;
		labelElement.innerHTML = mediaUnidades;
		mediaFinal = mediaUnidades;
		situacaoElement.dom.innerHTML = !isExibir ? '--' : calculaSituacao(mediaUnidades, recuperacao, mediaFinal, parseInt(numFaltas));
	}
	
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	if (situacaoElement.dom.innerHTML.toLowerCase() != '--') {
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.dom.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		if ( situacaoElement.dom.innerHTML == 'APR' ){
			situacaoElement.dom.title = "Aprovado";
			situacaoImg.title = "Aluno Aprovado";
		}
		else if ( situacaoElement.dom.innerHTML == 'REC' ){
			situacaoElement.dom.title = "Em recuperação";
			situacaoImg.title = "Aluno em Recuperação";
		}	
		else if ( situacaoElement.dom.innerHTML == 'REP' ){
			situacaoElement.dom.title = "Reprovado por notas";
			situacaoImg.title = "Aluno Reprovado";
		}
		else if ( situacaoElement.dom.innerHTML == 'REPF' ){
			situacaoElement.dom.title = "Reprovado por faltas";
			situacaoImg.title = "Aluno Reprovado";
		}
		else if ( situacaoElement.dom.innerHTML == 'REMF'){
			situacaoElement.dom.title = "Reprovado por notas e faltas";
			situacaoImg.title = "Aluno Reprovado";
		}
	} else {
		situacaoElement.innerHTML = "";
		situacaoElement.dom.title = "--";
		situacaoImg.innerHTML = "";
	}	
	
	
	if (recuperacaoElement) {
		if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && situacaoElement.dom.innerHTML != 'REPF' && isExibir) {
			$(recuperacaoElement.dom).setStyle({
				  backgroundColor: '#FFF'
			});
			recuperacaoElement.dom.disabled = false;
		} else {
			$(recuperacaoElement.dom).setStyle({
				  backgroundColor: '#E0DFE3',
				  borderColor: '#AAAAAA'
			});
			recuperacaoElement.dom.disabled = true;
		}
	}
}

function formataFaltas(campo, event) {

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

     key = String.fromCharCode(rcode); // Pega o valor da tecla pelo c?digo

     if (strCheck.indexOf(key) == -1){
          return false; // Filtra teclas inv?lidas
     }

     return true;
}

function enableRecuperacao() {
	var situacoes = getEl(document).getChildrenByClassName('situacao');
	for (i = 0; i < situacoes.length; i++) {
		var situacaoElement = situacoes[i]; 
		var recuperacaoElement = getEl(situacaoElement.dom.parentNode.parentNode.parentNode).getChildrenByClassName('recuperacao')[0];
		if (recuperacaoElement != null) {
			if (situacaoElement.dom.innerHTML == 'REC') {
				$(recuperacaoElement.dom).setStyle({
					  backgroundColor: '#FFF'
				});
				recuperacaoElement.dom.disabled = false;
			} else if (recuperacaoElement.dom.value == null || recuperacaoElement.dom.value == '') {
				$(recuperacaoElement.dom).setStyle({
					  backgroundColor: '#E0DFE3',
					  borderColor: '#AAAAAA'
				});
				recuperacaoElement.dom.disabled = true;
			} else {
				$(recuperacaoElement.dom).setStyle({
					  backgroundColor: '#FFF'
				});
				recuperacaoElement.dom.disabled = false;
			}
		}
	}
}

YAHOO.ext.EventManager.onDocumentReady(enableRecuperacao, document, true);

function verificaFaltasMaiorTotal(element) {
	var total = parseInt(element.value);
	var maximo = parseInt(getEl('maxFaltasTotal').dom.value);
	if (total > maximo) {
		
		var msg = 'Número de faltas inválido. O número de faltas máximo é ' + maximo + '.';
		if (maximo == 0)
			msg = 'Esta turma não possui ch de aula. O número de faltas máximo permitido é ' + maximo + '.';
		alert(msg);
		element.value = '';
	}
}

function situacaoApto(elem,isEvent) {
	var trElement = null;
	if (isEvent)
		trElement = elem.parentNode.parentNode;
	else
		trElement = elem[0];
	var select = trElement.getElementsByTagName('select')[0];
	var labels = trElement.getElementsByTagName('label');
	var situacaoImg = labels[0];
	var situacaoElement = labels[1];
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	
	var value = select.options[select.selectedIndex].value;
	var apr = false;
	
	if (value == 'true') {
		apr = true;
		situacaoElement.innerHTML = 'APR';
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Aprovado";
	} else if (value == 'false') {
		situacaoElement.innerHTML = 'REP';
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Reprovado";
	} else {
		situacaoElement.innerHTML = '--';
		situacaoImg.innerHTML = '--';
	}
	
	var faltasElement = getEl(trElement).getChildrenByClassName('faltas')[0];
	if (faltasElement != null && parseInt(faltasElement.dom.value) > parseInt(getEl('maxFaltas').dom.value)) {
		situacaoElement.innerHTML = 'REPF';
		if ( !apr )	situacaoElement.innerHTML = 'REMF';
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Reprovado";
	}

	
}

function situacaoConceito(elem,isEvent) {
	var trElement = null;
	
	if ( isEvent )
		trElement = elem.parentNode.parentNode;
	else
		trElement = elem[0];
		
	var labels = trElement.getElementsByTagName('label');
	var faltasElement = getEl(trElement).getChildrenByClassName('faltas')[0];
	var faltas = parseInt(faltasElement.dom.value);
	var situacaoImg = labels[0];
	var situacaoElement = labels[1];
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	var combo = getEl(trElement).getChildrenByClassName('comboConceito')[0];
	var value = combo.dom.options[combo.dom.selectedIndex].value;
	
	var repFaltas = false;
	
	if (faltas != null && faltas > getEl('maxFaltas').dom.value)
		repFaltas = true;

	if (value >= mediaMinimaPassarPorMedia && !repFaltas) {
		situacaoElement.innerHTML = 'APR';
		situacaoElement.title = "Aprovado";
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Aprovado";
	} else if (value < mediaMinimaPassarPorMedia && value > 0 && !repFaltas) {
		situacaoElement.innerHTML = 'REP';
		situacaoElement.title = "Reprovado";
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Reprovado";
	} else if (value < mediaMinimaPassarPorMedia && value > 0 && repFaltas) {
		situacaoElement.innerHTML = 'REMF';
		situacaoElement.title = "Reprovado por notas e faltas";
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Reprovado";
	} else if (value >= mediaMinimaPassarPorMedia && repFaltas) {
		situacaoElement.innerHTML = 'REPF';
		situacaoElement.title = "Reprovado por faltas";
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		situacaoImg.title = "Aluno Reprovado";
	} else {
		situacaoElement.innerHTML = '--';
		situacaoImg.innerHTML = '--';
	}
}

function transferir(elem) {
	var linha = getEl(elem.parentNode.parentNode);
	var elOrigem  = linha.getChildrenByClassName('faltas-calc')[0];
	var elDestino = linha.getChildrenByClassName('faltas')[0];
	elDestino.dom.value = elOrigem.dom.value;
}

function transferirTodos() {
	var els = getEl('notas-turma').getChildrenByClassName('img-transferir');
	for (var i = 0; i < els.length; i++) {
		els[i].dom.onclick();
	}
}

function exibirNota(linha, conceito, competencia, musica) {

	if (conceito) situacaoConceito(linha,false);
	
	if (competencia) situacaoApto(linha,false);
	
	if (!conceito && !competencia) calculaMedia(linha,false,musica);
}
