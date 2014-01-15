
var ProgressoProcessamento = function() {

	var pbar, runner, count, countInicial, tipo;

	var task = {
    	run: function(){
    	
    		Ext.Ajax.request({
				url : '/sigaa/feedbackProcessamento?tipo=' + tipo,
				disableCaching : true,
				success : function(response, options) {				
					var count = parseInt(response.responseText);
					var processadas = countInicial - count;
					
					var i = processadas / (countInicial == 0 ? 1 : countInicial); 
        			pbar.updateProgress(i, processadas + ' de ' + countInicial + ' turmas processadas... (' + Math.round(100*i) + '% completo)');
					if (count <= 0) {
						runner.stop(task);
						
						if (tipo == 'processamento') {
							document.location.href = '/sigaa/graduacao/matricula/processamento/fim.jsf';
						} else {
							Ext.MessageBox.alert('Processamento de Matrícula', 'Processamento finalizado com sucesso!');
						}
					}
				}
			});

    	},

    	interval: 5000 //5 segundos
	}

	return {
	
		init : function() {
			progresso = Ext.get('progresso');
			progresso.setVisibilityMode(Element.DISPLAY);
			progresso.setDisplayed(true);
			
			btn = Ext.get('form:processar');
			tipo = Ext.get('tipoProgresso').dom.value;
			
			Ext.Ajax.request({
				url : '/sigaa/feedbackProcessamento?tipo=' + tipo,
				disableCaching : true,
				success : function(response, options) {
					countInicial = parseInt(response.responseText);
					pbar = new Ext.ProgressBar({
						text:'Aguardando inicio do processamento...',
	        			id:'pbar',
	        			textEl:'pgbtext',
	        			renderTo:'pgb'
					});
		
					runner = new Ext.util.TaskRunner();
					runner.start(task);
				}
			});
		
		}
	
	}

}();

Ext.onReady(ProgressoProcessamento.init, ProgressoProcessamento, true);