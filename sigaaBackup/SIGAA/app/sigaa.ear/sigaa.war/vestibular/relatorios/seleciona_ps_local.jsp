<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > ${relatoriosVestibular.nomeRelatorio}</h2>

	<h:form id="form">
		<h:inputHidden value="#{relatoriosVestibular.nomeRelatorio}" />
		<table align="center" class="formulario" width="55%">
			<caption class="listagem">Dados do Relatório</caption>
			<tr>
				<th width="30%" class="obrigatorio">Processo Seletivo:</th>
				<td width="70%">
					<h:selectOneMenu id="processoSeletivo" immediate="true"
						value="#{relatoriosVestibular.idProcessoSeletivo}"
						valueChangeListener="#{relatoriosVestibular.carregaLocalAplicacao}"
						onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Local de Aplicação:</th>
				<td width="70%"><h:panelGrid id="local">
					<h:selectOneMenu immediate="true"
						value="#{relatoriosVestibular.idLocalAplicacaoProva}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatoriosVestibular.locaisAplicacao}" />
					</h:selectOneMenu>
				</h:panelGrid></td>
			</tr>
			<c:if test="${relatoriosVestibular.requerTitularReserva}">
				<tr>
					<th class="obrigatorio">Titularidade:</th>
					<td width="70%">
						<h:selectOneMenu immediate="true"
							value="#{relatoriosVestibular.titularReserva}">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
							<f:selectItem itemValue="1" itemLabel="SOMENTE RESERVAS" />
							<f:selectItem itemValue="2" itemLabel="SOMENTE TITULARES" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosVestibular.gerarRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{relatoriosVestibular.cancelar}" id="cancelar" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>