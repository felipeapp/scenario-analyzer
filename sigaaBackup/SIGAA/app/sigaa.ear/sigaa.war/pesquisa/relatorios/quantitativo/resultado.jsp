<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
table.listagem thead th {
	text-align: center;
	font-weight: bold;
}

table.listagem td {
	text-align: center;
	border: 1px solid black;
}
table.listagem tr.ultima td {
	font-weight: bold;
}
table.listagem {
	width: 100%;
}
-->
</style>
<f:view>
	<h:messages showDetail="true" />
	<h:outputText value="#{quantPesquisa.create}" />
	
		<hr>
		<table width="100%">
			<caption><b>Relatório Quantitativo de Pesquisa</b></caption>
			<tr>
				<th>Unidade:</th>
				<td><b>${quantPesquisa.unidade.nome}</b></td>
			</tr>
			<tr>
				<th>Período:</th>
				<td><b> ${quantPesquisa.descricaoPeriodo} </b></td>
			</tr>
		</table>
		<hr>
		<table class="listagem">
			<caption>Relatório</caption>
			<thead>
				<h:outputText value="#{quantPesquisa.cabecalho}" escape="false"/>
			</thead>
			<c:forEach items="${quantPesquisa.relatorio}" var="item">
				<tr>
					<td style="text-align: left;">${item.key}</td>
					<c:set value="0" var="total"/>
					<c:forEach items="${item.value}" var="meses">
						<td>${meses.value}</td>
						<c:set value="${total + meses.value}" var="total"/>
					</c:forEach>
					<td>${ total }</td>
				</tr>
			</c:forEach>
		</table>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
