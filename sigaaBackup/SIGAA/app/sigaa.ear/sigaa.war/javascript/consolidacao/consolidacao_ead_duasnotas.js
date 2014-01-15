function calculaMedia(element,isEvent,musica) {
	if (permiteTutor == 'true')
		calculaMediaTutor(element,isEvent,musica)
	else
		calculaMediaSemTutor(element,isEvent,musica)
}


function calculaMediaTutor(element,isEvent,musica) {
	var metodologia = getEl('metodologiaEad').dom.value;

	var tdElement = null;
	var trElement = null;
	
	if ( isEvent ){
		tdElement = element.parentNode;
		trElement = tdElement.parentNode;
	}else
		trElement = element[0];
	
	var notas = new Array();
	var recuperacao = 0.0;

	var isExibirNota = true;
	
	var i = 0;
	$A(trElement.getElementsByTagName('input')).each(
		function(item) {
			if (item.className != 'recuperacao' && item.className != 'faltas' && item.className.indexOf('avaliacao') == -1 )	{
				valor = parseFloat(item.value.replace(',','.')); 
				notas[i++] = valor; 
			} else if (item.className == 'recuperacao') {
				recuperacao = parseFloat(item.value.replace(',','.'));
				if (isNaN(recuperacao))
					recuperacao = null;
			}
		}
	);

	var labels = trElement.getElementsByTagName('label');

	var situacaoImg = labels[0];
	var notaUnidade = labels[1];
	var notaTutor = labels[2]; 
	var pesoTutorLabel = labels[3];
	var somaNota = labels[4]; 
	var notaUnidade2 = labels[5];
	var notaTutor2 = labels[6];
	var pesoTutor2Label = labels[7];
	var somaNota2 = labels[8]; 
	var labelElement = labels[9]; 
	var situacaoElement = labels[10];
	
	var pesoTutor = parseInt(getEl('pesoTutor').dom.value);
	var pesoProfessor = parseInt(getEl('pesoProfessor').dom.value);
	
	var mediaFinal = 0;
	if (metodologia == 'true') {
		mediaFinal = (notas[0]*pesoProfessor + parseFloat(notaTutor.innerHTML.replace(',','.'))*pesoTutor) / (pesoProfessor+pesoTutor);
	} else {
		var nota1 = notas[0];
		var nota2 = notas[1];
		
		if (recuperacao != null && nota1 > nota2 && recuperacao > nota2) {
			nota2 = recuperacao;
		} else if (recuperacao != null && nota1 <= nota2 && recuperacao > nota1) {
			nota1 = recuperacao;
		}
		
		var nota1Peso = Math.round((nota1 * pesoProfessor)/10) / 10;
		notaUnidade.innerHTML = isNaN(nota1Peso) ? '' : (nota1Peso).toString().replace('.',',');
		if (isNaN(nota1Peso)) nota1Peso = 0.0;
		var nota2Peso = Math.round((nota2 * pesoProfessor)/10) / 10;
		notaUnidade2.innerHTML = isNaN(nota2Peso) ? '' : (nota2Peso).toString().replace('.',',');
		if (isNaN(nota2Peso)) nota2Peso = 0.0; 
	
		var notaTutor1Peso = parseFloat(notaTutor.innerHTML.replace(',','.'))*pesoTutor / 100;
		if (isNaN(notaTutor1Peso)){
			pesoTutorLabel.innerHTML = '';
			notaTutor1Peso = 0.0;
		}	
		var notaTutor2Peso = parseFloat(notaTutor2.innerHTML.replace(',','.'))*pesoTutor / 100;
		if (isNaN(notaTutor2Peso)){
			pesoTutor2Label.innerHTML = '';
			notaTutor2Peso = 0.0;
		}
		mediaFinal = ((nota1Peso + notaTutor1Peso) + (nota2Peso + notaTutor2Peso)) / 2.0;  
	}
	
	mediaFinal = Math.round(mediaFinal*10)/10;
	
	isExibirNota = isNaN(mediaFinal) || notaUnidade.innerHTML == '' || notaUnidade2.innerHTML == '' ? false : true;
	
	labelElement.innerHTML = (!isExibirNota ? '--' : mediaFinal.toString().replace('.',','));
	situacaoElement.innerHTML = !isExibirNota ? '--' : calculaSituacao(mediaFinal, recuperacao, mediaFinal, null);
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	if (situacaoElement.innerHTML.toLowerCase() != '--') {
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		if ( situacaoElement.innerHTML == 'APR' ){
			situacaoElement.title = "Aprovado";
			situacaoImg.title = "Aluno Aprovado";
		}
		else if ( situacaoElement.innerHTML == 'REP' ){
			situacaoElement.title = "Reprovado";
			situacaoImg.title = "Aluno Reprovado";
		} 	
	} else {
		situacaoElement.innerHTML = "";
		situacaoImg.innerHTML = "";
	}	
	
	if ( isNaN(nota1Peso) || isNaN(notaTutor1Peso) || 
		notaUnidade.innerHTML == '' || pesoTutorLabel.innerHTML == '' )
		somaNota.innerHTML = '';
	else
		somaNota.innerHTML = Math.round((nota1Peso+notaTutor1Peso)*10)/10;
	
	if ( isNaN(nota2Peso) || isNaN(notaTutor2Peso) || 
		notaUnidade2.innerHTML == '' || pesoTutor2Label.innerHTML == '' )
		somaNota2.innerHTML = '';
	else
		somaNota2.innerHTML = Math.round((nota2Peso + notaTutor2Peso)*10)/10;
}

function calculaMediaSemTutor(element,isEvent,musica) {

	var tdElement = null;
	var trElement = null;
	
	if ( isEvent ){
		tdElement = element.parentNode;
		trElement = tdElement.parentNode;
	}else
		trElement = element[0];
	
	var notas = new Array();
	var recuperacao = 0.0;

	var isExibirNota = true;
	
	var i = 0;
	$A(trElement.getElementsByTagName('input')).each(
		function(item) {
			if (item.className != 'recuperacao' && item.className != 'faltas' && item.className.indexOf('avaliacao') == -1 )	{
				valor = parseFloat(item.value.replace(',','.')); 
				notas[i++] = valor; 
			} else if (item.className == 'recuperacao') {
				recuperacao = parseFloat(item.value.replace(',','.'));
				if (isNaN(recuperacao))
					recuperacao = null;
			}
		}
	);

	var labels = trElement.getElementsByTagName('label');

	var situacaoImg = labels[0];
	var labelElement = labels[1]; 
	var situacaoElement = labels[2];
	
	var pesoProfessor = parseInt(getEl('pesoProfessor').dom.value);
	
	var mediaFinal = 0;
	
	var nota1 = notas[0];
	var nota2 = notas[1];
	
	if (recuperacao != null && nota1 > nota2 && recuperacao > nota2) {
		nota2 = recuperacao;
	} else if (recuperacao != null && nota1 <= nota2 && recuperacao > nota1) {
		nota1 = recuperacao;
	}
	
	var nota1Peso = Math.round((nota1 * pesoProfessor)/10) / 10;
	if (isNaN(nota1Peso)) nota1Peso = 0.0;
	var nota2Peso = Math.round((nota2 * pesoProfessor)/10) / 10;
	if (isNaN(nota2Peso)) nota2Peso = 0.0; 
	mediaFinal = ((nota1Peso) + (nota2Peso)) / 2.0;  
	
	mediaFinal = Math.round(mediaFinal*10)/10;
	
	isExibirNota = !isNaN(mediaFinal) && !isNaN(nota1) && !isNaN(nota2);
	
	labelElement.innerHTML = (!isExibirNota ? '--' : mediaFinal.toString().replace('.',','));
	situacaoElement.innerHTML = !isExibirNota ? '--' : calculaSituacao(mediaFinal, recuperacao, mediaFinal, null);
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	if (situacaoElement.innerHTML.toLowerCase() != '--') {
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);
		if ( situacaoElement.innerHTML == 'APR' ){
			situacaoElement.title = "Aprovado";
			situacaoImg.title = "Aluno Aprovado";
		}
		else if ( situacaoElement.innerHTML == 'REP' ){
			situacaoElement.title = "Reprovado";
			situacaoImg.title = "Aluno Reprovado";
		} 	
	} else {
		situacaoElement.innerHTML = "--";
		situacaoImg.innerHTML = "";
	}	
}


//Calcula a situação 
function calculaSituacao(mediaUnidades, recuperacao, mediaFinal, faltas) {
	var repFaltas = false;
	
	if (faltas != null && faltas > getEl('maxFaltas').dom.value)
		repFaltas = true;

	if (mediaUnidades >= mediaMinimaAprovacao && !repFaltas) return 'APR';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaAprovacao && recuperacao == null && !repFaltas) return 'REC';
	else if (mediaUnidades < mediaMinimaPossibilitaRecuperacao && !repFaltas) return 'REP';
	else if (mediaUnidades < mediaMinimaPossibilitaRecuperacao && repFaltas) return 'REMF';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao != null && mediaFinal >= mediaMinima && !repFaltas) return 'APR';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao != null && mediaFinal < mediaMinima && !repFaltas) return 'REP';
	else if (mediaUnidades >= mediaMinimaPossibilitaRecuperacao && mediaUnidades < mediaMinimaPassarPorMedia && recuperacao != null && mediaFinal < mediaMinima && repFaltas) return 'REMF';
	else if (repFaltas) return 'REPF';
	else return '--';
}

function exibirNota(linha, conceito, competencia, musica) {
	calculaMedia(linha,false,musica);
}
