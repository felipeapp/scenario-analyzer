<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
<f:view>
	<div id="mensagem">
		<h:form id="form">

		<table class="visualizacao" width="100%">
			<h:outputText value="#{producao.dadosValidacao}" />
			<caption class="listagem">Participação em Comissão de Colegiado</caption>
			<h:inputHidden value="#{participacaoColegiadoComissao.obj.id}" />
			<h:inputHidden value="#{participacaoColegiadoComissao.validar}" />
			<c:if test="${not empty participacaoColegiadoComissao.obj.dataValidacao}">
				<tr>
					<c:if test="${participacaoColegiadoComissao.obj.validado}">
						<td colspan="4" class="prodValida">Produção Válida</td>
					</c:if>
					<c:if test="${not participacaoColegiadoComissao.obj.validado}">
						<td colspan="4" class="prodInvalida">Produção Inválida</td>
					</c:if>
				</tr>
			</c:if>
			<tr>
				<th>Departamento:</th>
				<td colspan="3"><h:outputText value="#{participacaoColegiadoComissao.obj.departamento.nome}" /></td>
			</tr>
			<tr>
				<th>Instituição:</th>
				<td colspan="3"><h:outputText value="#{participacaoColegiadoComissao.obj.instituicao.nome}" /></td>
			</tr>
			<tr>
				<th>Comissão:</th>
				<td colspan="3"><h:outputText value="#{participacaoColegiadoComissao.obj.comissao}" /></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td colspan="3"><h:outputText value="#{participacaoColegiadoComissao.obj.informacao}" /></td>
			</tr>
			<tr>
				<th>Ano de Referência:</th>
				<td><h:outputText value="#{participacaoColegiadoComissao.obj.anoReferencia}" /></td>
			</tr>
			<tr>
				<th>Período Início:</th>
				<td><fmt:formatDate value="${participacaoColegiadoComissao.obj.periodoInicio}" pattern="MM/yyyy" /></td>
				<th>Período Fim:</th>
				<td><fmt:formatDate value="${participacaoColegiadoComissao.obj.periodoFim}" pattern="MM/yyyy" /></td>
			</tr>

			<tr>
				<th>Tipo de comissão:</th>
				<td><h:outputText value="#{participacaoColegiadoComissao.obj.tipoComissaoColegiado.descricao}"/></td>
				<th>Número de Reuniões:</th>
				<td><h:outputText value="#{participacaoColegiadoComissao.obj.numeroReunioes}" /></td>
			</tr>
			<tr>
				<th>Membro Nato:</th>
				<td><t:selectBooleanCheckbox value="#{participacaoColegiadoComissao.obj.nato}" readonly="true" /></td>
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
	//Atencao se for dado include em "rodape.jsp" essa página irá dar erro no Internt Explorer
</script>
<%--
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
--%>