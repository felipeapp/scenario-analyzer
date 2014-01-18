var PainelModulos = function() {
	var painel, link, link2;
	var tries = 0;
	return {
		init : function() {
			link = getEl('show-modulos');
   		 	if (link != undefined) {
				link.on('click', this.show, this, true);
			} else if (tries < 10) {
				setTimeout('PainelModulos.init()', 50);
				tries++;
			}
		},
        show : function(){
	       if (!painel) {

       	 		painel = new YAHOO.ext.BasicDialog("painel-modulos", {
       	 			autoCreate: true,
					title: 'Módulos do SIGAA',
                    modal: false,
                    animateTarget: 'show-modulos',
                    width: 984,
                    height: 400,
                    shadow: true,
                    resizable: false
                });

       	 	}
       	 	painel.show();
			var mgr = painel.body.getUpdateManager();
			if (!mgr.beforeUpdate.fireDirect) { mgr.beforeUpdate.fireDirect = function() { } }
			mgr.update({ url: '/sigaa/verMenuPrincipal.do?dialog=true', scripts: true });
			YAHOO.ext.DialogManager.bringToFront(painel);
        }
	};
}();

var PainelAjuda = function() {
	var painel, link;

	return {
		init : function() {
			link = getEl('show-ajuda');
			if (link != undefined)
				link.on('click', this.show, this, true);
		},
        show : function(){
       	 	if (!painel) {
       	 		painel = new YAHOO.ext.BasicDialog("painel-ajuda", {
       	 			autoCreate: true,
					title: 'Ajuda do SIGAA',
                    modal: false,
                    animateTarget: 'show-ajuda',
                    width: 640,
                    height: 400,
                    shadow: true,
                    resizable: false
                });
       	 	}
       	 	painel.show();

       	 	var mgr = painel.body.getUpdateManager();
       	 	if (!mgr.beforeUpdate.fireDirect) { mgr.beforeUpdate.fireDirect = function() { } }
			mgr.update({ url: '/sigaa/manuais/index.jsp', scripts: true });
			YAHOO.ext.DialogManager.bringToFront(painel);

        }
	};
}();

var PainelModulosSipac = (function() {	
	var painel, link, link2; 
	var tries = 0;
	return { 
	
		init : function() { 
		
			link = getEl('show-modulos-sipac');
			if (link != undefined) {
				link.on('click', this.show, this, true);
			} else if (tries < 10) {
				setTimeout('PainelModulosSipac.init()', 50);
				tries++;
			}
			
			link2 = getEl('show-entrar-sistema'); 
			if (link2 != undefined) {
				link2.on('click', this.show, this, true);
			} else if (tries < 10) {
				setTimeout('PainelModulosSipac.init()', 50);
				tries++;
			}
		},
		show : function(){ 
			
			if (!painel) { 
				painel = new YAHOO.ext.BasicDialog("painel-modulos-sipac", { 
					autoCreate: true, 
					title: 'Módulos do SIPAC', 
					modal: true, 
					animateTarget: 'show-modulos-sipac',
					width: 865, height: 490, 
					shadow: false, resizable: false 
				}); 
			} 
			
			painel.show(); 
			var mgr = painel.body.getUpdateManager(); 
			if (!mgr.beforeUpdate.fireDirect) { mgr.beforeUpdate.fireDirect = function() { } }
			mgr.update({ url: '/sipac/subsistemas.jsp', scripts: true });
			YAHOO.ext.DialogManager.bringToFront(painel); 
		} 
	}; 
})();

YAHOO.ext.EventManager.onDocumentReady(PainelModulosSipac.init, PainelModulosSipac, true);

function setAba(aba) { document.getElementById('aba').value = aba; }

function setAbas(aba, subAba) {
	document.getElementById('aba').value = aba;
	document.getElementById('subAba').value = subAba;
}

var PainelModulosSigrh = (function() {	
	var painel, link, link2; 
	var tries = 0;
	return { 
		init : function() { 
			link = getEl('show-modulos-sigrh'); 
			if (link != undefined) {
				link.on('click', this.show, this, true);
			} else if (link == undefined && tries < 10) {
				setTimeout('PainelModulosSigrh.init()', 50);
				tries++;
			}
			link2 = getEl('show-entrar-sistema'); 
			if (link2 != undefined) {
				link2.on('click', this.show, this, true);
			} else if (link2 == undefined && tries < 10) {
				setTimeout('PainelModulosSigrh.init()', 50);
				tries++;
			}
		},
		show : function(){ 
			if (!painel) { 
				painel = new YAHOO.ext.BasicDialog("painel-modulos-sigrh", { 
					autoCreate: true, 
					title: 'Módulos do SIGRH', 
					modal: true, 
					animateTarget: 'show-modulos-sigrh',
					width: 1000, height: 490, 
					shadow: false, resizable: false 
				}); 
			} 
			
			painel.show(); 
			var mgr = painel.body.getUpdateManager(); 
			if (!mgr.beforeUpdate.fireDirect) { mgr.beforeUpdate.fireDirect = function() { } }
			mgr.update({ url: '/sigrh/menu/menu_principal.jsf?ajaxRequest=true', scripts: false });
			YAHOO.ext.DialogManager.bringToFront(painel); 
		} 
	}; 
})();

YAHOO.ext.EventManager.onDocumentReady(PainelModulosSigrh.init, PainelModulosSigrh, true);

var PainelModulosSigpp = (function() {	
	var painel, link, link2; 
	var tries = 0;
	return { 
		init : function() { 
			link = getEl('show-modulos-sigpp'); 
			if (link != undefined) {
				link.on('click', this.show, this, true);
			} else if (link == undefined && tries < 10) {
				setTimeout('PainelModulosSigpp.init()', 50);
				tries++;
			}
			link2 = getEl('show-entrar-sistema'); 
			if (link2 != undefined) {
				link2.on('click', this.show, this, true);
			} else if (link2 == undefined && tries < 10) {
				setTimeout('PainelModulosSigpp.init()', 50);
				tries++;
			}
		},
		show : function(){ 
			if (!painel) { 
				painel = new YAHOO.ext.BasicDialog("painel-modulos-sigpp", { 
					autoCreate: true, 
					title: 'Módulos do SIGPP', 
					modal: true, 
					animateTarget: 'show-modulos-sigpp',
					width: 972, height: 410, 
					shadow: false, resizable: false 
				}); 
			} 
			
			painel.show(); 
			var mgr = painel.body.getUpdateManager(); 
			if (!mgr.beforeUpdate.fireDirect) { mgr.beforeUpdate.fireDirect = function() { } }
			mgr.update({ url: '/sigpp/menu/menu_principal.jsf?ajaxRequest=true', scripts: false });
			YAHOO.ext.DialogManager.bringToFront(painel); 
		} 
	}; 
})();

YAHOO.ext.EventManager.onDocumentReady(PainelModulosSigpp.init, PainelModulosSigpp, true);
YAHOO.ext.EventManager.onDocumentReady(PainelModulos.init, PainelModulos, true);
YAHOO.ext.EventManager.onDocumentReady(PainelAjuda.init, PainelAjuda, true);

