<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.valor {font-weight: normal;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #AAA;}
	tr.unidade td {font-weight: bold; border: 1px solid #666; border-width: 1px 0; background: #EEE;}
	tr.total td {border-top: 1px solid #666;}
	tr.totalGeral td {font-weight: bold; padding: 3px; border: 1px solid #666; border-width: 1px 0; background: #F5F5F5;}
</style>

<f:view>
	<h2> Relatório Quantitativo de Solicitações de Matrículas ${calendarioAcademico.anoPeriodo } </h2>
	
	<br />
	<table width="100%">
	<thead>
		<tr class="header">
			<th style="text-align: left;">Curso</th>
			<th style="text-align: left;">Município</th>
			<th>Solicitantes</th>
			<th>Ativos</th>
			<th>Percentual</th>
		</tr>
	</thead>
	
	<c:set var="_unidade" value=""/>
	<c:set var="_totalMatriculadosUnidade" value="0"/>
	<c:set var ="_totalMatriculados" value="0"/>
	<c:set var="_totalAtivosUnidade" value="0"/>
	<c:set var ="_totalAtivos" value="0"/>
	<c:forEach items="${relatorioCurso.listaCurso}" var="linha" varStatus="loop">
	
		<c:if test="${linha.nome_unidade != _unidade}">
			<c:if test="${not loop.first}">
				<tr class="total">
					<td colspan="2">TOTAIS</td>
					<td class="valor"> <ufrn:format valor="${ _totalMatriculadosUnidade }" type="valorint"/> </td>
					<td class="valor"> <ufrn:format valor="${ _totalAtivosUnidade }" type="valorint"/></td>
					<td class="valor"> <ufrn:format valor="${(_totalMatriculadosUnidade / _totalAtivosUnidade) * 100}" type="valorint"/>% </td>
				</tr>
				<tr> 
					<td colspan="5" style="height: 15px;"> </td>
				</tr>
			</c:if>

			<c:set var="_totalMatriculadosUnidade" value="0"/>
			<c:set var="_totalAtivosUnidade" value="0"/>
			<c:set var="_unidade" value="${linha.nome_unidade}"/>
			
			<tr class="unidade">
				<td colspan="5"> ${_unidade} </td>			
			</tr>
		</c:if>
	
		<tr class="componentes">
			<td> ${linha.curso_nome} </td>
			<td> ${linha.municipio_nome} </td>
			<td class="valor"> <ufrn:format valor="${linha.total_matriculados}" type="valorint"/> </td>
			<td class="valor"> <ufrn:format valor="${linha.total_alunos}" type="valorint"/> </td>
			<td class="valor"> <ufrn:format valor="${(linha.total_matriculados / linha.total_alunos) * 100}" type="valorint"/>%</td>
		</tr>
		
		
		<c:set var="_totalMatriculadosUnidade" value="${_totalMatriculadosUnidade + linha.total_matriculados}"/>
		<c:set var ="_totalMatriculados" value="${_totalMatriculados + linha.total_matriculados}"/>
		<c:set var="_totalAtivosUnidade" value="${_totalAtivosUnidade + linha.total_alunos}"/>
		<c:set var ="_totalAtivos" value="${_totalAtivos + linha.total_alunos}"/>
	</c:forEach>
	
	<tr class="total">
		<td colspan="2">TOTAIS</td>
		<td class="valor"> <ufrn:format valor="${ _totalMatriculadosUnidade }" type="valorint"/></td>
		<td class="valor"> <ufrn:format valor="${ _totalAtivosUnidade }" type="valorint"/></td>
		<td class="valor"> <ufrn:format valor="${(_totalMatriculadosUnidade / _totalAtivosUnidade) * 100}" type="valorint"/>% </td>
	</tr>
	<tr> 
		<td colspan="5" style="height: 15px;"> </td>
	</tr>
	
	<tr class="totalGeral">
		<td colspan="2">TOTAL GERAL</td>
		<td class="valor"> <ufrn:format valor="${ _totalMatriculados }" type="valorint"/> </td>
		<td class="valor"> <ufrn:format valor="${ _totalAtivos }" type="valorint"/></td>
		<td class="valor"> <ufrn:format valor="${(_totalMatriculados / _totalAtivos) * 100}" type="valorint"/>% </td>
	</tr>
	
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
