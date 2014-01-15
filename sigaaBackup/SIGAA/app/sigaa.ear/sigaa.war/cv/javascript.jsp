<%-- a JSP é usada para gerar o javascript dinamicamente  --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
var Mensagem = function() {

	var painel, updater, dest;

	var listener1 = function() { 
		if (dest){ document.getElementById('destinatarios').value = dest; }
		updater.un('update', listener1); 
	};

	var listener2 = function() { 
		updater.un('update', listener2); 
		painel.hide(); 
		//painel = null; 
		updater = null; 
		Ext.MessageBox.alert('Status da Mensagem', 'Enviado com sucesso.');
 	}

	var listener3 = function() {
		Ext.get('assunto-mensagem').dom.value=escape(Ext.get('assunto-facade').dom.value);
		Ext.get('corpoMensagem').dom.value=escape(Ext.get('corpo-facade').dom.value);
		Ext.get('statusEnvio').dom.style.visibility='visible';
		updater.un('beforeupdate', listener3);
	}

	return {
	
		show: function(tipo, _dest) {
			if (!painel) {
				painel = new Ext.Window({
					applyTo: 'mensagem',
					width: 730,
					height: 240,
					indicatorText: '<div class="loading-indicator">Enviando...</div>',
					closeAction: 'hide',
					buttons: [
					{ text: 'Enviar', handler: function() { 
					
							if (Ext.get('assunto-facade').dom.value == '' || Ext.get('corpo-facade').dom.value == '') {
								alert('Digite um assunto e um texto para a mensagem.');
								return;
							}
					
							var mgr = painel.getUpdater();
							mgr.on('beforeupdate', listener3);
							mgr.on('failure', function() { alert("Erro no envio do chamado! Por favor, tente novamente...."); });
							mgr.on('update', listener2);
							mgr.formUpdate('formMensagem');
							
							//alert('Mensagem enviada com sucesso!');
							
						} 
					}, { text: 'Cancelar', handler: function() { painel.hide(); } }
					]
				});
			}

			dest = _dest;
			if(tipo==undefined){ tipo = 1; }

			painel.html = '';
			painel.setHeight(255);
			painel.center();

			updater = painel.getUpdater();
			updater.on('failure', function() { alert("Falha na abertura do formulário. Contate o administrador do sistema!"); });
			updater.on('update', listener1 );
			updater.update({ url: "/sigaa/mensagem/enviaMensagem.do?acao=5&mensagem.tipo=" + tipo });
		
			if (tipo == 3) {
				painel.setTitle("Enviar Chamado");
			} else {
				painel.setTitle("Enviar Mensagem");
			}
			
			painel.show();
		}
		
	}

}();
var PainelModulos = function(){
	var painel,link;
	var tries=0;
	
	return{
		init:function(){
			link=Ext.get('show-modulos-ava');
			if(link!=undefined){
				link.on('click',this.show,this,true);
			} else if(tries<10){
				setTimeout('PainelModulos.init()',50);
				tries++;
			}
		},
		
		show:function(){
			if(!painel){
				painel = new Ext.Window({
					title:'Módulos do SIGAA',
					modal:false,
					width:752,
					height:380,
					resizable:false,
					closeAction: 'hide'
				});
			}
			painel.show();
			var mgr = painel.getUpdater();
			mgr.update('/sigaa/verMenuPrincipal.do?dialog=true');
		}
	};
}();

var PainelAjuda = function(){
	var painel,link;
	
	return{	
		init:function(){
			link=Ext.get('show-ajuda-ava');
			if(link!=undefined)
				link.on('click',this.show,this,true);
		},
		
		show:function(){
			if(!painel){
				painel=new Ext.Window({
					autoCreate:true,
					title:'Ajuda do SIGAA',
					modal:false,
					animateTarget:'show-ajuda',
					width:640,
					height:400,
					shadow:true,
					resizable:false
				});
			}
			painel.show();
			var mgr=painel.getUpdater();
			mgr.update('/sigaa'+document.getElementById('linkAjuda').value);
		}
	};
}();

Ext.onReady(function(){

		PainelModulos.init();
		PainelAjuda.init();

        Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
        var inicializando = true;
        
        p = new Ext.Panel({
            region:'west',
            id:'west-panel',
            split:true,
            width: 200,
            resizable: false,
            activeOnTop: true,
            collapsible: false,
            margins:'0 0 0 5',
            layout:'accordion',
            stateEvents: [ 'expand', 'collapse'],
	   		restoreState: function() {
		        //Accordion
		        if (this.items) { 
		          for (var x=0; x<this.items.length; x++) {
		            var item = this.items.items[x];
		            if (item.stateId && Ext.state.Manager.getProvider().get(item.stateId + '_state') == 'collapsed') {
			          item.collapse();
			        } else if (item.stateId && Ext.state.Manager.getProvider().get(item.stateId + '_state') == 'expanded') {
			          item.expand();
			        }
		          }
		        }
		        
      		},	
            layoutConfig:{
                animate:false
            },
            items: [{
                contentEl: 'turma',
                title:'Menu Comunidade Virtual',
                border:false,
                iconCls:'turma',
                stateId: '1',
                autoScroll: true
            }    
		]
      });      
        
})

Event.KEY_RETURN = 13;
Event.KEY_BACKSPACE = 8;
Event.KEY_TAB = 9;
Event.KEY_LEFT = 37;
Event.KEY_RIGHT = 39;
Event.KEY_DELETE = 46;
function mask(field,_mask,val){
	var i,mki;var aux="";
	if(val.length>=field.maxLength){return val.substr(0,field.maxLength);}
	else {
		for(i=mki=0;i<val.length;i++,mki++){
			if(_mask.charAt(mki)==''||_mask.charAt(mki)=='#'||_mask.charAt(i)==val.charAt(i)){aux+=val.charAt(i);}
			else{aux+=_mask.charAt(mki)+val.charAt(i);mki++;}
		}
	}
	return aux;
}
function formataData(field,event){
	if (field.maxLength != 10) field.maxLength = 10;
	var _mask='##/##/####';
	var key='';var aux='';
	var len=0;var i=0;var strCheck='0123456789';
	var rcode=event.keyCode?event.keyCode:event.which?event.which:event.charCode;
	if((rcode==Event.KEY_RETURN)||(rcode==Event.KEY_BACKSPACE)||(rcode==Event.KEY_TAB)||(rcode==Event.KEY_LEFT)||(rcode==Event.KEY_RIGHT)||(rcode==Event.KEY_DELETE)){return true;}
	key=String.fromCharCode(rcode);if(strCheck.indexOf(key)==-1){return false;}
	aux=field.value+key;
	aux=mask(field,_mask,aux);
	field.value=aux;
	return false;
}

function datePicker(id) {
	dateField = new Ext.form.DateField({
    	allowBlank:false,
        format:'d/m/Y',
        applyTo: id
	});
}