/**
Painel para exibir a lista de solicitacoes de matricula de um discente
**/

var PainelSolicitacoesMatricula = (function() {
	var painel;
	return {
        show : function(id, exibirOrientacoes){
        		if( !exibirOrientacoes ){
        			exibirOrientacoes = false;
        		}
	        	var p = getEl('painel-solicitacoes');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-solicitacoes", {
	   	 		   autoCreate: true,
				   title: 'Solicitações de Matrícula do Discente',
                   proxyDrag: true,
                   constraintoviewport: false,
	               width: 700,
	               height: 330,
	               resizable: true
            	});

	       	 	painel.show();

				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/graduacao/solicitacao_matricula/painel_solicitacoes.jsf?id=' + id + '&exibirOrientacoes=' + exibirOrientacoes,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando solicitações de matrícula...'
					 });
        }
	};
})();
