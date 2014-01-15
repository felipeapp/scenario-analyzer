<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> >
	Avalia��o de Relat�rios de Projetos de Pesquisa
</h2>

<ufrn:table collection="${lista}"
	title="Lista de Relat�rios para Avalia��o"
	properties="projetoPesquisa.codigo, projetoPesquisa.titulo, dataEnvio, statusString"
	headers="C�digo do Projeto, Nome do Projeto, Data de Envio, Status da Avalia��o"
	links="src='${ctx}/img/pesquisa/avaliar.gif',?obj.id={id}&dispatch=edit, Avaliar Relat�rio" />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>