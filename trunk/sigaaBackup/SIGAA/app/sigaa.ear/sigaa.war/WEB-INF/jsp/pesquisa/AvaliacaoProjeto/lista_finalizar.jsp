<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.AvaliacaoProjetoForm"%>

<style>
	table.listagem tr.centro td {
		background: #C4D2EB;
		font-weight: bold;
		text-align: center;
		font-size: 1.2em;
	}

	table.listagem tr.projeto td {
		border-bottom: 1px solid #BBB;
		background: #F2F2F2;
		font-style: italic;
	}

	table.listagem tr.consultor td{
		background: #FFF;
	}
</style>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Encerrar Avaliações de Projetos Pendentes
</h2>

<html:form action="/pesquisa/avaliarProjetoPesquisa.do?dispatch=encerrarAvaliacoes" method="post" styleId="form">

<c:if test="${ not empty lista }">

	<table class="listagem">
		<caption> Avaliações de Projetos de Pesquisa (${ fn:length(lista)} encontradas)</caption>

		<c:set var="centro" value="" />
		<c:set var="projeto" value="" />
		<c:forEach var="avaliacao" items="${ lista }" varStatus="loop">

		<%-- Centros --%>
		<c:if test="${ avaliacao.projetoPesquisa.centro.id != centro}">
		<c:set var="centro" value="${ avaliacao.projetoPesquisa.centro.id }" />
		<tr class="centro">
			<td colspan="5"> ${ avaliacao.projetoPesquisa.centro.sigla } </td>
		</tr>
		</c:if>

		<%-- Projetos --%>
		<c:if test="${ avaliacao.projetoPesquisa.id != projeto}">
		<c:set var="projeto" value="${ avaliacao.projetoPesquisa.id }" />
		<tr class="projeto">
			<td valign="top"> ${ avaliacao.projetoPesquisa.codigo } </td>
			<td colspan="4">  ${ avaliacao.projetoPesquisa.titulo } </td>
		</tr>
		</c:if>

		<%-- Consultores --%>
		<tr class="consultor">
			<td> </td>
			<td width="50%"> <strong>c${ avaliacao.consultor.codigo }</strong></td>
			<td>
				${ avaliacao.statusAvaliacao }
			</td>
			<td>
				<span style="${avaliacao.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold;">
					<ufrn:format type="valor1" name="avaliacao" property="media" />
				</span>
			</td>
		</tr>

		</c:forEach>
		<tfoot>
		<tr>
			<td colspan="4" align="center">
			<html:submit value="Encerrar Avaliações" onclick="return confirm('Deseja encerrar todas as avaliações listadas?');" />
			<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>	
			</td>
		</tr>
		</tfoot>
	</table>
</c:if>

</html:form>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>