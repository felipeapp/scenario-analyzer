<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
<f:view>
	<div id="mensagem">
		<h:form id="form">

		<table class="visualizacao" width="100%">
			<h:outputText value="#{producao.dadosValidacao}" />
			<caption class="listagem">Texto de Discussão</caption>
			<h:inputHidden value="#{textoDiscussao.obj.id}" />
			<h:inputHidden value="#{textoDiscussao.validar}" />
			<c:if test="${not empty textoDiscussao.obj.dataValidacao}">
				<tr>
					<c:if test="${textoDiscussao.obj.validado}">
						<td colspan="4" class="prodValida">Produção Válida</td>
					</c:if>
					<c:if test="${not textoDiscussao.obj.validado}">
						<td colspan="4" class="prodInvalida">Produção Inválida</td>
					</c:if>
				</tr>
			</c:if>
			<tr>
				<th>Título:</th>
				<td colspan="3"><h:outputText value="#{textoDiscussao.obj.titulo}" /></td>
			</tr>
			<tr>
				<th>Autores:</th>
				<td><h:outputText value="#{textoDiscussao.obj.autores}" /></td>
			</tr>
			<tr>
				<th>Tipo de Participação:</th>
				<td colspan="3"><h:outputText value="#{textoDiscussao.obj.tipoParticipacao.descricao}" /></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td colspan="3"><h:outputText value="#{textoDiscussao.obj.informacao}" /></td>
			</tr>
			<tr>
				<td colspan="4"><hr /></td>
			</tr>
			<tr>
				<th>Área:</th>
				<td><h:outputText value="#{textoDiscussao.obj.area.nome}" /></td>
				<th>Sub-Área:</th>
				<td><h:outputText  value="#{textoDiscussao.obj.subArea.nome}" /></td>
			</tr>
			<tr>
				<th>Ano de Referência:</th>
				<td><h:outputText value="#{textoDiscussao.obj.anoReferencia}" /></td>
				<th>Local de Publicação:</th>
				<td><h:outputText value="#{textoDiscussao.obj.localPublicacao}" /></td>
			</tr>
			<tr>
				<th>Página Inicial</th>
				<td><h:outputText value="#{textoDiscussao.obj.paginaInicial}" /></td>
				<th>Página Final</th>
				<td><h:outputText value="#{textoDiscussao.obj.paginaFinal}" /></td>
			</tr>
			<tr>
				<th>Destaque:</th>
				<td><h:selectBooleanCheckbox value="#{textoDiscussao.obj.destaque}" disabled="true" /></td>
			</tr>
			<tr>
				<td colspan="4" align="center">
				<table class="subFormulario" width="100%">
					<caption class="subFormulario">Quantitativos</caption>
					<tr>
						<td width="10%" nowrap="nowrap">Docentes (incluindo você):</td>
						<td><h:outputText value="#{textoDiscussao.obj.numeroDocentes}"/></td>
						<td width="10%" nowrap="nowrap">Doc. de outros Departamentos:</td>
						<td colspan="3"><h:outputText value="#{textoDiscussao.obj.numeroDocentesOutros}" /></td>
					</tr>
					<tr>
						<td width="10%" nowrap="nowrap">Estudantes:</td>
						<td><h:outputText value="#{textoDiscussao.obj.numeroEstudantes}" /></td>
						<td width="10%" nowrap="nowrap">Técnicos:</td>
						<td><h:outputText value="#{textoDiscussao.obj.numeroTecnicos}" /></td>
						<td width="10%" nowrap="nowrap">Outros:</td>
						<td><h:outputText  value="#{textoDiscussao.obj.numeroOutros}" /></td>
					</tr>
				</table>
				</td>
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
