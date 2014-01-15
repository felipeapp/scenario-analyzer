<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:import url="${menuDocenteExterno}"/>

<h2> Relat�rios de Projetos </h2>

<table class="listagem">
	<caption>Relat�rios Finais de Projetos</caption>
	<thead>
		<tr>
			<th> C�digo </th>
			<th> T�tulo do Projeto </th>
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
						alt="Visualizar Relat�rio"
						title="Visualizar Relat�rio" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>