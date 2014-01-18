
var PainelFactory = function() {

	var create = function(title, containerId, className, panelId, width, height, urlBuilder) {
		var painel = new PainelProtocolos();
		painel.init(title, containerId, className, panelId, width, height, urlBuilder);
	}

	return {
		
		init : function() {
		
			create('Dados do Documento', 'info-documento', 'detalhes', "documento-panel", 600, 300, function(el) {
				return '/sipac/protocolo/processo/info_documento.jsf?posicao=' + el.id.substring(10) + '&ajaxRequest=true';
			});
			
			create('Dados do Documento', 'info-cons-documento', 'detalhes-doc', "documento-panel", 600, 300, function(el) {
				return '/sipac/protocolo/consulta/info_documento.jsf?idDoc=' + el.id.substring(10) + '&ajaxRequest=true';
			});
			
			create('Dados do Processo', 'info-processo', 'detalhes-processo', "processo-panel", 800, 450, function(el) {
				return '/sipac/protocolo/processo/info_processo.jsf?posicao=' + el.id.substring(9) + '&ajaxRequest=true';
			});
			
			create('Dados do Processo', 'info-proc', 'detalhes-proc', "processo-proc-panel", 800, 450, function(el) {
				return '/sipac/protocolo/processo/info_processo_proc.jsf?posicao=' + el.id.substring(9) + '&ajaxRequest=true';
			});
		}
		
	}

}();

var PainelProtocolos = (function() {
	var painel, link;
	var title, containerId, className, panelId, urlBuilder, width, height;

	return {
		init : function(_title, _containerId, _className, _panelId, _width, _height, _urlBuilder) {
			this.title = _title;
			this.containerId = _containerId;
			this.className = _className;
			this.panelId = _panelId;
			this.urlBuilder = _urlBuilder;
			this.width = _width;
			this.height = _height;
		
			link = getEl(_containerId);
			if (link != undefined)
				link.on('click', this.show, this, true);
		},
        show : function(event){
	        var el = event.target ? event.target : event.srcElement;
	    	if (el.className != this.className) {
        		return false;
        	}

       	 	if (!painel) {
       	 		painel = new YAHOO.ext.BasicDialog(this.panelId, {
       	 			autoCreate: true,
					title: this.title,
                    modal: false,
                    animateTarget: this.containerId,
                    width: this.width,
                    height: this.height,
                    shadow: false,
                    resizable: false
                });
                painel.addKeyListener(27, painel.hide, painel);
       	 	}
       	 	painel.show();

			var mgr = painel.body.getUpdateManager();
			mgr.update(this.urlBuilder(el));
			YAHOO.ext.DialogManager.bringToFront(painel);
        }
	};
});

YAHOO.ext.EventManager.onDocumentReady(PainelFactory.init, PainelFactory, true);

