<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

	<h2> Alunos Cadastrados - ${sessionScope.usuario.unidade.nome}</h2>
	<table class="listagem" >
		<caption>${fn:length(discentes) } Discentes Encontrados</caption>

		<c:if test="${empty discentes}">
			<tr><td>Nenhum aluno encontrado</td></tr>
		</c:if>
		<c:if test="${not empty discentes}">
			<thead>
				<tr>
					<td width="10%">Matrícula</td>
					<td>Nome</td>
					<td width="15%">Situação</td>
				</tr>
			</thead>
		<c:forEach items="${discentes}" var="d" varStatus="s">
			<tbody>
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td> ${d.matricula } </td>
					<td> ${d.nome} </td>
					<td> ${d.statusString} </td>
				</tr>
			</tbody>
		</c:forEach>
		</c:if>
	</table>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
