
function calculaTotal(grupo) {
	var notas = getEl(document).getChildrenByClassName('nota');
	var notasGrupo = getEl(document).getChildrenByClassName('nota_' + grupo);

	var elGrupo = getEl('total_' + grupo);
	var elTotal = getEl('total');

	var nota = 0.0;
	for (i = 0; i < notasGrupo.length; i++) {
		var valor = notasGrupo[i].dom.value.replace(',','.')
		if (valor == '') valor = 0;
		nota += parseFloat(valor);
	}

	var total = 0.0;
	for (i = 0; i < notas.length; i++) {
		var valor = notas[i].dom.value.replace(',','.')
		if (valor == '') valor = 0;
		total += parseFloat(valor);
	}

	elGrupo.dom.innerHTML = (Math.round(nota*100)/100).toString().replace('.',',');
	elTotal.dom.innerHTML = (Math.round(total*100)/100).toString().replace('.',',');
}