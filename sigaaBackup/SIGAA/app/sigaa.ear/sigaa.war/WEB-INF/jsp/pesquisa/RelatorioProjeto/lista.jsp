<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:import url="${menuDocenteExterno}"/>

<h2> Relatórios de Projetos </h2>

<table class="listagem">
	<caption>Relatórios Finais de Projetos</caption>
	<thead>
		<tr>
			<th> Código </th>
			<th> Título do Projeto </th>
			<th nowrap="nowrap"> Data de Envio </th>
			<th> </th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="relatorio" items="${ lista }" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${ relatorio.projetoPesquisa.codigo } </td>
			<td> ${ relatorio.projetoPesquisa.titulo } </td>
			<td> ${ relatorio.dataEnvio } </td>
			<td>
				<ufrn:link action="/pesquisa/cadastroRelatorioProjeto" param="id=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Visualizar Relatório"
						title="Visualizar Relatório" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>