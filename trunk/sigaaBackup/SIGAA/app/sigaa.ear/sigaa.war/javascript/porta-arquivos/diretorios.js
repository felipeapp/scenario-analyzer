
var Arvore = function() {
	var tree, root, nodes;
	var menuC = new Ext.menu.Menu('mainContext');
	menuC.add({text: 'Criar Sub-Pasta', icon : '/sigaa/img/porta-arquivos/page_add.png', handler : function(item, e) {
		var node = tree.getSelectionModel().getSelectedNode();
		PainelNovaPasta.show(node.attributes.id); 
	}});
    menuC.add({text: 'Editar', icon : '/sigaa/img/porta-arquivos/page_edit.png', handler : function(item, e) {
    	var node = tree.getSelectionModel().getSelectedNode();
    	Ext.get('pastaAtual').dom.value = node.attributes.id;
    	PainelAlterarPasta.show();
    }});
    menuC.addSeparator();
    menuC.add({text: 'Remover', icon : '/sigaa/img/porta-arquivos/page_delete.png', handler : function(item, e) {
    	var node = tree.getSelectionModel().getSelectedNode();
    	removerPasta(node.attributes.id);
    }});

	function findById(id) {
		for (var i = 0; i < nodes.length; i++) {
			if (nodes[i].id == id)
				return nodes[i]; 
		}
		return null;
	}	
	
	function remove(id) {
		var index;
		for (var i = 0; i < nodes.length; i++) {
			if (nodes[i].id == id) {
				index = i;
				break; 
			}
		}
		nodes.splice(i, 1);
	}	

	// Cria um novo nó para a árvore
	function newNode(obj) {
		var aNode = new Ext.tree.TreeNode({
			id: obj.id,
			pai: obj.pai,
			text: obj.text,
			allowDrop: true,
			allowDrag: true
		});
				
		var pai = findById(obj.pai);

		if (!pai) root.appendChild(aNode);
		else pai.appendChild(aNode);
		
		aNode.on('click', function() {
			Ext.get('pastaAtual').dom.value = obj.id;
			showFiles(obj.id);
		});
		
		nodes.push(aNode);
	}

	return {
	
		init : function() {
		
			tree = new Ext.tree.TreePanel({ 
				animate: false,
				enableDD: true,
				containerScroll: true,
				ddGroup: 'PortaArquivosDD', 
				ddAppendOnly: true,
				lines: true,
				autoScroll: 'auto',
				id: 'teste',
				el: 'diretorios'
			});
			
			var sorter = new Ext.tree.TreeSorter(tree, {folderSort:true});
			
			nodes = new Array();
			root = new Ext.tree.TreeNode({ 
				text: 'Meus Arquivos',
				allowDrop: true,
				allowDrag: false, 
				id: -1, 
				pai: -1 
			});
			
			root.on('click', function() {
				Ext.get('pastaAtual').dom.value = -1;
				showFiles(-1);
			});
			
			for (i = 0; i < objs.length; i++) {
				obj = objs[i];
				newNode(obj)				
			}
			
			tree.setRootNode(root);
			tree.render();
			tree.expandAll();
			
			tree.on('contextmenu', this.menuShow, this);
			tree.on('beforenodedrop', function(e) { 
				if (e.dropNode != undefined) {
					var mgr = Ext.get('dummyDiv').getUpdateManager();
					mgr.update('/sigaa/moverPasta.do?origem=' + e.data.node.id + '&destino=' + e.target.id);
					mgr.on('failure', function() {
						Ext.MessageBox.alert('Erro', 'Erro ao mover pasta!');
					});
				} else {
					var sm = GridUI.getGrid().getSelectionModel();
					var rows = sm.getSelections();
					var rowData = GridUI.getDataSource().getById(rows[0].id);
					
					var mgr = Ext.get('dummyDiv').getUpdateManager();
					mgr.update('/sigaa/moverArquivo.do?arquivo.id=' + rowData.id + '&pasta.id=' + e.target.id);
					mgr.on('update', function(oElement, oResponseObject) {
						var id = oResponseObject.responseText;
						GridUI.retrieve(id); 
					});
					mgr.on('failure', function() {
						Ext.MessageBox.alert('Erro', 'Erro ao mover arquivo!');
					});
				}
			});
			
			
		},
		
		addNode : function(obj) {
			newNode(obj)
			tree.render();
		},
		
		delNode : function(obj) { 
			var node = findById(obj.id);
			var pai = node.parentNode;
			pai.removeChild(node);
			remove(obj.id);
			tree.render();
		},
		
		menuShow : function(node) {
			tree.getSelectionModel().select( node );
			menuC.show(node.ui.getAnchor());
		}
	};
	

}();

Ext.onReady(Arvore.init, Arvore, true);
