// Converte um numero(texto) em Double, troca as ',' por '.' e não permite que seja copiado texto no input
function comma2Point(texto){
	var t = parseFloat(texto.value.replace(',','.'));
	if(isNaN(t)){
		texto.value = '0.0';
	}else {
		texto.value = t;
	}
}

// Verifica se a nota digitada é maior que 10. Se for, invalida.
function verificaNotaMaiorDez(element) {
	verificaNotaMaior(element, 10.0);
}

// Verifica se a nota digitada é maior que 10. Se for, invalida.
function verificaNotaMaior(element, maximo) {
	var valor = parseFloat(element.value.replace(',','.'));
	if (valor > maximo) {
		alert('Nota inválida. As notas devem estar entre 0 e ' + maximo + '.');
		element.value = '0.0';
	}
}