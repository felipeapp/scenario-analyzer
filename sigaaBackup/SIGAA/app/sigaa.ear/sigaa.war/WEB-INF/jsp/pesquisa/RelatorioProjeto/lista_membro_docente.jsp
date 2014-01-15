<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Relatórios de Projetos </h2>

<style>
	table.listagem tr.ano td {
		font-weight: bold;
		background-color: #C4D2EB;
		padding: 3px 15px;
		border-bottom: 1px solid #CCC;
		font-size: 1.1em;
		text-align: center;
	}
</style>

<div class="descricaoOperacao">
	<center>
	<p>
		<strong>Atenção!</strong>
		<br/>Somente os relatórios dos projetos que ainda estiverem com o prazo de submissão aberto podem ser editados. 
		<br/>Esta edição só poderá ser realizada pelos coordenadores.
	</p>
	</center>
</div>

<center>
	<div class="infoAltRem">
		<img src="${ctx}/img/alterar.gif" />: Editar Relatório
		<img src="${ctx}/img/view.gif" />: Visualizar Relatório
	</div>
</center>

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
		<c:set var="ano" value=""/>

		<c:forEach var="relatorio" items="${ lista }" varStatus="loop">

		<c:if test="${relatorio.projetoPesquisa.codigo.ano != ano}">
			<c:set var="ano" value="${relatorio.projetoPesquisa.codigo.ano}" />
			<tr class="ano">
				<td colspan="6"> ${ano} </td>
			</tr>
		</c:if>

		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${ relatorio.projetoPesquisa.codigo } </td>
			<td>
				${ relatorio.projetoPesquisa.titulo } <br />
				<em> Coordenador:  ${ relatorio.projetoPesquisa.coordenador } </em>
			</td>
			<td nowrap="nowrap">
				<c:choose>
				<c:when test="${ not empty relatorio.dataEnvio }">
					<ufrn:format type="datahora" name="relatorio" property="dataEnvio" />
				</c:when>
				<c:otherwise>
					<em> indisponível </em>
				</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:if test="${ relatorio.editavel }">
				<ufrn:link action="/pesquisa/relatorioProjeto" param="id=${relatorio.id}&dispatch=edit">
					<img src="${ctx}/img/alterar.gif"
						alt="Editar Relatório"
						title="Editar Relatório" />
				</ufrn:link>
				</c:if>
				<ufrn:link action="/pesquisa/relatorioProjeto" param="id=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/view.gif"
						alt="Visualizar Relatório"
						title="Visualizar Relatório" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>