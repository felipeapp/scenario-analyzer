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
	
	if ( isEvent ) {
		tdElement = element.parentNode;
		trElement = tdElement.parentNode;
	} else
		trElement = element[0];
	
	var notas = new Array();
	var recuperacao = 0.0;

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
	var labelElement = labels[5]; 
	var situacaoElement = labels[6];

	var pesoTutor = parseInt(getEl('pesoTutor').dom.value);
	var pesoProfessor = parseInt(getEl('pesoProfessor').dom.value);
	
	var mediaFinal = 0;
	if (metodologia == 'true') {
		
		var notaProfessor = notas[0];
				
		// Cálculo da nota do professor com o peso
		var notaPeso = Math.round((notaProfessor * pesoProfessor)/10) / 10;
		notaUnidade.innerHTML = isNaN(notaPeso) ? '' : (notaPeso).toString().replace('.',',');
		
		// Cálculo da nota final da unidade somada com a nota do tutor
		var tutorNota = parseFloat(notaTutor.innerHTML.replace(',','.'));
		if ( isNaN(notaPeso) || isNaN(tutorNota) || 
			notaUnidade.innerHTML == '' || pesoTutorLabel.innerHTML == '' )
			somaNota.innerHTML = '';
		else
			somaNota.innerHTML = Math.round((notaPeso+((tutorNota*pesoTutor)/100))*10)/10;
		
		if ( isNaN(tutorNota) ) {
			pesoTutorLabel.innerHTML = '';
			tutorNota = 0.0;
		}
		// Cálculo da média final
		if (notaProfessor < recuperacao) notaProfessor = recuperacao;
		mediaFinal = (notaProfessor*pesoProfessor + tutorNota*pesoTutor) / (pesoProfessor+pesoTutor);
	} else {
		var nota1 = notas[0];
		var nota2 = notas[1];
	
		if (recuperacao != null && nota1 > nota2 && recuperacao > nota2) {
			nota2 = recuperacao;
		} else if (recuperacao != null && nota1 <= nota2 && recuperacao > nota1) {
			nota1 = recuperacao;
		}
	
		mediaFinal = (((nota1 + nota2)/2)*pesoProfessor + parseFloat(notaTutor.innerHTML.replace(',','.'))*pesoTutor) / (pesoProfessor+pesoTutor);
	}
	
	mediaFinal = Math.round(mediaFinal*10)/10;
	
	labelElement.innerHTML = (isNaN(mediaFinal) ? '--' : mediaFinal.toString().replace('.',','));
	situacaoElement.innerHTML = calculaSituacao(mediaFinal, recuperacao, mediaFinal, null);
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	if (situacaoElement.innerHTML.toLowerCase() != '--'){
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
		situacaoImg.innerHTML = "";
		situacaoElement.title = "";
	}	
}

function calculaMediaSemTutor(element,isEvent,musica) {
	var metodologia = getEl('metodologiaEad').dom.value;
	var tdElement = null;
	var trElement = null;
	
	if ( isEvent ) {
		tdElement = element.parentNode;
		trElement = tdElement.parentNode;
	} else
		trElement = element[0];
	
	var notas = new Array();
	var recuperacao = 0.0;

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

//	var pesoTutor = parseInt(getEl('pesoTutor').dom.value);
	var pesoProfessor = parseInt(getEl('pesoProfessor').dom.value);
	
	var mediaFinal = 0;
		
	var notaProfessor = notas[0];
				
	// Cálculo da nota do professor com o peso
	var notaPeso = Math.round((notaProfessor * pesoProfessor)/10) / 10;

	// Cálculo da média final
	if (notaProfessor < recuperacao) notaProfessor = recuperacao;
	mediaFinal = (notaProfessor*pesoProfessor) / (pesoProfessor);
	mediaFinal = Math.round(mediaFinal*10)/10;
	
	labelElement.innerHTML = (isNaN(mediaFinal) ? '--' : mediaFinal.toString().replace('.',','));
	situacaoElement.innerHTML = calculaSituacao(mediaFinal, recuperacao, mediaFinal, null);
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	if (situacaoElement.innerHTML.toLowerCase() != '--'){
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
		situacaoImg.innerHTML = "";
		situacaoElement.title = "";
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