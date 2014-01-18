var Tabs = function(){
	var tabs;

	return {
	    init : function(){
	        tabs = new YAHOO.ext.TabPanel('abas-ead');
	    },
		adicionarAbas : function() {
		   	tabs.addTab('tutoria-ead', "Tutoria");
		   	tabs.addTab('cadastros-ead', "Cadastros");
			tabs.addTab('relatorios-ead', "Relatórios");
	
			tabs.activate('tutoria-ead');
		},
		ativar : function(aba) {
			tabs.activate(aba);
		}
	};
}();
YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
YAHOO.ext.EventManager.onDocumentReady(Tabs.adicionarAbas, Tabs, true);