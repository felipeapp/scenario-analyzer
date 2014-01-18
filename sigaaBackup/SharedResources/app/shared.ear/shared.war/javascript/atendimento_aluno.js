var PainelLerResposta = (function() {
	var painel;
	return {
        show : function(id){
	        	var p = getEl('painel-lerResposta');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-lerResposta", {
	   	 		   autoCreate: true,
				   title: 'Detalhes da Resposta',
                   proxyDrag: true,
                   constraintoviewport: false,
	               width: 700,
	               height: 330,
	               resizable: false
            	});

	       	 	painel.show();

				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/geral/atendimento_aluno/tela_ler_resposta.jsf?ajaxRequest=true&id=' + id,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando a pergunta...'
					 });
        }
	};
})();
var PainelCriarPergunta = (function() {
	var painel;
	return {
        show : function(){
	        	var p = getEl('painel-criarPergunta');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-criarPergunta", {
					autoCreate: true,
					title: 'Atendimento ao Aluno',
					proxyDrag: true,
					constraintoviewport: false,
					width: 700,
					height: 330,
					resizable: false
				});

				painel.show();
			
				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/geral/atendimento_aluno/form.jsf?ajaxRequest=true',
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Abrindo formulario...'
			 	});
		}
	};
})();