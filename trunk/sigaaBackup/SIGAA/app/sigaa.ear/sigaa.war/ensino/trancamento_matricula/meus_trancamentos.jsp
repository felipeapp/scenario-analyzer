<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<h:outputText value="#{trancamentoMatricula.create}"/>
	<h:outputText value="#{motivoTrancamento.create}"/>

	<h2> Solicitação de Trancamento de Matrícula </h2>

	<div class="descricaoOperacao">
		<p>
		Caro Aluno,
		</p><br>
		<p>
		Nesta operação você pode visualizar as solicitações de trancamento realizada e acompanhar a situação de cada uma.
		</p>
		<p>
		A matrícula em uma disciplina só é trancada depois de passar 7 dias corridos após a solicitação,
		caso esta seja feita até 7 dias antes do prazo máximo para trancamento definido no calendário universitário.
		Nesse caso, é possível cancelar a solicitação até o fim destes 7 dias.
		</p>
		<p>
		No caso de a solicitação ser feita em prazo inferior a 7 dias do prazo máximo para trancamento,
		a disciplina será definitivamente trancada no dia seguinte ao fim desse prazo.
		Nesse caso, o período de cancelamento da solicitação se encerra no último dia definido no calendário universitário para trancamento.
		</p>
	</div>

	<c:set var="discente" value="#{trancamentoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/cronograma/remover.gif"style="overflow: visible;"/>: Cancelar Solicitação de Trancamento
		</div>
	</center>
	<br/>

	<c:set var="responsavelVisto" value="Coordenador"/>
	<c:if test="${trancamentoMatricula.discente.stricto}">
		<c:set var="responsavelVisto" value="Orientador"/>
	</c:if>

	<table class="listagem">
		<caption>Solicitações de Trancamento Realizadas</caption>
		<thead>
		<tr>
			<th width="15%"> Ano.Periodo </th>
			<th> Componente </th>
			<th> Turma</th>
			<th> Status</th>
			<th> Data Solicitação</th>
			<th width="10%"> 
				Data Visto ${responsavelVisto}
			</th>
			<td width="3%"></td>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="solicitacao" items="${trancamentoMatricula.solicitacoes}" varStatus="status">

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${solicitacao.matriculaComponente.anoPeriodo } </td>
					<td> ${solicitacao.matriculaComponente.componenteDescricaoResumida} </td>
					<td align="center">${solicitacao.matriculaComponente.turma.codigo}</td>
					<td align="center">${solicitacao.situacaoString}</td>
					<td>
						<ufrn:format type="data" valor="${solicitacao.dataCadastro}" />
					</td>
					<td>
						<ufrn:format type="data" valor="${solicitacao.dataAtendimento}" />
					</td>
					<td>
						<c:if test="${solicitacao.podeCancelar && trancamentoMatricula.periodoTrancamentoTurmas}">
						<h:form>
							<input type="hidden" value="${solicitacao.id}" name="id">
							<h:commandButton image="/img/cronograma/remover.gif" styleClass="noborder" title="Cancelar Solicitação" value="Cancelar Solicitação"
							action="#{trancamentoMatricula.resumirCancelamentoSolicitacao}"/>
						</h:form>
						</c:if>
					</td>
				</tr>



				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>Motivo do Trancamento:</td>
					<td colspan="6"><b>
						<c:if test="${not empty solicitacao.justificativa}">
							${solicitacao.justificativa}
						</c:if>
						<c:if test="${empty solicitacao.justificativa}">
							${solicitacao.motivo.descricao}
						</c:if>
					</b></td>
				</tr>

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<c:if test="${not empty solicitacao.replica}">
					<td>Orientação do ${responsavelVisto}:</td>
					<td colspan="6"><b>${solicitacao.replica}<b	></td>
				</c:if>
				</tr>

			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="7" align="center">
				<h:form>
					<h:commandButton value="Retornar ao menu principal" onclick="#{confirm}" action="#{trancamentoMatricula.cancelar}" />
				</h:form>
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>