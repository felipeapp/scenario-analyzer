J = jQuery.noConflict();
function habilitarDetalhes(idDiscente) {
	var linha = 'linha_'+ idDiscente;
	if ( J('#'+linha).css('display') == 'none' ) {
		if (/msie/.test( navigator.userAgent.toLowerCase() ))
			J('#'+linha).css('display', 'inline-block');
		else
			J('#'+linha).css('display', 'table-cell');			
		
		if (J('#'+linha).html() != null) {
			if (document.getElementById('form:indicator_'+idDiscente) != null)
			   document.getElementById('form:indicator_'+idDiscente).style.display = 'block';
			
			new Ajax.Request("/sigaa/graduacao/detalhes_discente.jsf?idDiscente=" + idDiscente, {
				onComplete: function(transport) {
					J('#'+linha).html(transport.responseText);
					J('#'+linha).addClass('populado');
					if (document.getElementById('form:indicator_'+idDiscente) != null)
						document.getElementById('form:indicator_'+idDiscente).style.display = 'none';
				}
			});			
		}
	} else {
		J('#'+linha).css('display', 'none');		
	}
}