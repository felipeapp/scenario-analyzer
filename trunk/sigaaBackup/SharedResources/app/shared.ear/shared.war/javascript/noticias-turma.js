var PainelNoticias = (function() {
	var painel, link;

	return {
		init : function() {
			link = getEl('noticias-turma');
			if (link != undefined)
				link.on('click', this.show, this, true);
		},
        show : function(event){
	        var el = event.target ? event.target : event.srcElement;
        	if (el.nodeName !== 'A') {
        		return false;
        	}

       	 	if (!painel) {
       	 		painel = new YAHOO.ext.BasicDialog("painel-noticia", {
       	 			autoCreate: true,
					title: 'Notícia da Turma',
                    modal: false,
                    animateTarget: 'noticias-turma',
                    width: 615,
                    height: 345,
                    shadow: true,
                    resizable: false
                });
                painel.addKeyListener(27, painel.hide, painel);
       	 	}
       	 	painel.show();

			var mgr = painel.body.getUpdateManager();
			mgr.update('/sigaa/portais/turma/NoticiaTurma/view.jsf?ajaxRequest=true&id=' + el.id.substring(8));
			YAHOO.ext.DialogManager.bringToFront(painel);

        }
	};
})();

YAHOO.ext.EventManager.onDocumentReady(PainelNoticias.init, PainelNoticias, true);