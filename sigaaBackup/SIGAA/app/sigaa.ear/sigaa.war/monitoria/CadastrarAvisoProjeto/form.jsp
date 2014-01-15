<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{avisoProjeto.create}"/>

	<h2><ufrn:subSistema /> > Avisos do Projeto</h2>
	<h:form>

	<div class="descricaoOperacao">
		<font color="red">Atenção!</font><br/>
		Este aviso será enviado por e-mail para TODOS os docentes e discentes ativos do projeto de ensino.		
	</div>
	
	<br/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Aviso para Projetos </caption>
	
	
	<tr>
		<th width="15%"> Projeto: </th>
		<td><b><h:outputText value="#{avisoProjeto.obj.projeto.anoTitulo}" /></b></td>
	</tr>
	
	<tr>
		<th> Assunto: </th>
		<td><h:inputText value="#{avisoProjeto.obj.titulo}" size="70" maxlength="80"/></td>
	</tr>

	<tr>
		<td colspan="2" align="center">
			<b>Texto do Aviso</b>
			<h:inputTextarea id="descricao" value="#{avisoProjeto.obj.descricao}"	rows="10" style="width:97%;"/>
		</td>
	</tr>
	
	
	<%--
		//A publicação do aviso nos portais foi substituída pelo envio de uma mensagem para todos os participantes ativos do projeto
	
		<tr>
			<th width="20%"> Data de Validade: </th>
			<td> <t:inputCalendar value="#{avisoProjeto.obj.dataValidade}"  renderAsPopup="true" size="10" readonly="#{avisoProjeto.readOnly}" popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)"  maxlength="10"/>
			</td>
		</tr>

		<tr>
			<th width="20%"> Publicar: </th>
			<td> <h:selectBooleanCheckbox value="#{avisoProjeto.obj.publicar}" styleClass="noborder" readonly="#{avisoProjeto.readOnly}"/>
			</td>
		</tr>
	--%>

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{avisoProjeto.confirmButton}" action="#{avisoProjeto.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{avisoProjeto.cancelar}"/>
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