<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > ${relatoriosVestibular.nomeRelatorio}</h2>

	<h:form id="form">
		<h:inputHidden value="#{relatoriosVestibular.nomeRelatorio}" />
		<table align="center" class="formulario" width="55%">
			<caption class="listagem">Parâmetros do Relatório</caption>
			<tr>
				<th width="30%" class="obrigatorio">Processo Seletivo:</th>
				<td width="70%">
					<h:selectOneMenu id="processoSeletivo" immediate="true"
						value="#{relatoriosVestibular.idProcessoSeletivo}"
						valueChangeListener="#{relatoriosVestibular.carregaConvocacaoProcessoSeletivo}"
						onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Convocação:</th>
				<td width="70%"><h:panelGrid id="local">
					<h:selectOneMenu immediate="true"
						value="#{relatoriosVestibular.idConvocacaoProcessoSeletivo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatoriosVestibular.convocacoesProcessoSeletivo}" />
					</h:selectOneMenu>
				</h:panelGrid></td>
			</tr>
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