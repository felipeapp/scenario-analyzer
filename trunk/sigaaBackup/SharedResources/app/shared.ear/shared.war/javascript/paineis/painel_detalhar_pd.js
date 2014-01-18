// painel com form de validação de produção intelectual
//Edson Anibal (copiei do "painel valida" e alterei algumas coisas)

//Funcao que pega o estilo dos objetos html em tmepo de execução
//usado para obter o tamanho das paginas vews e ajustar os paineis.
//Obs: so sera usado caso a funcao presnete nas views nao funcione no IE. (caso contrario retirar essa funcao daqui) 
function getStyle(el,styleProp)
{
	var x = document.getelementbyid(el);
	if (x.currentStyle)
	var y = x.currentStyle[styleProp];
	else if (window.getComputedStyle)
	var y = document.defaultView.getComputedStyle(x,null).getPropertyValue(styleProp);
	return y;
}

//Funcoes do Painel em Sí
var painel; //variavel global para as views enxergarem o painel de mudarem seu tamanho.
var PainelDetalhar = (function() {

	return {

        show : function(url, mostrarTodosBotoes,width,height){
        	var p = getEl('painel-detalhar');
        	if (p)
        		p.remove();
   	 		painel = new YAHOO.ext.BasicDialog("painel-detalhar", {
   	 			autoCreate: true,
				title: 'Produção Intelectual',
				width:700,
				height:400,
                modal: false,
                shadow: false,
                resizable: true
            });
			if(!width) width = 700;
			if(!height) height = 400;
			painel.resizeTo(width, height);
            //--------------//
            if (mostrarTodosBotoes) {
	            painel.addButton('Baixar Arquivo',
	            			function getArquivo() { // FUNCAO QND CLICAR NO BOTAO.
								var id = url.substr(url.lastIndexOf('&idArquivo=')+11).substr('&');
							    document.location.href='/sigaa/verProducao?idProducao='+id;

							}, painel);
			}
            //--------------//


       	 	painel.show();

			var um = painel.body.getUpdateManager();
			um.disableCaching = false;
			um.update({
				 url: url,
				 discardUrl: true,
				 nocache: true,
				 scripts: true,
				 text: 'CARREGANDO PRODUÇÃO...'
				 });

        }

	};
})();



