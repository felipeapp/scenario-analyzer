
function calcularNota(event) {
	var obj = event.target ? event.target : event.srcElement;
	calcular(obj.className.substring(5));
}

function calcular(idDisciplina) {
	var media = getEl('media_' + idDisciplina);
	var notas = getEl(document).getChildrenByClassName('nota_' + idDisciplina);

	var nota = 0.0;
	for (var i = 0; i < notas.length; i++) {
		var valor = notas[i].dom.value.replace(',','.');
		if (valor == '') valor = 0.0;
		nota += parseFloat(valor);
	}
	
	nota /= notas.length;
	media.dom.innerHTML = (Math.round(nota*10)/10).toString().replace('.',',');	
}

function iniciar() {
	var notas = getEl(document).getChildrenByTagName('input');
	for (var i = 0; i < notas.length; i++) {
		var input = notas[i].dom;
		if (input.type == 'text') {
			notas[i].on('keyup', calcularNota);
		}
	}
	
	var medias = getEl(document).getChildrenByClassName('media');
	for (var i = 0; i < medias.length; i++) {
		var media = medias[i].dom;
		calcular(media.id.substring(6));
	}
}

YAHOO.ext.EventManager.onDocumentReady(iniciar, this, true);