<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
<f:view>
	<div id="mensagem">
		<h:form id="form">
		<table class="visualizacao" width="100%">
			<h:outputText value="#{producao.dadosValidacao}" />
			<h:inputHidden value="#{producaoArtisticaLiterariaVisual.obj.id}" />
			<h:inputHidden value="#{producaoArtisticaLiterariaVisual.validar}" />
			<c:if test="${not empty producaoArtisticaLiterariaVisual.obj.dataValidacao}">
				<tr>
					<c:if test="${producaoArtisticaLiterariaVisual.obj.validado}">
						<td colspan="4" class="prodValida">Produ��o V�lida</td>
					</c:if>
					<c:if test="${not producaoArtisticaLiterariaVisual.obj.validado}">
						<td colspan="4" class="prodInvalida">Produ��o Inv�lida</td>
					</c:if>
				</tr>
			</c:if>
			<tr>
				<td width="20%"><b>T�tulo:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<td><b>Autores:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.autores}" /></td>
			</tr>
			<tr>
				<td><b>Tipo de Participa��o:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.tipoParticipacao.descricao}" /></td>
			</tr>

			<tr>
				<td><b>Informa��es Complementares:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.informacao}" /></td>
			</tr>
		
			<tr>
				<td><b>Premiada:</b></td>
				<td>${producaoArtisticaLiterariaVisual.obj.premiada ? "Sim":"N�o"}</td>
			</tr>
			
			<tr>
				<td><b>�mbito</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.tipoRegiao.descricao }" /></td>
			</tr>

			<tr>
				<td><b>�rea:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.area.nome}" /></td>
			</tr>
			<tr>
				<td><b>Sub-�rea:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.subArea.nome}" /></td>
			</tr>
			<tr>
				<td><b>Tipo Art�stico:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.tipoArtistico.descricao}" /></td>
			</tr>
			<%-- SubTipoArtisticoGenerico para AudioVisual id = 99 --%>
			<c:if test="${producaoArtisticaLiterariaVisual.obj.subTipoArtistico.id != 99}">
			 <c:if test="${producaoArtisticaLiterariaVisual.obj.subTipoArtistico.id >0}">
				<tr>
					<td><b>Sub-Tipo Art�stico:</b></td>
					<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.subTipoArtistico.descricao}" /> 
						${producaoArtisticaLiterariaVisual.obj.subTipoArtistico.id}</td>
				</tr>
			 </c:if>
			</c:if>
			
			<tr>
				<td><b>Ano de Refer�ncia</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.anoReferencia}" /></td>
			</tr>
			<tr>
				<td><b>Data In�cio:</b></td>
				<td><fmt:formatDate value="${producaoArtisticaLiterariaVisual.obj.periodoInicio}" pattern="MM/yyyy" /></td>
			</tr>
			<tr>
				<td><b>Data Fim:</b></td>
				<td><fmt:formatDate  value="${producaoArtisticaLiterariaVisual.obj.periodoFim }" pattern="MM/yyyy" /></td>
			</tr>
			<tr>
				<td><b>Local:</b></td>
				<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.local}" /></td>
			</tr>
			
			<tr>
				<td colspan="4" align="center">
				<table class="subFormulario" width="100%">
					<caption class="subFormulario">Quantitativos</caption>
					<tr>
						<td width="10%" nowrap="nowrap">Docentes (incluindo voc�):</td>
						<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.numeroDocentes}" /></td>
						<td width="10%" nowrap="nowrap">Doc. de outros Departamentos:</td>
						<td colspan="3"><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.numeroDocentesOutros}" /></td>
					</tr>
					<tr>
						<td width="10%" nowrap="nowrap">Estudantes:</td>
						<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.numeroEstudantes}" /></td>
						<td width="10%" nowrap="nowrap">T�cnicos:</td>
						<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.numeroTecnicos}" /></td>
						<td width="10%" nowrap="nowrap">Outros:</td>
						<td><h:outputText value="#{producaoArtisticaLiterariaVisual.obj.numeroOutros}" /></td>
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
	//Atencao se for dado include em "rodape.jsp" essa p�gina ir� dar erro no Internt Explorer
</script>
<%--
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
--%>

