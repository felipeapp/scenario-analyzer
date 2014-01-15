

// ARQUIVO COM FUNCOES JAVA SCRIPT QUE MOSTRAM OS DETALHES DOS EXEMPLARES E FASCICULOS NOS RESULTADOS DAS BUSCAS //


// funcao utilizada para abilitar a visualizacao dos detalhes de um exemplar em especifico //
//
// @param idLinha o id da linha onde se quer inserir os dados com javascript
// @param idExemplar o exemplar cujos dados vao ser mostrados na pagina inserida via javascript
//

function habilitarDetalhesExemplar(idLinha, idExemplar) {
	var linha = 'linha_'+ idLinha; // o id da linha da tabela
	var linhaImagem = 'imagem_' + idLinha; // o id da imagem usada no link que habilita os detalhes
	
	if ( $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()
		if ( !Element.hasClassName(linha, 'populado') ) {
			
			
			new Ajax.Request("/sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesExemplar.jsf?idExemplar=" + idExemplar, {
				onComplete: function(transport) {
					$(linha).innerHTML = transport.responseText;
					Element.addClassName(linha, 'populado');
				}
			});
			
		}
		
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$(linha).style.display = 'inline-block';
		}else{
			$(linha).style.display = 'table-cell';
		}
		
		//$(linha).style.display = 'table-cell';
		$(linhaImagem).src= '/sigaa/img/biblioteca/cima.gif';
		$(linhaImagem).title = 'Ocultar Informações Completas do Exemplar';
	} else {
		$(linha).style.display = 'none';
		$(linhaImagem).src= '/sigaa/img/biblioteca/baixo.gif';
		$(linhaImagem).title = 'Mostrar Informações Completas do Exemplar';
	}
}


//função utilizada para habilitar a visualização dos detalhes de um fascículo em especifico //
//
// @param idLinha o id da linha onde se quer inserir os dados com javascript
// @param idFasciculo o fascículo cujos dados vão ser mostrados na pagina inserida via javascript
//
// OBS.: as vezes o idLinha eh igual o idItem.

function habilitarDetalhesFasciculo(idLinha, idFasciculo) {
	var linha = 'linha_'+ idLinha; // o id da linha da tabela
	var linhaImagem = 'imagem_' + idLinha; // o id da imagem usada no link que habilita os detalhes
	
	if (   $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block'  ) {       //$() == getElementById()
		if ( !Element.hasClassName(linha, 'populado') ) {
			
			new Ajax.Request("/sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesFasciculo.jsf?idFasciculo=" + idFasciculo, {
				onComplete: function(transport) {
					$(linha).innerHTML = transport.responseText;
					Element.addClassName(linha, 'populado');
				}
			});
			
		}
		
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$(linha).style.display = 'inline-block';
		}else{
			$(linha).style.display = 'table-cell';
		}
		
		$(linhaImagem).src= '/sigaa/img/biblioteca/cima.gif';
		$(linhaImagem).title = 'Ocultar Informações Completas do Fascículo';
	} else {
		$(linha).style.display = 'none';
		$(linhaImagem).src= '/sigaa/img/biblioteca/baixo.gif';
		$(linhaImagem).title = 'Mostrar Informações Completas do Fascículo';
	}
}





// carrega a página de detalhes dos artigo do fascículo.
function habilitarDetalhesArtigo(idLinha, idArtigo) {
	
	var linha = 'linha_'+ idLinha;           // o id da linha da tabela
	var linhaImagem = 'imagem_' + idLinha;  // o id da imagem usada no link que habilita os detalhes
	
	if (  $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()

		if ( !Element.hasClassName(linha, 'populado') ) {
		
			new Ajax.Request("/sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesArtigos.jsf?idArtigoDePeriodico=" + idArtigo, {
				onComplete: function(transport) {
					$(linha).innerHTML = transport.responseText;
					Element.addClassName(linha, 'populado');
				}
			});
			
		}
		
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$(linha).style.display = 'inline-block';
		}else{
			$(linha).style.display = 'table-cell';
		}
		
		$(linhaImagem).src= '/sigaa/img/biblioteca/cima.gif';
		$(linhaImagem).title = 'Ocultar Informações Completas do Artigo';
	} else {
		$(linha).style.display = 'none';
		$(linhaImagem).src= '/sigaa/img/biblioteca/baixo.gif';
		$(linhaImagem).title = 'Mostrar Informações Completas do Artigo';
	}
}










