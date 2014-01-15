<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
.destacado{
	color: red;
}
</style>

<f:view>

	<h2>Relatório da Quantidade de Alunos no Cadastro Único por Curso/Centro</h2>

	<div id="parametrosRelatorio">
		<table >
			<tr>
				<c:if test="${relatoriosSaeMBean.unidade != null && relatoriosSaeMBean.curso != null}">
					<th>${ not empty relatoriosSaeMBean.unidade.nome ? 'Centro:' : 'Curso:' }</th>
					<td>${ not empty relatoriosSaeMBean.unidade.nome  ? relatoriosSaeMBean.unidade.nome : relatoriosSaeMBean.curso.descricao }</td>
				</c:if>
			</tr>
			<tr>
				<th>Ano-Período:</th>
				<td>${ relatoriosSaeMBean.ano }.${ relatoriosSaeMBean.periodo }</td>
			</tr>			
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">	
		<thead>
		<tr>
			<th class="alinharCentro"> Matrícula </th>
			<th> Discente </th>
			<th> Status </th>
		</tr>
		</thead>
		<c:forEach var="entry" items="#{relatoriosSaeMBean.discentesCadastroUnico}">
			<tr>
				<td colspan="3"><strong>${entry.key }</strong></td>
				<c:set var="totalPrioritarios" value="0" />
			</tr>
			<c:forEach var="item" items="#{entry.value}">	 
				<tr>
					<td class="alinharCentro"> ${ item.discente.matricula} </td>										
					<td> ${ item.discente.nome} </td>
					<td> ${ item.prioritario ? 'Aluno Prioritário¹' : '' } </td>
					<c:set var="totalGeral" value="${totalGeral + 1}" />
					<c:if test="${ item.prioritario }">
						<c:set var="totalPrioritarios" value="${totalPrioritarios + 1}" />
						<c:set var="totalPrioritariosGeral" value="${totalPrioritariosGeral + 1}" />
					</c:if>
				</tr>
			</c:forEach>
			<tr class="foot">
				<td colspan="2" align="right">Total Prioritários:</td>
				<td align="right"> ${ totalPrioritarios } </td>
			</tr>			
			<tr class="foot">
				<td colspan="2" align="right">Total:</td>
				<td  align="right"> ${ fn:length(entry.value) }</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp</td>
			</tr>
		</c:forEach>	
		<tr class="foot">
			<td colspan="2" align="right">Total Geral de Alunos Prioritários:</td> 
			<td align="right">${ totalPrioritariosGeral }</td>
		</tr>
		<tr class="foot">
			<td colspan="2" align="right">Total Geral de Alunos:</td>
			<td align="right"> ${ totalGeral }</td>
		</tr>
	</table>
	<br/>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td>¹ Resolução Nº 169/2008-CONSEPE</td>
			</tr>
		</table>
	</div>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>