<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:outputText value="#{ relatoriosCenso.create }" />
	<c:set var="relatorio_" value="${ relatoriosCenso.relatorio }"/>

	<h2>Relatório Analítico de Quantidade de Projetos por Docente agrupados por Área de Conhecimento - Ano: ${relatoriosCenso.ano}</h2>
	<table class="listagem">
		<thead>
		<tr>
			<th>Siape</th>
			<th>CPF</th>
			<th>Nome</th>
			<th>Lotação</th>
			<th>Área de Conhecimento</th>
			<th style="text-align: center;">Num. Projetos</th>
		</tr>	
		</thead>
		
		<c:set var="total" value="0"/>
		
		<c:forEach var="item" items="${ relatorio_ }">
			<c:forEach var="linha" items="${ item.value.linhas }" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">		
					<td> ${ linha.value.siape } </td>
					<td> <ufrn:format type="cpf_cnpj" valor="${ linha.value.cpf }" /> </td>
					<td> ${ linha.value.nome } </td>
					<td> ${ linha.value.lotacao } </td>
					<td> ${ linha.value.area } </td>
					<td style="text-align: center;"> ${ linha.value.num_projetos } </td>
					<c:set var="total" value="${ total + linha.value.num_projetos }"/>
				</tr>
			</c:forEach>
		</c:forEach>
		<tr>
			<td colspan="5">Total</td>
			<td style="text-align: center;">${ total }</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
