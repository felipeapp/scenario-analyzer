<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Avalia��o de Projetos
</h2>

<c:if test="${not empty lista}">
	<ufrn:table
		collection="${lista}"
		title="Avalia��es de Projetos"
		properties="projetoPesquisa.titulo, projetoPesquisa.dataInicio, projetoPesquisa.dataFim, statusAvaliacao"
		headers="Projeto de Pesquisa, Data de In�cio, Data de Fim, Situa��o"
		links="src='${ctx}/img/pesquisa/avaliar.gif',?obj.id={id}&dispatch=popular, Avaliar Projeto">
	</ufrn:table>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>