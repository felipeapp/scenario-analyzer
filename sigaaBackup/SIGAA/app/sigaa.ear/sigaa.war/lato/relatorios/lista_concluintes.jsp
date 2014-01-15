<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de Alunos Concluintes - Pós-Graduação Lato Sensu</b></caption>
		<tr>
			<td></td>
		</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatoriosLato.numeroRegistrosEncontrados}"/></b></caption>
	<c:set var="curso_loop"/>
	<c:forEach items="${relatoriosLato.lista}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:if test="${curso_loop != cursoAtual}">
			<c:set var="curso_loop" value="${cursoAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.curso}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td>Matrícula</td>
				<td>Nome</td>
			<tr>
		</c:if>
		<tr>
			<td>${linha.matricula}</td>
			<td>${linha.nome}</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
