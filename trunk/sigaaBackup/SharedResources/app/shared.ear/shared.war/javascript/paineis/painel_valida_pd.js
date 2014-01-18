// Painel com form de validação de produção intelectual

var PainelValidacao = (function() {
	var painel;

	var imagem_validado = '/sigaa/img/prodocente/accept.png';
	var imagem_invalidado = '/sigaa/img/prodocente/cancel.png';
	var imagem_pendente = '/sigaa/img/prodocente/help.png';

	return {

        show : function(url, somenteLeitura, imagem){
        	var p = getEl('painel-validacao');

        	if (p)
        		p.remove();
   	 		painel = new YAHOO.ext.BasicDialog("painel-validacao", {
   	 			autoCreate: true,
				title: 'Validação de Produções Intelectuais',
                modal: false,
                width: 740,
                height: 400,
                shadow: false,
                resizable: true
            });

            painel.addButton('Cancelar', painel.hide, painel);
            if(!somenteLeitura){
	            if (imagem.src.indexOf(imagem_validado) == -1 ) {
		            painel.addButton('VALIDAR PRODUÇÃO',
		            			function validar() { // FUNCAO QND CLICAR NO BOTAO.
									var id = url.substr(url.lastIndexOf('?id=')+4).substr('&');
									var ajax = new Ajax.Request('/sigaa/ajaxValidacaoProducao',
										{ method: 'get',
											parameters: 'id=' + id + '&validado=true',
											onSuccess: function(t) {
												painel.hide();
												//$('formFiltragem').submit();
												$('divMsg').style.display = "block";
												$('divMsg').innerHTML = t.responseText;
												imagem.src = '/sigaa/img/prodocente/accept.png';
											},
											onFailure: function(t) {
												painel.hide();
												$('divMsg').style.display = "block";
												$('divMsg').innerHTML = t.responseText;
											}
										});
								}, painel);
				}

				if( imagem.src.indexOf(imagem_invalidado) == -1 ) {
		            painel.addButton('INVALIDAR PRODUÇÃO',
		            			function invalidar() { // FUNCAO QND CLICAR NO BOTAO.
									var id = url.substr(url.lastIndexOf('?id=')+4).substr('&');
									var ajax = new Ajax.Request('/sigaa/ajaxValidacaoProducao',
										{ method: 'get',
											parameters: 'id=' + id + '&validado=false',
											onSuccess: function(t) {
												painel.hide();
												$('divMsg').style.display = "block";
												$('divMsg').innerHTML = t.responseText;
												imagem.src = '/sigaa/img/prodocente/cancel.png';
											},
											onFailure: function(t) {
												painel.hide();
												$('divMsg').style.display = "block";
												$('divMsg').innerHTML = t.responseText;
											}
										});
								}, painel);
	            }
		     }

       	 	painel.show();

			var um = painel.body.getUpdateManager();
			um.disableCaching = false;
			um.update({
				 url: url,
				 discardUrl: true,
				 nocache: true,
				 text: 'CARREGANDO PRODUÇÃO...'
				 });

        }

	};
})();



