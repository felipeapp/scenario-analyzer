<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:outputText value="#{ relatorioOcupacaoTurma.create }" />
	<c:set var="relatorio" value="${ relatorioOcupacaoTurma.relatorio }"/>

	<h2>Relatório de Ocupação de Turma</h2>
	<table class="listagem">
	<caption>Turmas encontradas</caption>
		<thead>
			<th>Cód. Disciplina</th>
			<th>Nome da Disciplina</th>
			<th>Cód. Turma</th>
			<th>Capacidade</th>
			<th>Matriculados</th>
			<th>Ocupação (%)</th>
		</thead>
		<c:forEach var="linha" items="${ relatorio }" varStatus="status">
			<c:forEach var="turma" items="${ linha.value.turmas }">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center"> ${ linha.value.codigoDisciplina } </td>
					<td align="center"> ${ linha.key } </td>
					<td align="center"> ${ turma.key } </td>
					<td align="center"> ${ turma.value.capacidadeAluno } </td>
					<td align="center"> ${ turma.value.totalMatriculados } </td>
					<c:set var="cap" value="${ turma.value.capacidadeAluno }"/>
					<c:set var="mat" value="${ turma.value.totalMatriculados }"/>
					<td align="center"> ${ (mat/cap)*100 }</td>
				</tr>
			</c:forEach>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
