<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
table.listagem thead th {
	text-align: center;
	font-weight: bold;
}

table.listagem td {
	text-align: center;
	border: 1px solid black;
}
</style>
<f:view>
	<h:messages showDetail="true" />
	<h:outputText value="#{prodQuantitativo.create}" />
	
		<h2>Relatório Quantitativo de Produção Acadêmica</h2>
		<table width="100%" class="visualizacao">
			<caption><b>Relatório Quantitativo de Pesquisa</b></caption>
			<tr>
				<th>Unidade:</th>
				<td><b>${prodQuantitativo.unidade.nome}</b></td>
			</tr>
			<tr>
				<th>Período:</th>
				<td><b> ${prodQuantitativo.mesInicial}/${prodQuantitativo.anoInicial} a ${prodQuantitativo.mesFinal}/${prodQuantitativo.anoFinal} </b></td>
			</tr>
		</table>

		<hr>
		
		<c:forEach items="${ prodQuantitativo.relatorioBuilder.relatorio }" var="item">
				${ item }
		</c:forEach>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>