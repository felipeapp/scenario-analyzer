<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>	

	<h2><ufrn:subSistema /> > Monitorar Finalização de Ações > Notificar Coordenadores</h2>
				
		
<h:form id="form">
	<table class="formulario" width="100%" >
			<caption class="listagem">Notificação para Coordenadores Inadimplentes</caption>
								
				<tr>					
					<td> <h:inputTextarea value="#{ atividadeExtensao.msgCoordenadorPendenteRelatorio }"  rows="20" cols="90" /> </td>
				</tr>			
				
				
				<tfoot>
					<tr>
						<td>
							<h:commandButton id="notificarCoord" value="Notificar Coordenador(es)" action="#{atividadeExtensao.notificacaoFaltaDeRelatorioFinal}" />
							<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}"/>						
						</td>
					</tr>
				</tfoot>
				
		</table>
</h:form>
</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "100%", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>