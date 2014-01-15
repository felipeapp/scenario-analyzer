<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
table.listagem tr.linhaPesquisa td {
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}
</style>

<f:view>

	<c:if test="${not empty relatorioDiscentesLinhaPesquisa.discentes}">
		<center>
		<h3>RELATÓRIO DE DISCENTES POR LINHA DE PESQUISA</h3>
		</center>
		<br/>
			<c:if test="${!acesso.ppg}">
				<strong>Programa:</strong> 	${relatorioDiscentesLinhaPesquisa.programaStricto}
			</c:if>
		<hr />
		<table class="listagem" width="100%">
			<thead>
				<tr>
					<th style="text-align: right;" width="15%">Matrícula</th>
					<th width="4%"></th>
					<th colspan="2" style="text-align: left;">Aluno</th>
				</tr>
			</thead>
			<tbody>
				<h:form id="form">
					<c:set var="idFiltro" value="-1" />
					<c:forEach items="#{relatorioDiscentesLinhaPesquisa.discentes}" var="discente" varStatus="status">
						<c:set var="idLoop" value="#{discente.linha.id}" />
						
						<c:if test="${idFiltro != idLoop}">
							
							<c:set var="idFiltro" value="#{discente.linha.id}" />
							
							<tr class="linhaPesquisa">
								<td colspan="3">${discente.linha.denominacao}</td>
								<td style="text-align: left">Nível: ${discente.area.nivelDesc}</td>
							</tr>
						</c:if>
						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td align="right">${discente.matricula}</td>
							<td></td>
							<td align="left">${discente.pessoa.nome}</td>
							<td></td>
						</tr>
					</c:forEach>
				</h:form>
			</tbody>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>