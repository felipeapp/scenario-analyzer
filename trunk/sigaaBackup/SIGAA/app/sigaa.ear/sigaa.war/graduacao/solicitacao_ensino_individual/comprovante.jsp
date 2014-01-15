<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<c:set value="#{solicitacaoEnsinoIndividual.discente }" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>

	<c:if test="${not empty solicitacaoEnsinoIndividual.solicitacoes}">
	<br>
	<table class="listagem" style="width: 100%">
		<caption>Solicitações de turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</caption>
		<thead>
			<tr>
				<td>Nº Solicitação</td>
				<td>Componente</td>
				<c:if test="${not solicitacaoEnsinoIndividual.ferias}">
				<td>Sugestão de Horário</td>
				</c:if>
				<td>Data Solicitação</td>
				<td>Situação</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{solicitacaoEnsinoIndividual.solicitacoes}" var="sol" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small; border-top: thin dashed;">
				<td>${sol.numeroSolicitacao}</td>
				<td>${sol.componente}</td>
				<c:if test="${not solicitacaoEnsinoIndividual.ferias}">
					<td>${sol.sugestaoHorario}</td>
				</c:if>
				<td width="12%"><ufrn:format type="data" valor="${sol.dataSolicitacao}" /></td>
				<td width="12%">${sol.situacaoString}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	</div>
	<br/>
	<div>
		<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	</div>
</f:view>