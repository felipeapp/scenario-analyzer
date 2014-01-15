
var removerPasta = function(id) {
	Ext.MessageBox.confirm('Confirmar', 'Deseja realmente remover essa pasta e todo o seu conte&uacute;do? ' +
							'Caso algum arquivo da pasta esteja associado a uma turma ou comunidade virtual, ' +
							'tais arquivos também serão removidos.', function(btn) {
		var esconder = false;
		if (btn == 'yes') {
			if (!id) {
				id = Ext.get('remover-pasta-pai').dom.value;
				esconder = true;
			}

			var mgr = new Ext.UpdateManager('dummyDiv');
			mgr.disableCaching = true;
			mgr.update('/sigaa/removerPasta.do?acao=remover&pasta.id=' + id);
	
			mgr.on('update', function(element, response) {
				var obj = eval('(' + response.responseText + ')');
	    		Arvore.delNode(obj);
	    	});
			Ext.MessageBox.alert('Sucesso', 'Pasta excluida com sucesso.');
		}
		
	});
}

Ext.MessageBox.buttonText.yes = "Sim";
Ext.MessageBox.buttonText.no = "Não";

var PainelNovaPasta = function() {
	var painel;

	var formSubmit = function() {
		var id = Ext.get('cadastrar-pasta-pai').dom.value;
		var nome = Ext.get('cadastrar-pasta-nome').dom.value;
		
		if (Ext.get('cadastrar-pasta-nome').dom.value == ''){ 
			Ext.MessageBox.alert('Erro', 'Digite o nome da nova pasta.')
		}
		else
		{
			var mgr = new Ext.UpdateManager('dummyDiv');
			mgr.disableCaching = true;
			mgr.update('/sigaa/cadastrarPasta.do?acao=cadastrar&pasta.nome=' + nome + '&pasta.pai.id=' + id);		
			
			mgr.on('failure', function(oElement, oResponseObject) { 
				Ext.MessageBox.alert('Erro', 'Erro ao criar nova pasta.') 
			});
			
			mgr.on('update', function(oElement, oResponseObject) {
			var obj = eval('(' + oResponseObject.responseText + ')');
			Arvore.addNode(obj);
			Ext.MessageBox.alert('Sucesso', 'Pasta criada com sucesso.');
			painel.hide();
			});
		}
	}

	return {
		
        show : function(nodeid){
	       if (!painel) {
       	 		painel = new Ext.Window({
       	 			title: 'Criar nova pasta',
                    animateTarget: 'btn-nova-pasta',
                    width: 450,
                    height: 130,
                    shadow: false,
                    resizable: false,
                    closable: false,
                    buttons: [{
                    	text: 'Criar',
                    	handler: function(){ formSubmit(); }
                	},{
                  	  	text:'Fechar',
                    	handler: function() { painel.hide(); }
                	}],
                	keys: [{
                		key: 27,
                		handler: function() { painel.hide() }
                	}]
                });
       	 	}
       	 	
       	 	painel.show();
       	 	
       	 	var mgr = new Ext.UpdateManager(painel.body);
       	 	mgr.disableCaching = true;
       	 	mgr.indicatorText = 'Carregando...';
			mgr.update('/sigaa/ava/PortaArquivos/cadastrarPasta.jsf');
			mgr.on('update', function() {
				if (nodeid) {
        			var options = Ext.get('cadastrar-pasta-pai').dom.options;
					for (var i = 0; i < options.length; i++) {
						if (options[i].value == nodeid) {
							Ext.get('cadastrar-pasta-pai').dom.selectedIndex = i;
							break;
						}
					}
        		}
        		
        		Ext.get('cadastrar-pasta-nome').on('keypress', function(e) {
        			if (e.getKey() == Ext.EventObject.ENTER) {
        				e.stopEvent();
        			}
        		});        		
			});
        }       
        
	};
}();

var PainelRemoverPasta = function() {
	var painel;

	var formSubmit = function() {
		removerPasta();
	}

	return {

        show : function(){
	       if (!painel) {
       	 		painel = new Ext.Window({
       	 			title: 'Remover pasta',
                    animateTarget: 'btn-remover-pasta',
                    width: 350,
                    height: 100,
                    shadow: false,
                    resizable: false,
                    closable: false,
                    buttons: [{
                    	text: 'Remover',
                    	handler: function(){ formSubmit();painel.hide();}
                	},{
                  	  	text:'Fechar',
                    	handler: function() { painel.hide(); }
                	}],
                	keys: [{
                		key: 27,
                		handler: function() { painel.hide(); }
                	}]
                });
       	 	}
       	 	
       	 	painel.show();
       	 	
       	 	var mgr = new Ext.UpdateManager(painel.body);
       	 	mgr.disableCaching = true;
       	 	mgr.indicatorText = 'Carregando...';
			mgr.update('/sigaa/ava/PortaArquivos/removerPasta.jsf');
        },
        
        hide : function() {
        	painel.hide();
        }
	};
}();

var PainelAlterarPasta = function() {
	var painel;

	var formSubmit = function() {
		var novoNome = Ext.get('alterar-pasta-nome').dom.value;
		var novoPai = Ext.get('alterar-pasta-pai').dom.value;
		var id = Ext.get('alterar-pasta-id').dom.value;
		var mgr = new Ext.UpdateManager('dummyDiv');
		mgr.disableCaching = true;
		mgr.update('/sigaa/cadastrarPasta.do?acao=alterar&pasta.id=' + id + '&pasta.nome=' + novoNome + '&pasta.pai.id=' + novoPai);
		
		mgr.on('update', function(element, response) {
		    if (response.responseText !== undefined){ 
		    	var obj = eval('(' + response.responseText + ')');
		    	Arvore.delNode(obj);
		    	Arvore.addNode(obj);
		    	Ext.MessageBox.alert('Sucesso', 'Pasta alterada com sucesso.');
				painel.hide();
	    	} 
		});
		mgr.on('failure', function(o) {
			Ext.MessageBox.alert('Erro', 'Erro ao alterar pasta.');
		});
	}

	return {
        show : function(){
        	if (Ext.get('pastaAtual').dom.value == -1) {
        		Ext.MessageBox.alert('Erro', 'Não é possível alterar a pasta Meus arquivos');
        		return false;
        	}
        
	       if (!painel) {
       	 		painel = new Ext.Window({
       	 			title: 'Alterar pasta',
                    animateTarget: 'btn-alterar-pasta',
                    width: 450,
                    height: 130,
                    shadow: false,
                    resizable: false,
                    closable: false,
                    buttons: [{
                    	text: 'Alterar',
                    	handler: function(){ formSubmit(); }
                	},{
                  	  	text:'Fechar',
                    	handler: function() { painel.hide(); }
                	}],
                	keys: [{
                		key: 27,
                		handler: function() { painel.hide() }
                	}]
                });
       	 	}
       	 	
       	 	painel.show();
       	 	
       	 	var mgr = new Ext.UpdateManager(painel.body);
       	 	mgr.disableCaching = true;
       	 	mgr.indicatorText = 'Carregando...';
			mgr.update('/sigaa/ava/PortaArquivos/alterarPasta.jsf?idPasta=' + Ext.get('pastaAtual').dom.value);
			
			mgr.on('update', function() {
        		Ext.get('alterar-pasta-nome').on('keypress', function(e) {
        			if (e.getKey() == Ext.EventObject.ENTER) {
        				e.stopEvent();
        			}
        		});        		
			});
        }
	};
}();


var PainelRenomear = function() {
	var painel, grid;

	var formSubmit = function(element, response) {
		//try {
			var mgr = new Ext.Updater('dummyDiv');
			mgr.disableCaching = true;
			mgr.on('update', function(element, response) {
				Ext.MessageBox.alert('Sucesso', 'Arquivo alterado com sucesso.');
				painel.hide()
				Ext.get('nomeArquivo').on('keypress', function(e) {
        			if (e.getKey() == Ext.EventObject.ENTER) {
        				e.stopEvent();
        			}
        		});
        		grid.getStore().load({ params: {idPasta: response.responseText}});
				GridUI.reRender();   
			});
			mgr.formUpdate('form-renomear');
      // 	} catch(exp) {
       		//alert(exp);
      //	}
	}

	return {
        show : function(_grid, record){
        	grid = _grid;
       	 	if (!painel) {
       	 		painel = new Ext.Window({
       	 			title: 'Renomear Arquivo',
                    width: 450,
                    height: 130,
                    shadow: false,
                    resizable: false,
                    closable: false,
                    buttons: [{
                    	text: 'Renomear',
                    	handler: function(){ formSubmit(); }
                	},{
                  	  	text:'Fechar',
                    	handler: function() { painel.hide(); }
                	}],
                	keys: [{
                		key: 27,
                		handler: function() { painel.hide() }
                	}]
                });
       	 	}
       	 	
       	 	painel.show();
       	 	
       	 	var mgr = new Ext.Updater(painel.body);
       	 	mgr.disableCaching = true;
       	 	mgr.indicatorText = 'Carregando...';
			mgr.update('/sigaa/ava/PortaArquivos/renomearArquivo.jsf');
			
			mgr.on('update', function() {
        		Ext.get('nomeAnterior').dom.innerHTML = record.data.nome;
	       	 	Ext.get('nomeArquivo').dom.value = record.data.nome;
	       	 	Ext.get('idRenomearArquivo').dom.value = record.id; 
	       		Ext.get('nomeArquivo').on('keypress', function(e) {
	        		if (e.getKey() == Ext.EventObject.ENTER) {
	        			e.stopEvent();
	        		}
				});
			});
       	 	
        }
	};
}();


Ext.onReady(function() {
	
	// Definição da Toolbar
	var tb = new Ext.Toolbar({ renderTo: 'toolbar' });
	tb.add({ cls: 'x-btn-text-icon novoArquivo', text: 'Novo Arquivo', handler: function() { document.location.href = '/sigaa/ava/PortaArquivos/index.jsf' } })
	tb.add('-')
	tb.add({ cls: 'x-btn-text-icon novaPasta', text: 'Nova Pasta', id: 'btn-nova-pasta', handler: PainelNovaPasta.show })
	tb.add('-')		
	tb.add({ cls: 'x-btn-text-icon alterarPasta', text: 'Alterar Pasta', id: 'btn-alterar-pasta', handler: PainelAlterarPasta.show })
	tb.add('-')		
	tb.add({ cls: 'x-btn-text-icon removerPasta', text: 'Remover Pasta', id: 'btn-remover-pasta', handler: PainelRemoverPasta.show })
	
});

function showFiles(id) {
	if (id == undefined)
		id = -1;
	if (isNaN(parseInt(id)))
		id = -1;
		
	Ext.get('idPasta').dom.value = id;
	GridUI.retrieve(id); 
}