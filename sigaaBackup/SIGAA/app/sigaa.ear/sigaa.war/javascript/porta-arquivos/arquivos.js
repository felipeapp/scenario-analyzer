
// Definição da grid de arquivos
var GridUI = function() {

	var grid; //component
	var ds; // datasource
	var columnModel; // definition of the columns
	var clicked;

	function setupDataSource(id) {
		if (id == undefined) id = -1;
	
		if (!ds) {
			ds = new Ext.data.Store({
				url: '/sigaa/listarArquivos.do',
		        reader: new Ext.data.XmlReader({
	               record: 'arquivo',
	               id: 'id'
	           }, [ 'nome', 'tamanho', 'dataarquivo', 'idArquivo', 'key' ])
			});
		}
		
		ds.load({ params: {idPasta: id}});
	}


	function getColumnModel() {
		if(!columnModel) {
			columnModel = new Ext.grid.ColumnModel(
				[
					{
						header: 'Nome',
						width: 400,
						sortable: true,
						dataIndex: 'nome'
					},
					{
						header: 'Tamanho (kb)',
						width:100,
						sortable: true,
						dataIndex: 'tamanho'
					},
					{
						header: 'Data',
						width:100,
						sortable: true,
						dataIndex: 'dataarquivo',
						renderer:  Ext.util.Format.dateRenderer('d/m/Y H:i')
					}
				]
			);
		}
		return columnModel;
	}

	function buildGrid() {
		if (!grid) {
			grid = new Ext.grid.GridPanel({
					ds: ds,
					ddGroup: 'PortaArquivosDD',
					enableDrag: true,
					cm: getColumnModel(),
					autoHeight : false,
					height: 200,
					renderTo: 'arquivos',
					bbar: new Ext.Toolbar({
						items: [
							{
							    text: 'Associar Arquivo a Turma',
							    cls: 'x-btn-text-icon details',
							    icon: '/sigaa/img/porta-arquivos/attach.png',
								handler: function() {
									var record = grid.getSelectionModel().getSelected();
									if (record != undefined)
										document.location.href='/sigaa/associarArquivo.do?dispatch=associar&id=' + record.id;
									else
							        	Ext.MessageBox.alert('Erro', 'Nenhum arquivo selecionado.');
								}
							}, '-',
							{
							    text: 'Associar Vídeo a Turma',
							    cls: 'x-btn-text-icon details',
							    icon: '/sigaa/img/porta-arquivos/attach.png',
								handler: function() {
									var record = grid.getSelectionModel().getSelected();
									if (record != undefined)
										document.location.href='/sigaa/associarVideo.do?dispatch=associar&id=' + record.id;
									else
							        	Ext.MessageBox.alert('Erro', 'Nenhum arquivo selecionado.');
								}
							}, '-',
							{
							    text: 'Editar Selecionado',
							    cls: 'x-btn-text-icon details',
							    icon: '/sigaa/img/porta-arquivos/page_edit.png',
								handler: function() {
									var record = grid.getSelectionModel().getSelected();
									if (record)
							        	PainelRenomear.show(grid, record);
							        else
							        	Ext.MessageBox.alert('Erro', 'Nenhum arquivo selecionado.');
								}
							},'-', {
							    text: 'Excluir Selecionado',
							    cls: 'x-btn-text-icon details',
							    icon: '/sigaa/img/porta-arquivos/page_delete.png',
								handler: function() {
									var record = grid.getSelectionModel().getSelected();
									if (!record) {
										Ext.MessageBox.alert('Erro', 'Nenhum arquivo selecionado.');
										return;
									}
									
									Ext.MessageBox.confirm('Confirmar', 'Deseja realmente remover esse arquivo? Caso este arquivo '+
															'seja excluído todas as referências a ele também serão, incluindo arquivos ' + 
															'associados a turmas ou comunidades virtuais.', function(btn) {
										if (btn == 'yes') {
											var mgr = Ext.get('dummyDiv').getUpdater();
											mgr.disableCaching = true;
											mgr.update({ url: '/sigaa/removerArquivo.do?arquivo.id=' + record.id });
											mgr.on('update', function(element, response) {
												Ext.MessageBox.alert('Sucesso', 'Arquivo excluido com sucesso.');
												var id = response.responseText; 
												ds.load({ params: {idPasta: id}});
												GridUI.reRender();
											});
											mgr.on('failure', function(element, response) {
												Ext.MessageBox.alert('Erro', 'Erro ao excluir arquivo. Verifique se ele não está associado a nenhuma turma.');
												var id = response.responseText;
												ds.load({ params: {idPasta: id}});
												GridUI.reRender();
											});
										}
									});
									
								}
							}
						]
					})
				}
			);
			grid.getSelectionModel().singleSelect = true;
			grid.getDragDropText = function(){
			     var nome = this.getSelectionModel().getSelected().data.nome;
			     return nome;
			}
			grid.on("rowclick", function(grid) { clicked = false; });
			grid.on("rowdblclick", function(grid) { 
				if (!clicked) window.open('/sigaa/verProducao?idProducao=' + grid.getSelectionModel().getSelected().data.idArquivo + '&key=' + grid.getSelectionModel().getSelected().data.key);
				clicked = true; 
			});
		}

		grid.render();
	}
	
	function loadContextMenu() {
		var gridContextMenu = new Ext.menu.Menu('arquivo-cm');
		gridContextMenu.add({ id: 'arquivo-edit', text: 'Editar', handler: onGridContextMenuClick, icon : '/sigaa/img/porta-arquivos/page_edit.png' });
		gridContextMenu.add({ id: 'arquivo-associar', text: 'Associar a uma Turma', handler: onGridContextMenuClick, icon : '/sigaa/img/porta-arquivos/page_link.png' });
		gridContextMenu.addSeparator();
		gridContextMenu.add({ id: 'arquivo-delete', text: 'Excluir', handler: onGridContextMenuClick, icon : '/sigaa/img/porta-arquivos/page_delete.png' });
		
		
		grid.doRowContextMenu = function (grid, rowIndex, e) {
			//row data is available here
			e.stopEvent();
			var coords = e.getXY();
			gridContextMenu.showAt([coords[0], coords[1]]);
			rowRecord = grid.getStore().getAt(rowIndex);
		}
		
		grid.addListener('rowcontextmenu', grid.doRowContextMenu);
		
		function onGridContextMenuClick(item, e) {
			// row data or handle to grid or row id is not available in item or e
			//only the id and text of the selected contextmenu
			switch  (item.id) {
				case 'arquivo-associar':
					var record = grid.getSelectionModel().getSelected();
					document.location.href='/sigaa/associarArquivo.do?dispatch=associar&id=' + record.id;
					break;
		      	case 'arquivo-edit':
		      		var record = grid.getSelectionModel().getSelected(); 
		      		if (record != undefined)
		        		PainelRenomear.show(grid, record);
		      	  	break;
		    	case 'arquivo-delete':
		      	  	Ext.MessageBox.confirm('Confirmar', 'Deseja realmente remover esse arquivo?', function(btn) {
					if (btn == 'yes') {
						var record = grid.getSelectionModel().getSelected();
						var mgr = Ext.get('dummyDiv').getUpdateManager();
						mgr.disableCaching = true;
						mgr.update('/sigaa/removerArquivo.do?arquivo.id=' + record.id);
						mgr.on('update', function(oElement, oResponseObject) {
							Ext.MessageBox.alert('Sucesso', 'Arquivo excluido com sucesso.');
							var id = oResponseObject.responseText;
							ds.load({ params: {idPasta: id}});
							GridUI.reRender();
						});
					}
				});
		     	  break;
		  	}
		}
	}
	
	return {
		retrieve : function(id) {
			setupDataSource(id);
			buildGrid();
			loadContextMenu();
		},
		getDataSource : function() {
			return ds;
		},
		reRender : function() {
			grid.render();
		},
		getGrid : function() {
			return grid;
		}
	}
}();

Ext.onReady(GridUI.retrieve, GridUI, true);
