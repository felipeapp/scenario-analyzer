<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2>Turmas Consolidadas</h2>
	<h:outputText value="#{portalDocente.create}" />

	<table class="listagem" width="80%">
		<thead>
		<tr>
			<td>C�digo </td>
			<td>Disciplina</td>
			<td>Ano/Semestre</td>
			<td>Cr�ditos</td>
			<td>Hor�rio</td>
		</tr>
		</thead>
		<c:forEach items="${portalDocente.turmas}" var="turma">
			<tr>
				<td>${turma.disciplina.codigo}</td>
				<td>${turma.disciplina.nome}</td>
				<td>${turma.ano}.${turma.periodo}</td>
				<td>${turma.disciplina.detalhes.crTotal}</td>
				<td> </td>
			</tr>
		</c:forEach>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
