<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

<a4j:keepAlive beanName="avisoProjeto" />
<a4j:keepAlive beanName="projetoMonitoria" />


	<h2><ufrn:subSistema /> > Avisos do Projeto > Visualizar Aviso</h2>
	<h:form>

	<br/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Aviso para Projetos </caption>
	
	
	<tr>
		<th width="15%"> Projeto: </th>
		<td><b><h:outputText value="#{avisoProjeto.obj.projeto.anoTitulo}" /></b></td>
	</tr>
	
	<tr>
		<th> Assunto: </th>
		<td><h:outputText value="#{avisoProjeto.obj.titulo}" /></td>
	</tr>

	<tr>
		<th>Texto do Aviso: </th>
		<td>
			<h:outputText id="descricao" value="#{avisoProjeto.obj.descricao}"	escape="false" style="width:97%;"/>
		</td>
	</tr>
	
	

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{avisoProjeto.voltarListaAviso}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

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