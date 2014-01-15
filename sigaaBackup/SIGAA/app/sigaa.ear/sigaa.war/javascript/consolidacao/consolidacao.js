var mediaMinimaPossibilitaRecuperacao = parseFloat(getEl('mediaMinimaPossibilitaRecuperacao').dom.value);
var mediaMinima = parseInt(getEl('mediaMinima').dom.value);
var mediaMinimaPassarPorMedia = parseInt(getEl('mediaMinimaPassarPorMedia').dom.value);
var mediaMinimaAprovacao = parseInt(getEl('mediaMinimaAprovacao').dom.value);

var pesoMedia = parseInt(getEl('pesoMedia').dom.value);
var pesoRecuperacao = parseInt(getEl('pesoRecuperacao').dom.value);
var permiteTutor = null;
if (getEl('permiteTutor') != null)
	permiteTutor = getEl('permiteTutor').dom.value;


// Verifica se a nota digitada é maior que 10. Se for, invalida.
function verificaNotaMaiorDez(element) {
	verificaNotaMaior(element, 10.0, null, true);
}

//Verifica se a nota digitada é maior que 10. Se for, invalida.
function verificaNotaMaior(element, maximo, tipo, todosTipos) {
	var valor = parseFloat(element.value.replace(',','.'));
	
	// Deve fazer a verificação apenas se for soma das notas ou se for chamada para
	//	verificar se a nota digitada é maior que 10.0
	var verifica = tipo == 'S' || todosTipos;
	
	if ( verifica ){
		if (valor > maximo) {
			alert('Nota inválida. As notas devem estar entre 0 e ' + maximo + '.');
			element.value = '';
		}
	}
}

// Destaca a linha da matrícula cuja nota está sendo digitada.
function destacarOnFocus() {
	var inputs = getEl(document).getChildrenByTagName('input');
	for (var i = 0; i < inputs.length; i++) {
		if (inputs[i].dom.type == 'text') {
			inputs[i].on('focus', function(event, element) { element.dom.parentNode.parentNode.className += ' alunoSelecionado' });
			inputs[i].on('blur', function(event, element) { 
				var tr = element.dom.parentNode.parentNode;
				tr.className = tr.className.substring(0, tr.className.indexOf('alunoSelecionado')); 
			});
		}
	}
}

// Calcula a situação 
function calculaSituacao(mediaUnidades, recuperacao, mediaFinal, faltas) {
	var repFaltas = false;
	
	if (faltas != null && faltas > getEl('maxFaltas').dom.value)
		repFaltas = true;

	if (mediaUnidades >= mediaMinimaPassarPorMedia && !repFaltas) return 'APR';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao == null && !repFaltas) return 'REC';
	else if (mediaUnidades < mediaMinimaPossibilitaRecuperacao && !repFaltas) return 'REP';
	else if (mediaUnidades < mediaMinimaPossibilitaRecuperacao && repFaltas) return 'REMF';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao != null && mediaFinal >= mediaMinima && !repFaltas) return 'APR';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao != null && mediaFinal < mediaMinima && !repFaltas) return 'REP';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao != null && mediaFinal < mediaMinima && repFaltas) return 'REMF';
	else if (repFaltas) return 'REPF';
	else return '--';
}

// Soma o valor dos campos de avaliação
function somaAvaliacoes(idUnidade, tipo, musica, element, maximo) {
	var somaNotas = 0.0; 
	var somaPesos = 0.0;
	
	// Como o IE não implementa o método document.getElementsByClassName é nescessário fazer um tratamento.
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
			if (tipo == 'A') {
				somaPesos += 1;
				
				valor = parseFloat(item.value.replace(',', '.'));
				somaNotas += valor;
			} else if (tipo == 'P') {
				var peso = parseInt(Element.readAttribute(item, 'peso'));
				somaPesos += peso;
				
				valor = parseFloat(item.value.replace(',', '.'));
				somaNotas += (valor * peso);
			} else if (tipo == 'S') {
				valor = parseFloat(item.value.replace(',', '.'));
				somaNotas += valor;
			}
		}	
	);

	if ( isNaN(somaNotas) )
		nota = "";	
	else{
		if (tipo == 'A') {
			somaNotas /= somaPesos;
		} else if (tipo == 'P') {
			somaNotas /= somaPesos;		
		} 
		
		somaNotas = Math.round(somaNotas*10)/10;
		if (somaNotas > 10) somaNotas = 10.0;
		
		var nota = somaNotas.toString().replace('.', ',');
				
		if (nota.indexOf(',') == -1)
			nota += ',0';

		if (tipo == 'S') {
			var valor = parseFloat(element.value.replace(',','.'));
			if (valor > maximo)
				nota = '';
		} 
	}	
	$('nota_' + idUnidade).value = nota;
	calculaMedia($('nota_' + idUnidade),true,musica);
}

YAHOO.ext.EventManager.onDocumentReady(destacarOnFocus, document, true);
