Mensagem = Class.create();

Mensagem.prototype =  {

	initialize: function() {
		this.tipoChamado = false;
		this.dialog = new YAHOO.widget.Dialog("painel-mensagem-envio", {
				width: '700px',
				fixedcenter: true,
				constraintoviewport: true,
				underlay: 'shadow',
				visible:false,
				draggable:true,
				modal: true }
			);
		var dialog = this.dialog;
        var tipoChamado = false;
		var handleCancel = function() { 
			this.tipoChamado = false;
			this.cancel(); 
		}
		var setTipoChamado = function(value) { this.tipoChamado = value;};
		var handleSubmit = function() {
			var destinatarios = null;
			if (getEl('destinatarios') != null)
				destinatarios = escape(getEl('destinatarios').dom.value).replace(/^\s+|\s+$/g, '');
			var assunto = escape(getEl('assunto-facade').dom.value).replace(/^\s+|\s+$/g, '');
			var corpoMensagem = escape(getEl('corpo-facade').dom.value).replace(/^\s+|\s+$/g, '');
			var tipo = getEl('tipoMensagem').dom.value;
			
			if (destinatarios != null && destinatarios == "") {
				alert('Informe os destinatários da mensagem.');
				return;
			}
			
			if (assunto == "") {
				alert('Informe o assunto da mensagem.');
				return;
			}
			
			if (corpoMensagem == "") {
				alert('Digite o texto da mensagem.');
				return;
			}
			
			getEl('assunto-mensagem').dom.value = assunto;
			getEl('corpoMensagem').dom.value = corpoMensagem;
			
			if(tipo != 2 && tipo != 3 && tipo != 5){
				this.submit();
				this.show();
				getEl('statusEnvio').dom.style.display = '';	
			}else {
				if (!this.tipoChamado) {
					alert('Informe o tipo do chamado.');
				} else {
					this.submit();
					this.show();
					getEl('statusEnvio').dom.style.display = '';	
					this.tipoChamado = false;
				}
			}
		}

		var myButtons = [
			{ text: "Enviar", handler: handleSubmit, isDefault: true },
			{ text: "Cancelar", handler: handleCancel }
		];
		this.dialog.cfg.queueProperty("buttons", myButtons);

		var listeners = new YAHOO.util.KeyListener(document, { keys : 27 }, {fn:handleCancel, scope:this.dialog, correctScope:true} );
		this.dialog.cfg.queueProperty("keylisteners", listeners);

		var onSuccess = function(o) {
			try {
				var resultado = eval("(" + o.responseText + ")");
			} catch (erro) {
			}

			getEl('statusEnvio').dom.style.visibility = 'hidden';
			if (resultado != undefined && resultado.sucesso ) {
				dialog.hide();
			} else {
				alert('Problema no envio!');
			}
		}

		var onFailure = function(o) {
			alert("Erro no envio do chamado! Tente novamente....");
		}
		this.dialog.setTipoChamado = setTipoChamado;
		this.dialog.callback.success = onSuccess;
		this.dialog.callback.failure = onFailure;

		this.dialog.setHeader("Mensagem");
		this.dialog.setBody("Aguarde...");
		this.dialog.render();
	},

	show: function(tipoMensagem, url, dest) {
		var dialog = this.dialog;
		var mensagem = this;

		if (tipoMensagem == undefined) {
			tipoMensagem = 1;
		}

		var callback = {
			success: function(o) {

				if (tipoMensagem == 2) {
					dialog.setHeader('Abrir Chamado');
				} else {
					dialog.setHeader('Enviar Mensagem');
				}

				dialog.setBody(o.responseText);
				dialog.registerForm();
				if(dest)
					$('destinatarios').value=dest;
				dialog.show();
			},
		  	failure: function(o) {
		  		alert("Falha na abertura do formulário. Contate o administrador do sistema!");
		  	}
		};
		if (url == undefined) {
			url = '/sigaa/mensagem/enviaMensagem.do?acao=5&mensagem.tipo=' + tipoMensagem;
		}
		bodyRequest = YAHOO.util.Connect.asyncRequest('GET', url, callback);
	}
}


PainelMensagemLeitura = Class.create();
PainelMensagemLeitura.prototype =  {
	initialize: function() {
		this.panel = new YAHOO.widget.Panel("painel-mensagem-leitura", {
			width: '700px',
			height: '400px',
			fixedcenter: true,
			constraintoviewport: true,
			underlay: 'shadow',
			visible:false,
			draggable:true,
			modal: true
		});
		var listeners = new YAHOO.util.KeyListener(document, { keys : 27 }, {fn: this.panel.hide, scope:this.panel, correctScope:true} );
		this.panel.cfg.queueProperty("keylisteners", listeners);

		this.panel.setHeader('Mensagem');
		this.panel.setBody('<div align="center">Carregando mensagem... Aguarde!  </div>');
		this.panel.render();
	},

	show: function(url, id) {
		var panel = this.panel;
		var callback = {
			success: function(o) {
			
				panel.setBody(o.responseText);
				panel.show();
				if ($('todasLidas')) {
//					$('msg').hide();
//					$('link').show();
//					$('modulos').show();
				} else {
//					$('msg').show();
//					$('link').hide();
//					$('modulos').hide();
				}
				$('msg_'+id).removeClassName('selected');
				$('msg_td_'+id).removeClassName('leituraObrigatoria');
			},
		  	failure: function(o) {
		  		alert("Falha na abertura do painel. Contate o administrador do sistema!");
		  	}
		};

		YAHOO.util.Connect.asyncRequest('GET', url, callback);
		this.panel.setBody('<div align="center">Carregando mensagem... Aguarde!  </div>');
		panel.show();
	},

	hide: function() {
		this.panel.hide();
	}

}

PainelSubsistemas = Class.create();
PainelSubsistemas.prototype = {

	initialize: function() {
		this.panel = new YAHOO.widget.Panel("painel-subsistemas", {
			width: '650px',
			fixedcenter: true,
			constraintoviewport: true,
			underlay: 'shadow',
			visible:false,
			draggable:true,
			modal: true
		});
		var listeners = new YAHOO.util.KeyListener(document, { keys : 27 }, {fn: this.panel.hide, scope:this.panel, correctScope:true} );
		this.panel.cfg.queueProperty("keylisteners", listeners);

		this.panel.setHeader('Selecione a opção desejada:');
		this.panel.setBody("Aguarde...");
		this.panel.render();
	},

	show: function() {
		var panel = this.panel;
		var callback = {
			success: function(o) {
				panel.setBody(o.responseText);
				panel.show();
			},
		  	failure: function(o) {
		  		alert("Falha na abertura do painel. Contate o administrador do sistema!");
		  	}
		};

		YAHOO.util.Connect.asyncRequest('GET', 'subsistemas.jsp', callback);
	}
}

