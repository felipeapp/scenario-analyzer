// Edson Anibal (ambar@info.ufrn.br)
//Painel genérico que mostra as views por ajax. Obs: o Painel deve ser chamado assim na JSP:
//<script type="text/javascript">
//<!--
//	function exibirPainel(id) {
//		var getURL = function(t) {
//			var url = t.responseText;
//			PainelDetalhar.show(url, 'Detalhes da Tarefa', 600, 395);
//		}
//		// buscar url
//		var ajax = new Ajax.Request('Tarefa/view.jsf',
//		{
//			method: 'get',
//			parameters: 'id=' + id,
//			onSuccess: getURL
//		});
//	}
////-->
//</script>

//Funcoes do Painel em Sí
var painel; //variavel global para as views enxergarem o painel de mudarem seu tamanho.
var PainelDetalhar = (function() {

	return {

        show : function(url, titulo,width,height){
        	var p = getEl('painel-detalhar');
        	if (p)
        		p.remove();
   	 		painel = new YAHOO.ext.BasicDialog("painel-detalhar", {
   	 			autoCreate: true,
				title: titulo,
				width:700,
				height:400,
				constraintoviewport: false,
				fixedcenter: true,
                modal: false,
                shadow: false,
                resizable: true
            });

			if(!width) width = 700;
			if(!height) height = 400;
			painel.resizeTo(width, height);

       	 	painel.show();

			var um = painel.body.getUpdateManager();
			um.disableCaching = false;
			um.update({
				 url: url,
				 discardUrl: true,
				 nocache: true,
				 scripts: true,
				 text: 'CARREGANDO...'
				 });

        }

	};
})();



