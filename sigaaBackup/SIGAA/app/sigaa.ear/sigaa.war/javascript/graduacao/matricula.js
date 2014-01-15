//include('/sigaa/javascript/cssQuery.js');


var PainelConsultaTurmas = (function() {
	var painel;
	return {
        show : function(expressao){
	        	var p = getEl('painel-turma-consulta-turmas');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-turma-consulta-turmas", {
	   	 		   autoCreate: true,
				   title: 'Turmas Abertas Encontradas',
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
					 url: '/sigaa/graduacao/matricula/painel_turmas_abertas.jsf?expressao='+expressao,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando Turmas...'
					 });
        }
	};
})();


var PainelAjudaMatricula = (function() {
	var painel;
	return {
        show : function(){
	        	var p = getEl('painel-ajuda');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-ajuda", {
	   	 		   autoCreate: true,
				   title: 'Ajuda de Matrícula Online',
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
					 url: '/sigaa/graduacao/matricula/ajuda.jsf',
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando Ajuda...'
					 });
        }
	};
})();

var AbasMatricula = {
    init : function(){
    	if (getEl('abas-matricula')) {
	        var abas = new YAHOO.ext.TabPanel('abas-matricula');
	        abas.addTab('disciplinas', "Disciplinas Selecionadas");
	        abas.addTab('horarios', "Quadro de Horários");
	        abas.activate('disciplinas');
        }
    }
};

var PainelDetalhesTurma = function() {
	var painel, links;

	return {
		init : function() {
			links = getEl('lista-turmas',true);
			if (links) {
				getEl('lista-turmas',true).on('click', this.show, this, true);
			}
		},
        show : function(event){
        	var el = event.target ? event.target : event.srcElement;
        	if (el.nodeName !== 'A') {
        		return false;
        	}

        	if (!painel) {
       	 		painel = new YAHOO.ext.BasicDialog("painel-detalhes-turma", {
       	 			autoCreate: true,
					title: 'Detalhes da Turma',
                    modal: false,
                    width: 540,
                    height: 400,
                    shadow: true,
                    resizable: false,
                    proxyDrag: true
                });
                painel.addKeyListener(27, painel.hide, painel);
       	 	}
       	 	painel.animateTarget = el;
			painel.show();
       	 	painel.setTitle('Turma ' + el.id.substring(5));
       	 	YAHOO.ext.DialogManager.bringToFront(painel);
        }
	};
}();

YAHOO.ext.EventManager.onDocumentReady(AbasMatricula.init, AbasMatricula, true);
YAHOO.ext.EventManager.onDocumentReady(PainelDetalhesTurma.init, PainelDetalhesTurma, true);

function bloquearSubmissao() {
	$('formBotoesSuperiores:botaoSubmissao').onclick = null;
	$('formBotoesSuperiores:linkSubmissao').onclick = null;
	$('formBotoesSuperiores:linkSubmissao').href = 'javascript:void(0);';
}