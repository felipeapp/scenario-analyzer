<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
<f:view>
	<div id="mensagem">
	<h:form id="form">

		<table class="visualizacao" width="100%">
			<h:outputText value="#{producao.dadosValidacao}" />
			<caption class="listagem">Apresenta��o Em Evento</caption>
			<h:inputHidden value="#{apresentacaoEmEvento.obj.id}" />
			<h:inputHidden id="validar" value="#{apresentacaoEmEvento.validar}" />
			<c:if test="${not empty publicacaoEvento.obj.dataValidacao}">
				<tr>
					<c:if test="${apresentacaoEmEvento.obj.validado}">
						<td colspan="4" class="prodValida">Produ��o V�lida</td>
					</c:if>
					<c:if test="${not apresentacaoEmEvento.obj.validado}">
						<td colspan="4" class="prodInvalida">Produ��o Inv�lida</td>
					</c:if>
				</tr>
			</c:if>
			<tr>
				<th>T�tulo:</th>
				<td colspan="3"><h:outputText value="#{apresentacaoEmEvento.obj.titulo}"/></td>
			</tr>
			<tr>
				<th>Tipo de Evento:</th>
				<td><h:outputText value="#{apresentacaoEmEvento.obj.tipoEvento.descricao}"/></td>
				<th>�mbito:</th>
				<td><h:outputText value="#{apresentacaoEmEvento.obj.tipoRegiao.descricao}"/></td>
			</tr>
			<tr>
				<th>Tipo de Participa��o:</th>
				<td colspan="3"><h:outputText value="#{apresentacaoEmEvento.obj.tipoParticipacao.descricao}" /></td>
			</tr>
			<tr>
				<th>�rea:</th>
				<td colspan="3"><h:outputText value="#{apresentacaoEmEvento.obj.area.nome}"/></td>
			</tr>
			<tr>
				<th>Sub-�rea:</th>
				<td colspan="3"><h:outputText value="#{apresentacaoEmEvento.obj.subArea.nome}"/></td>
			</tr>
			<tr>
				<th>Informa��o:</th>
				<td colspan="3"><h:outputText value="#{apresentacaoEmEvento.obj.informacao}"/></td>
			</tr>
			<tr>
				<th><font color="red">Tipo:</font></th>
				<td><h:outputText value="#{apresentacaoEmEvento.obj.tipo}" /></td>
				<th>Local:</th>
				<td><h:outputText value="#{apresentacaoEmEvento.obj.local}" /></td>
			</tr>
			<tr>
				<th>Ano de Refer�ncia:</th>
				<td><h:outputText value="#{apresentacaoEmEvento.obj.anoReferencia}" /></td>
				<th>Evento:</th>
				<td><h:outputText value="#{apresentacaoEmEvento.obj.evento}" /></td>
			</tr>
		</table>
	</h:form>
	</div>
</f:view>
<script type="text/javascript">
	var form = document.getElementById('form');

	var width=0;
	var height=0;
	
	//Firefox
	try 
    {
		formReadOnly = getComputedStyle(form,'');
		width =parseInt(formReadOnly.width)+70;
		height = parseInt(formReadOnly.height)+60;
	}
	catch(err) //Err = Internet Explorer
	{
		form.offsetWidth;
		width = parseInt(form.offsetWidth)+70;
		height = parseInt(form.offsetHeight)+60;
		if (form.offsetHeight <=0)
			height=parseInt(width)-170;
	}
	painel.resizeTo(width, height);
	//Atencao se for dado include em "rodape.jsp" essa p�gina ir� dar erro no Internt Explorer
</script>
<%--
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
--%>