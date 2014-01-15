<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h:outputText value="#{trancamentoMatricula.create}"/>

	<h2> Comprovante de Trancamento de Matrícula <br>
	Número da Solicitação: <big> ${trancamentoMatricula.numeroSolicitacao} </big> </h2>

	<c:set var="discente" value="#{trancamentoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<table class="listagem">
		<caption>Disciplinas Trancadas</caption>
		<thead>
		<tr>
			<th> Componente </th>
			<th> Turma</th>
			<th> Horário</th>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="matricula" items="${trancamentoMatricula.matriculasEscolhidas}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style=" font-weight: bold">
				<td> ${matricula.matriculaComponente.componenteDescricao}</td>
				<td align="center">${matricula.matriculaComponente.turma.codigo}</td>
				<td align="center">${matricula.matriculaComponente.turma.descricaoHorario}</td>
			</tr>
			
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td  align="right">Motivo do Trancamento:</td>
				<td colspan="2" style="font-style: italic; font-size: x-small;">
					<c:if test="${not empty matricula.justificativa}">
						${matricula.justificativa}
					</c:if>
					
					<c:if test="${empty matricula.justificativa}">
						<c:forEach var="mot" items="#{motivoTrancamento.allExibir}">					
							<c:if test="${mot.id == matricula.motivo.id}">
								${mot.descricao}
							</c:if>
						 </c:forEach>
					</c:if>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>