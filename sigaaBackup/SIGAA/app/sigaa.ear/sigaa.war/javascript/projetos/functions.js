// Converte um numero(texto) em Double, troca as ',' por '.' e n�o permite que seja copiado texto no input
function comma2Point(texto){
	var t = parseFloat(texto.value.replace(',','.'));
	if(isNaN(t)){
		texto.value = '0.0';
	}else {
		texto.value = t;
	}
}

// Verifica se a nota digitada � maior que 10. Se for, invalida.
function verificaNotaMaiorDez(element) {
	verificaNotaMaior(element, 10.0);
}

// Verifica se a nota digitada � maior que 10. Se for, invalida.
function verificaNotaMaior(element, maximo) {
	var valor = parseFloat(element.value.replace(',','.'));
	if (valor > maximo) {
		alert('Nota inv�lida. As notas devem estar entre 0 e ' + maximo + '.');
		element.value = '0.0';
	}
}