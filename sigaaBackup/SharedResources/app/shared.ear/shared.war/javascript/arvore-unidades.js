
var ArvoreUnidade = function() {

	var divOrigem, divDestino, campoId, campoCodigo, campoNome, tipo, onselectfunc, isRootVisible;
	
	var arvore, raiz, nos;
	var ajax = false;
	
	function construir(raiz, node) {
		var filhos = node.childNodes;
		
		for (var i = 0; i < filhos.length; i++) {
			if (filhos[i].tagName == 'LI') {
				var filho = Ext.get(filhos[i]);
				
				var _id = filho.dom.id;
				var _codigo = filho.child('span', true).innerHTML;
				var _text = filho.child('label', true).innerHTML;
				
				var noFilho;
				
				if (ajax)
					noFilho = new Ext.tree.AsyncTreeNode({ text: _text, id: _id, codigo: _codigo });
				else
					noFilho = new Ext.tree.TreeNode({ text: _text, id: _id, codigo: _codigo });
				
				noFilho.on ( "click", function(e) {
					Ext.get(campoId).dom.value = e.id;
					Ext.get(campoCodigo).dom.value = e.attributes.codigo;
					Ext.get(campoNome).dom.value = e.attributes.text.unescapeHTML();
					if (onselectfunc) { eval(onselectfunc); }
				});
				
				nos.push(noFilho);
				raiz.appendChild(noFilho);
				
				var proximo = Ext.get(filhos[i]).down('ul');
				if (proximo) construir(noFilho, proximo.dom);
			}
		}
	}
	
	function expandirNo(no) {
		if (no.parentNode != null && no.parentNode != arvore.root) {
			expandirNo(no.parentNode);
		}
		no.expand();
	}
	
	return {
		
		init: function(_divOrigem, _divDestino, _campoCodigo, _campoId, _campoNome, _ajax, _tipo, _onselectfunc) {
			campoId = _campoId;
			campoCodigo = _campoCodigo;
			campoNome = _campoNome;
			divOrigem = _divOrigem;
			divDestino = _divDestino;
			ajax = _ajax;
			tipo = _tipo;
			onselectfunc = _onselectfunc;
			
			var primeiroNo = Ext.get(divOrigem).down('ul').down('li').dom;
			var noRaiz = Ext.get(primeiroNo);
			var _id = noRaiz.dom.id;
			var _codigo = noRaiz.child('span', true).innerHTML;
			var _text = noRaiz.child('label', true).innerHTML;

			isRootVisible = _codigo != '';
			nos = new Array();

			if (ajax) {
				var _loader = new Ext.tree.TreeLoader({ dataUrl: '/admin/buscaUnidade' });
				_loader.on("beforeload", function(treeLoader, node) {
					treeLoader.baseParams.tipo = tipo;
			    }, this);
				_loader.createNode = function(_d) {
					if (_loader.baseAttrs) {
						Ext.applyIf(_d, _loader.baseAttrs);
					}
					if (_loader.applyLoader !== false) {
						_d.loader = _loader;
					}
					if (typeof _d.uiProvider == "string") {
						_d.uiProvider = _loader.uiProviders[_d.uiProvider] || eval(_d.uiProvider);
					}
					
					var node = (_d.leaf ? new Ext.tree.TreeNode(_d) : new Ext.tree.AsyncTreeNode(_d));
					node.on( "click", function(e) {
						Ext.get(campoId).dom.value = e.id;
						Ext.get(campoCodigo).dom.value = e.attributes.codigo;
						Ext.get(campoNome).dom.value = e.attributes.text.unescapeHTML();
						if (onselectfunc) { eval(onselectfunc); }
					});
					
					return node; 
				}
			
		        arvore = new Ext.tree.TreePanel(divDestino, {
		        	loader: _loader,
					animate: false,
					containerScroll: true,
					lines: true,
					rootVisible: true
				});
		        raiz = new Ext.tree.AsyncTreeNode({ text: _text, id: _id, codigo: _codigo });
		        raiz.on ( "click", function(e) {
					Ext.get(campoId).dom.value = e.id;
					Ext.get(campoCodigo).dom.value = e.attributes.codigo;
					Ext.get(campoNome).dom.value = e.attributes.text.unescapeHTML();
					if (onselectfunc) { eval(onselectfunc); }
				});
		        nos.push(raiz);
		        
			} else {
				arvore = new Ext.tree.TreePanel(divDestino, {
					animate: false,
					containerScroll: true,
					lines: true,
					rootVisible: isRootVisible
				});
				raiz = new Ext.tree.TreeNode({ text: _text, id: _id, codigo: _codigo });
				raiz.on ( "click", function(e) {
					Ext.get(campoId).dom.value = e.id;
					Ext.get(campoCodigo).dom.value = e.attributes.codigo;
					Ext.get(campoNome).dom.value = e.attributes.text.unescapeHTML();
					if (onselectfunc) { eval(onselectfunc); }
				});
				nos.push(raiz);
			}
			
			construir(raiz, Ext.get(primeiroNo).down('ul').dom);
			arvore.setRootNode(raiz);
			arvore.render();
			raiz.expand();
		},
		
		expandir : function(path) {
			if (ajax) {
				arvore.expandPath(path, null, function(bSuccess, oLastNode) {
					oLastNode.select();
					Ext.get(campoNome).dom.value = oLastNode.attributes.text.unescapeHTML();
				});
			} else {
				var codigo = Ext.get(campoCodigo).dom.value;
				for (var i = 0; i < nos.length; i++) {
					var no = nos[i];
					if ((no.attributes.codigo) && (no.attributes.codigo == codigo)) {
						expandirNo(no);
						no.select();
						Ext.get(campoId).dom.value = no.id;
						Ext.get(campoNome).dom.value = no.attributes.text.unescapeHTML();
						return no;
					}else {
						Ext.get(campoId).dom.value = 0;
						Ext.get(campoNome).dom.value = '';
						no.unselect();
					}
				}
			}
		},
		
		selecionar : function() {
			var _codigo = Ext.get(campoCodigo).dom.value;
			if (_codigo.length >= 5) {
				if (ajax) {
					jQuery.get('/admin/buscaUnidade?tipo=' + this.getTipo() + '&codigo=' + _codigo, function(data) {
						arvore.expandPath(data, null, function(bSuccess, oLastNode) {
							oLastNode.select();
							Ext.get(campoNome).dom.value = oLastNode.attributes.text.unescapeHTML();
						});
					});
				} else {
					this.expandir();
				}
				
				Ext.get(campoCodigo).focus();
				
				if (onselectfunc) {
					eval(onselectfunc); 
				}
			} else {
				this.expandir();
			}
		},
		
		isAjax : function() {
			return ajax;
		},
		
		getTipo: function() {
			return tipo;
		}
		
	};
};

function selecionaUnidadeAutocomplete(codigoUnd, nomeUnidade, destinoId, lista, onselectfunc) {
	var nome = Ext.get(nomeUnidade).dom.value;
	var cod = nome.substring(nome.lastIndexOf("(") + 1, nome.lastIndexOf(")"));
	Ext.get(codigoUnd).dom.value = cod;

	if (lista.isAjax()) {
		jQuery.get('/admin/buscaUnidade?tipo=' + lista.getTipo() + '&codigo=' + cod, function(data) {
			lista.expandir(data);
		});
	} else {
		var node = lista.expandir();
		Ext.get(nomeUnidade).dom.value = node.attributes.text;
	}
	
	if (onselectfunc) { eval(onselectfunc); }
}

function formataUnidade(src, event) {
	return formatarMascara(src, event, '##.##.##.##.##.##.##.##');
}