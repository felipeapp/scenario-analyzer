<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
</style>

<f:view>

	<h2>Lista de Email dos Alunos com Vínculo</h2>

	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Curso:</th>
				<td>${ relatorioDiscente.curso.descricao }</td>
			</tr>			
			<tr>
				<th>Status dos Alunos:</th>
				<td>SOMENTE ALUNOS COM VÍNCULO</td>
			</tr>						
		</table>
	</div>
			
	<br>
	<c:set var="anoAtual" value="0"></c:set>
	<c:set var="PeriodoAtual" value="0"></c:set>
	<table class="tabelaRelatorioBorda" width="100%">	
		<thead>
		<tr>
			<th><center>Matrícula</center></th>
			<th> Discente </th>
			<th> Email </th>
		</tr>
		</thead>
		<c:forEach var="aluno" items="#{relatorioDiscente.discentes}">
			<c:if test="${anoAtual != aluno.anoIngresso || PeriodoAtual != aluno.periodoIngresso}">
				<c:set var="anoAtual" value="${aluno.anoIngresso}"></c:set>
				<c:set var="PeriodoAtual" value="${aluno.periodoIngresso}"></c:set>
				<tr>
					<td colspan="3"><b>Ano-Período de Ingresso: ${ aluno.anoIngresso }.${aluno.periodoIngresso } </b></td>
				</tr>
			</c:if>
		
			<tr>
				<td align="center"> ${ aluno.matricula } </td>
				<td> ${ aluno.pessoa.nome } </td>										
				<td> ${ aluno.pessoa.email } </td>
			</tr>
		</c:forEach>
	</table>
		
	<table align="center">
		<tfoot>
		<tr>
			<td colspan="6" style="text-align: center; font-weight: bold;">
				${fn:length(relatorioDiscente.discentes)} discente(s) encontrado(s)
			</td>
			</tr>
		</tfoot>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>