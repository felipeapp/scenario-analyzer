


/*
 * Função que carrega utilizando ajax os detalhes do usuário que fez a solicitação da reserva do Espaço Físico
 */
function habilitarDetalhesUsuarioSolicitante(idReservaEspacoFisico){

	var linha = 'linha_'+ idReservaEspacoFisico; // o id da linha da tabela
	
	if ( $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()
		if ( !Element.hasClassName(linha, 'populado') ) {
			
			new Ajax.Request("/sigaa/infra_fisica/paginaDetalhesUsuarioSolicitanteReserva.jsf?idReservaEspacoFisico=" + idReservaEspacoFisico, {
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
		
		
	} else {
		$(linha).style.display = 'none';
	}
}

