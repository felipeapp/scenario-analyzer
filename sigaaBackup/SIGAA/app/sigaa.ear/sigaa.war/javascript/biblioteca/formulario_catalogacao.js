
// CALCULA O TAMANHO DAS TEXTAREAS DA PÁGINA NO MOMENTO QUE ELA É CARRREGADA.

// Só depois que a página é carregada que jQuery está definido
window.onload = function() {
	resize_AllTextArea(false);
};






//Funçao que percorre todas as text áreas da página e calcula o tamanho de cada uma
//dependendo da quantidade de caracteres e linha de texto de cada uma
function resize_AllTextArea(fullScreen){

	var J = jQuery.noConflict(); 
	
	J(document).ready(function (){
		J("textarea").each(function (){
	
			var qtdEnter = 0;
			var textArea = J(this);
			
			t = textArea.val().split('\n');
	
			for (var i = 0; i< t.length; i++){ // quantida de quebra de linhas do text
					qtdEnter++;
			}
	
			var tamanhoMaximoTexto = getTamanhoMaxioTextoTextArea(fullScreen);
			var qtdLinhaInteira = 0;
			
			t = parseInt( textArea.val().length / tamanhoMaximoTexto);
	
			for (var i = 0; i < t ; i++){ // quantidade de vezes que passou o tamanho máximo da linha
				qtdLinhaInteira++;
			}
	
		
			
			///////////////////////////////////////////////////////////////////////////////////////
			// formula de crescimento que melhor teve resultado para o crescimento da textArea.  //
			// Quanto a quantidade de texto tende ao inifino, a quantidade de linha da textArea  //
			// fica um pouco a mais do que o necessário, mas no uso normal não dá para notar.    //
			///////////////////////////////////////////////////////////////////////////////////////
			textArea.attr('rows',  calculaQuantidadeLinhasTextArea(qtdEnter, qtdLinhaInteira, fullScreen)  );
			
		});
	});

}



//
// Função JQuery para simular o click em um link da página.
//
// Clique é realizado no link que tiver a classe css passada
//
function simulaClickLink(classeDoLink){
	var J = jQuery.noConflict(); 
	J(classeDoLink).trigger('click');
	return false;
}



function focaTagEtiqueta(campo){
	setTimeout(focaCampo(campo), 500);
}

function focaCampo(campo){
	campo.focus();
}	


// funcação usado para quando o usuário clicar nos campos de indicadores o sistema já selecionar 
// o texto, para ele saber que existe um indicar ali, pois o valor padrão é espaço em branco.
// e também para ele não precisar apagar para digitar um novo valor.
function selecionaValorCampo(campo){
	campo.focus();
	campo.select();
}



// funcao que aumenta a text area dos dado da catalogacao de acordo com
// as quebra de linha do texto digitado            
// tem que ser chamada no evento onkeyup para funcionar.                                         
function resize_HTMLTextArea(textArea, fullScreen) {
	
    var qtdEnter = 0;
	t = textArea.value.split('\n');

	for (var i = 0; i< t.length; i++){ // quantida de quebra de linhas do text
			qtdEnter++;
	}

	var tamanhoMaximoTexto = getTamanhoMaxioTextoTextArea(fullScreen);
	var qtdLinhaInteira = 0;
	
	t = parseInt( textArea.value.length / tamanhoMaximoTexto);

	for (var i = 0; i < t ; i++){ // quantidade de vezes que passou o tamanho máximo da linha
		qtdLinhaInteira++;
	}

	textArea.rows =  calculaQuantidadeLinhasTextArea(qtdEnter, qtdLinhaInteira, fullScreen);
	
}

// O tamanho de texto dentro da textArea
function getTamanhoMaxioTextoTextArea(fullScreen){
	return fullScreen ? 60 : 50;
}


///////////////////////////////////////////////////////////////////////////////////////
// formula de crescimento que melhor teve resultado para o crescimento da textArea.  //
// Quanto a quantidade de texto tende ao inifino, a quantidade de linha da textArea  //
// fica um pouco a mais do que o necessário, mas no uso normal não dá para notar.    //
///////////////////////////////////////////////////////////////////////////////////////
function calculaQuantidadeLinhasTextArea(qtdEnter, qtdLinhaInteira, fullScreen){
	return (qtdEnter + 3) + (qtdLinhaInteira - parseInt(qtdLinhaInteira / ( fullScreen ? 2.5 : 3) ));
}



//Método que clica do botão da ação quando o usuário preciona a tecla "ENTER" em algum campo de texto
//Deve ser chamado no evento onkeypress
function realizaAcaoTeclaEnter(elemento, evento, idBotaoAcao) {
	var tecla = "";
	if (isIe())
		tecla = evento.keyCode;
	else
		tecla = evento.which;

	if (tecla == 13){
		document.getElementById(idBotaoAcao).click();
		elemento.focus(); // foca o campo que precionou ENTER
		return false;
	}
	
	return true;
	
}	

// Verifica se o navegador é o IE
function isIe() {
	return (typeof window.ActiveXObject != 'undefined');
}

