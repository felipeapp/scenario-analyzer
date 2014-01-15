<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Relat�rios de Projetos </h2>

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
		<strong>Aten��o!</strong>
		<br/>Somente os relat�rios dos projetos que ainda estiverem com o prazo de submiss�o aberto podem ser editados. 
		<br/>Esta edi��o s� poder� ser realizada pelos coordenadores.
	</p>
	</center>
</div>

<center>
	<div class="infoAltRem">
		<img src="${ctx}/img/alterar.gif" />: Editar Relat�rio
		<img src="${ctx}/img/view.gif" />: Visualizar Relat�rio
	</div>
</center>

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
					<em> indispon�vel </em>
				</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:if test="${ relatorio.editavel }">
				<ufrn:link action="/pesquisa/relatorioProjeto" param="id=${relatorio.id}&dispatch=edit">
					<img src="${ctx}/img/alterar.gif"
						alt="Editar Relat�rio"
						title="Editar Relat�rio" />
				</ufrn:link>
				</c:if>
				<ufrn:link action="/pesquisa/relatorioProjeto" param="id=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/view.gif"
						alt="Visualizar Relat�rio"
						title="Visualizar Relat�rio" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>