<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Relatório Quantitativo de Alunos por Ano Ingresso</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td>Unidade:</td>
				<td>${usuario.vinculoAtivo.unidade.nome}</td>
			</tr>
		</table>
	</div>

	<br />
	
	<table class="tabelaRelatorioBorda"; width="50%" align="center">
	<thead>
		<tr class="header">
			<td style="text-align: center;">Ano de Ingresso</td>
			<td style="text-align: right;">Quantidade de Alunos</td>
		</tr>
	</thead>
	<c:set var="total_geral" value="0"/>
	
	<c:forEach items="${relatoriosTecnico.lista}" var="linha" varStatus="row">
		<tr class="componentes">
			<td style="text-align: center;">${linha.ano_ingresso}</td>
			<td align="right">${linha.total}</td>
		</tr>		
		<c:set var="total_geral" value="${total_geral + linha.total}"/>
		
	</c:forEach>
   		<tr>
   			<td colspan="2" align="center"><b>Total de Alunos: ${total_geral}</b></td>
   		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
