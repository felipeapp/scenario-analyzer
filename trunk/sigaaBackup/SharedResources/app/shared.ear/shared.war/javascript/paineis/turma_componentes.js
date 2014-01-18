/**
Paineis para exibir detalhes sobre turmas e componentes curriculares.
**/

var PainelTurma = (function() {
	var painel;
	return {
        show : function(id){
	        	var p = getEl('painel-turma');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-turma", {
	   	 		   autoCreate: true,
				   title: 'Informações da Turma',
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
					 url: '/sigaa/graduacao/turma/view_painel.jsf?ajaxRequest=true&contarMatriculados=true&id='+id,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando informações da turma...'
					 });
        }
	};
})();

var PainelComponente = (function() {
	var painel;
	return {
        show : function(id){
			var p = getEl('painel-componente');
        	if (p) {
        		p.remove();
        	}

    	 	painel = new YAHOO.ext.BasicDialog("painel-componente", {
   	 			autoCreate: true,
				title: 'Informações do Componente Curricular',
                proxyDrag: true,
                constraintoviewport: false,
                width: 750,
                height: 480,
                resizable: true
			});

       	 	painel.show();

			var um = painel.body.getUpdateManager();
			um.disableCaching = false;
			um.loadScripts = true;
			um.update({
				 url: '/sigaa/graduacao/componente/view_painel.jsf?ajaxRequest=true&id='+id,
				 discardUrl: true,
				 nocache: true,
				 text: 'Aguarde! Carregando informações do componente curricular...'
			});
        }
	};
})();
