function calculaMedia(element, musica) {
	var metodologia = getEl('metodologiaEad').dom.value;

	var tdElement = element.parentNode;
	var trElement = tdElement.parentNode;
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
	var notaTutor = labels[1]; 
	var labelElement = labels[2]; 
	var situacaoElement = labels[3];

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
	
		mediaFinal = (((nota1 + nota2)/2)*pesoProfessor + parseFloat(notaTutor.innerHTML.replace(',','.'))*pesoTutor) / (pesoProfessor+pesoTutor);
	}
	
	mediaFinal = Math.round(mediaFinal*10)/10;
	
	labelElement.innerHTML = (isNaN(mediaFinal) ? '--' : mediaFinal.toString().replace('.',','));
	situacaoElement.innerHTML = calculaSituacao(mediaFinal, recuperacao, mediaFinal, null);
	var img = '<img src="/sigaa/img/consolidacao/situacao_%.png"/>';
	if (situacaoElement.innerHTML.toLowerCase() != '--')
		situacaoImg.innerHTML = img.substring(0, img.indexOf('%')) + situacaoElement.innerHTML.toLowerCase() + img.substring(img.indexOf('%')+1);

}