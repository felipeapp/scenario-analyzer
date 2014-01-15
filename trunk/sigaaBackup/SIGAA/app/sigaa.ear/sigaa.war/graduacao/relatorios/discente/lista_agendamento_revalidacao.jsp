<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de Inscritos para Revalidação de Diplomas</b></caption>
			<c:if test="${not empty solRevalidacaoDiploma.dataSelecionada}">
			<tr>
				<th>Data/Horário:</th>
				<td colspan="3" width="80%">
					<b>
						<h:outputText value="#{solRevalidacaoDiploma.dataSelecionada}"/> 
						<c:if test="${not empty solRevalidacaoDiploma.horarioSelecionado}">
						 	às <h:outputText value="#{solRevalidacaoDiploma.horarioSelecionado}"/>
						</c:if>
					</b>
				</td>
			</tr>
			</c:if>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;" class="relatorio">
		<caption>Total de Registros: ${fn:length(solRevalidacaoDiploma.solicitacoesRevalidacaoDiploma)}</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Nome</th>
				<th>Cpf / Passaporte</th>
				<th>E-mail</th>
				<th>Data</th>
				<th>Horário</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${solRevalidacaoDiploma.solicitacoesRevalidacaoDiploma}" var="linha">
				<tr>
					<td align="left"> ${linha.nome}</td>
					<td align="center">
					<c:choose>
						<c:when test="${linha.cpf>0}">
							<ufrn:format type="cpf_cnpj" valor="${linha.cpf}"/>
						</c:when>
						<c:otherwise>
							${linha.passaporte}
						</c:otherwise>
					</c:choose>
					</td>
					<td align="left"> ${linha.email}</td>
					<td align="center"> <ufrn:format type="data" valor="${linha.agendaRevalidacaoDiploma.data}"/></td>
					<td align="center"> ${linha.agendaRevalidacaoDiploma.horario} </td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>