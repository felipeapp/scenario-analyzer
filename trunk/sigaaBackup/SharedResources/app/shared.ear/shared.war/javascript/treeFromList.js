
var ListaArvoreInstanciavel = function() {

	var tree, root, treeNodes;
	var show = false;

	function buildTree(root, node, destino, destinoId, nomeId) {
		var lis = node.childNodes;
		for (var i = 0; i < lis.length; i++) {
			if (lis[i].tagName == 'LI') {
				var li = Ext.get(lis[i]);
				var _cod = li.child('span', true).innerHTML;
				var label = li.child('label', true).innerHTML;
				var child = new Ext.tree.TreeNode({ text: label, id: lis[i].id, codigo: _cod } );
				child.on ( "click", function(e) {
					Ext.get(destino).dom.value = e.attributes.codigo;
					if (nomeId) Ext.get(nomeId).dom.value = e.attributes.text;
					Ext.get(destinoId).dom.value = e.id;
				})
				treeNodes.push(child);
				root.appendChild(child);

				
				var ul = Ext.get(lis[i]).down('ul');
				if (ul) buildTree(child, ul.dom, destino, destinoId, nomeId);
				
			}
		}
	}
	
	function expandNode(node) {
		if (node.parentNode != tree.root)
			expandNode(node.parentNode);
		node.expand();
	}

	return {
		init : function(idLista, idArvore, destino, destinoId, nomeId) {
			tree = new Ext.tree.TreePanel(idArvore,{
				animate: false,
				containerScroll: true,
				lines: true,
				rootVisible: false
			});

			treeNodes = new Array();

			root = new Ext.tree.TreeNode({ text: 'Raiz' });
			buildTree(root, Ext.get(idLista).down('ul').dom, destino, destinoId, nomeId);
			
			tree.setRootNode(root);
			tree.render();
			
			if (Ext.get(idLista).down('ul').dom.className == 'expanded') {
				root.expandChildNodes();
			}
		},
		
		expandir : function(codUnidade, destinoId) {
			for (var i = 0; i < treeNodes.length; i++) {
				var node = treeNodes[i];
				if (node.attributes.codigo == codUnidade) {
					expandNode(node);
					node.select();
					Ext.get(destinoId).dom.value = node.id;
					return node;
				}else {
					Ext.get(destinoId).dom.value = 0;
				}
			}
		},
		
		selecionaUnidade: function (codigoUnd, destinoId, nomeUnidade) {
			if(Ext.get(codigoUnd).dom.value.length >= 5){
				var cod = Ext.get(codigoUnd).dom.value;
				var url = '/shared/selecionaUnidade?codUnidade=' + cod;
				new Ajax.Request(url, {
					method: 'get',
					onSuccess: function(transport) {
						alert('');
						Ext.get(nomeUnidade).dom.value = transport.responseText;
					}
				});
				this.expandir(cod,destinoId );
				Ext.get(codigoUnd).focus();
			}
		}

		
		
	};
};

var ListaArvore = new ListaArvoreInstanciavel();

function selecionaUnidade(codigoUnd, destinoId, nomeUnidade, lista) {
	if(Ext.get(codigoUnd).dom.value.length >= 5){
		var cod = Ext.get(codigoUnd).dom.value;
		
		if (lista) {
			var node = lista.expandir(cod, destinoId);
			Ext.get(nomeUnidade).dom.value = node.attributes.text;
		} else {
			ListaArvore.expandir(cod, destinoId);
		}
		
		Ext.get(codigoUnd).focus();
	}
}

function selecionaUnidadeAutocomplete(codigoUnd, nomeUnidade, destinoId, lista) {
	var nome = Ext.get(nomeUnidade).dom.value;
	var cod = nome.substring(nome.lastIndexOf("(") + 1, nome.lastIndexOf(")"));
	Ext.get(codigoUnd).dom.value = cod;
	if (lista) {
		var node = lista.expandir(cod, destinoId);
		Ext.get(nomeUnidade).dom.value = node.attributes.text; 
	} else {
		ListaArvore.expandir(cod, destinoId);
	}
}

function formataUnidade(src, event) {
	return formatarMascara(src, event, '##.##.##.##.##.##.##.##');
}
