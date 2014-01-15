<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{participantesDasBancas.create}" />

	<h2><ufrn:subSistema /> &gt; ${participantesDasBancas.titulo}</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="participantesDasBancas" />
		<table class="formulario" style="width: 95%">
			<caption>Informe os critérios para a emissão do relatório</caption>
			<c:choose>
				<c:when test="${acesso.ppg}">
					<tr>
						<th class="required">Programa:</th>
						<td><h:selectOneMenu id="programa"
							value="#{participantesDasBancas.unidade.id}">
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
						</h:selectOneMenu></td>
					</tr>
				</c:when>
				<c:when test="${not acesso.ppg}">
					<tr>
						<th><b>Programa:</b></th>
						<td><h:outputText
							value="#{participantesDasBancas.unidade.nome}" /></td>
					</tr>
				</c:when>
			</c:choose>

			<tr>
				<th class="required">Ano/Período:</th>
				<td><h:inputText value="#{participantesDasBancas.anoBanca}" id="anoBanca"
					size="5" maxlength="4" onkeyup="return formatarInteiro(this);"
					converter="#{ intConverter }" />.<h:inputText
					value="#{participantesDasBancas.periodoBanca}" id="periodoBanca" size="2"
					maxlength="1" onkeyup="return formatarInteiro(this);"
					converter="#{ intConverter }" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{participantesDasBancas.gerarRelatorio}" value="Emitir Relatório" />
						<h:commandButton action="#{participantesDasBancas.cancelar}" value="Cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>