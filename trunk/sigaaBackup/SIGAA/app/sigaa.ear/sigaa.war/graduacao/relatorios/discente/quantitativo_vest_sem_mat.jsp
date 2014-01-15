<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.discente td {
		padding: 1px;
		border-bottom: 1px dashed #CCC;
	}
</style>

<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>

	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="cidadeLoop"/>
	
	<c:forEach items="${resultado}" var="linha">
		<c:set var="cursoAtualLoop" value="${linha.id_curso}"/>
		<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:set var="cidadeAtual" value="${linha.id_municipio}"/>
		
		<c:if test="${cursoLoop != cursoAtualLoop || turnoLoop != turnoAtual || cidadeLoop != cidadeAtual}">
			<c:set var="cursoLoop" value="${cursoAtualLoop}"/> 
			<c:set var="turnoLoop" value="${turnoAtual}"/>
			<c:set var="cidadeLoop" value="${cidadeAtual}"/>
			<tr>
				<td colspan="5">
					<br>
					<b>${linha.centro_sigla} - ${linha.curso_nome} - ${linha.turno_sigla} - ${linha.municipio} </b>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Aluno</b></td>
				<td><b>Status</b></td>
			<tr>
		</c:if>
		<tr class="discente">
			<td> ${linha.matricula} -  ${linha.aluno_nome} </td>
			<td> ${linha.discente_status} </td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
