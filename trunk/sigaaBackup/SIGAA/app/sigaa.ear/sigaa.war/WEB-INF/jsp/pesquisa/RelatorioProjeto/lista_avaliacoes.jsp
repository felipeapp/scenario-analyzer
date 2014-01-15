<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> >
	Avaliação de Relatórios de Projetos de Pesquisa
</h2>

<ufrn:table collection="${lista}"
	title="Lista de Relatórios para Avaliação"
	properties="projetoPesquisa.codigo, projetoPesquisa.titulo, dataEnvio, statusString"
	headers="Código do Projeto, Nome do Projeto, Data de Envio, Status da Avaliação"
	links="src='${ctx}/img/pesquisa/avaliar.gif',?obj.id={id}&dispatch=edit, Avaliar Relatório" />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>