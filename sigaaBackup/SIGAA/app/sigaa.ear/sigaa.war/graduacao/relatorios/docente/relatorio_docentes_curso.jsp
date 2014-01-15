<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.tabelaRelatorioBorda tr.lotacao td { border-bottom: 1px solid black; font-weight: bold; padding: 10px 2px 2px 10px; border-width: 1px 0;}
</style>

<f:view>
	<h2> Relatório de Docentes Vinculados a um Curso </h2>

	<div id="parametrosRelatorio">
	<table width="100%">
		<tr>
			<th width="20%">Ano:</th>
			<td>${relatorioDocentesPorCursoBean.ano}</td>
		</tr>
		<tr>
			<th>Curso:</th>
			<td><h:outputText value="#{relatorioDocentesPorCursoBean.curso.descricao}" /></td>
		</tr>
		<tr>
			<th>Total de docentes:</th>
			<td>${fn:length(resultado)}</td>
		</tr>
	</table>
	</div>
	
	<br />
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th rowspan="2">Docente </th>
				<th rowspan="2">Regime </th>
				<th rowspan="2">Titulação </th>
				<th colspan="3" style="text-align: center;">CH Semanal</th>
			</tr>
			<tr>
				<th>${relatorioDocentesPorCursoBean.ano}.1</th>
				<th>${relatorioDocentesPorCursoBean.ano}.2</th>
				<th>Total</th>
			<tr>
		<tbody>
			<c:set var="_lotacao" />
			<c:forEach items="${resultado}" var="linha">
			
				<c:if test="${_lotacao != linha.lotacao}">
					<c:set var="_lotacao" value="${linha.lotacao}"/>
					<tr class="lotacao">  
						<td colspan="7">${_lotacao} </td>
					</tr>				
				</c:if>
			
				<tr>
					<td>${linha.nome}</td>
					<td align="right">${linha.regime_trabalho}</td>
					<td>${linha.titulacao}</td>
					<td align="right">${linha.ch1}</td>
					<td align="right">${linha.ch2}</td>
					<td align="right">${linha.ch1 + linha.ch2}</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="7" align="center">${fn:length(resultado)} docentes</td>
			<tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
