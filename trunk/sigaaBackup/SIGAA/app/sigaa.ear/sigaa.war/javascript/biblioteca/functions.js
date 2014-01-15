

/** Seleciona e deseleciona  os checkbox referentes ao campo passado dependendo do tipo do campo. 
 * Chamar no evento: onchange
 */ 
function marcarCheckBox(campo, idCheckBox){
	
	checkBox = getEl(idCheckBox);
	
	if(campo.type == 'text')
		marcarCheckBoxCampoTexto(campo, checkBox);
	
	if(campo.type == 'select-one')
		marcarCheckBoxCampoSelectOne(campo, checkBox);
	
	if(campo.type == 'select-multiple')
		marcarCheckBoxCampoSelectMultiple(campo, checkBox);
	
}

/** Realiza a seleção e deseleção para campos de texto onde o valor vazio do campo é uma string vazia */
function marcarCheckBoxCampoTexto(campo, checkBox){
	if(campo.value == '')
		checkBox.dom.checked = false;
	else
		checkBox.dom.checked = true;
}

/** Realiza a seleção e deseleção para campos do combobox onde o valor vazio é um número menor que zero */
function marcarCheckBoxCampoSelectOne(campo, checkBox){
	if(campo.value < 0)
		checkBox.dom.checked = false;
	else
		checkBox.dom.checked = true;
}

/** Realiza a seleção e deseleção para campos do combobox onde o valor vazio é um número menor que zerou uma string vazia */
function marcarCheckBoxCampoSelectMultiple(campo, checkBox){
	if(campo.value == '' || campo.value < 0)
		checkBox.dom.checked = false;
	else
		checkBox.dom.checked = true;
}

// função que executa o click no botao passado quando o usuário pressiona o enter
function executaClickBotao(evento, idBotao) {
	
	var tecla = "";
	if (isIe())
		tecla = evento.keyCode;
	else
		tecla = evento.which;

	if (tecla == 13){
		getElementoById(idBotao).click();
		return false;
	}
	
	return true;
	
}	

// testa se é o IE ou não
function isIe() {
	return (typeof window.ActiveXObject != 'undefined');
}
			

