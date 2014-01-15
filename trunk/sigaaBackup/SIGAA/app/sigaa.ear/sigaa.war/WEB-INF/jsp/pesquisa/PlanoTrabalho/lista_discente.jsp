<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema></ufrn:subSistema> >
	Meus Planos de Trabalho
</h2>

<style>
	table.listagem tr.orientador td {
		background-color: #C4D2EB;
		padding: 8px 10px 2px;
		border-bottom: 1px solid #BBB;
		font-variant: small-caps;

		font-style: italic;
	}
</style>

	<div class="infoAltRem">
		<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
		 : Visualizar Plano de Trabalho
	</div>

<table class="listagem">
	<caption>Planos de Trabalho</caption>
	<thead>
		<tr>
			<th> Projeto de Pesquisa </th>
			<th> Discente Ativo</th>
			<th> Modalidade da Bolsa </th>
			<th> Status </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:set var="orientador"/>

		<c:forEach var="plano" items="${ planos }" varStatus="loop">

		<c:if test="${plano.orientador.nome != orientador}">
			<c:set var="orientador" value="${ plano.orientador.nome }" />
			<tr class="orientador">
				<td colspan="5">
					<b>Orientador:</b> ${orientador}
				</td>
			</tr>
		</c:if>

		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"  }">
			<td>
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${plano.projetoPesquisa.id}">
				${ plano.projetoPesquisa.codigo }
				</html:link>
			</td>
			<td>
				<c:choose>
					<c:when test="${ not empty plano.membroProjetoDiscente }">
						${ plano.membroProjetoDiscente.discente.pessoa.nome }
					</c:when>
					<c:otherwise>
						<em> não definido </em>
					</c:otherwise>
				</c:choose>
			</td>
			<td> ${ plano.tipoBolsaString } </td>
			<td> ${ plano.statusString } </td>
			<td nowrap="nowrap">
				<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=view&obj.id=${plano.id}">
					<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho"/>
				</html:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5" style="text-align: center; font-weight: bold;	">
				${ fn:length(planos) } planos de trabalho encontrados
			</td>
		</tr>
	</tfoot>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>