var Tabs = {
    init : function(){
        this.tabs = new Ext.TabPanel('abas-turma');
        this.tabs.addTab('aulas-turma', "T&oacute;picos");
		this.tabs.activate('aulas-turma');
    }
}
YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);


