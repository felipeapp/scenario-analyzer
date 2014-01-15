<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	table.listagem tr.turma td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>
<f:view>

<c:if test="${not empty relatorioDocentesTurmaBean.docentes}">
<br />
<table class="listagem" width="100%">
	<h3> Relatório de Docentes Por Turma </h3>
	<thead>
	<tr>
		<th> Docente </th>
		<th> Ch </th>
	</tr>
	<tbody>
		<h:form>
		<c:set var="idFiltro" value="-1" />

		<c:forEach items="#{relatorioDocentesTurmaBean.docentes}" var="docente" varStatus="status">

			<c:set var="idLoop" value="${docente.turma.id}" />
			
			<c:if test="${ idFiltro != idLoop}">
				<c:set var="idFiltro" value="${docente.turma.id}" />
				<tr class="turma">
					<td colspan="2">
						${docente.turma.disciplina} - T${docente.turma.codigo} - ${docente.turma.chTotalTurma}h
					</td>
				</tr>
			</c:if>
	
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${docente.docenteDescricao}</td>
				<td>${docente.chDedicadaPeriodo}h</td>
			</tr>
		</c:forEach>

		</h:form>
	</tbody>
</table>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>