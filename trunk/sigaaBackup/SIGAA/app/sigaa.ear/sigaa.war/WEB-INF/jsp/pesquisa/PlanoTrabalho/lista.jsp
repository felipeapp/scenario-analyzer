<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Planos de Trabalho
</h2>


	<ufrn:table collection="${lista}"
		title="Planos de Trabalho"
		properties="projetoPesquisa.titulo, orientador.pessoa.nome, projetoPesquisa.dataInicio, projetoPesquisa.dataFim, statusString"
		headers="Projeto de Pesquisa, Orientador, Data de Início, Data de Fim, Status">
	</ufrn:table>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
