<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Distribuir Projeto de Pesquisa
</h2>

<div class="descricaoOperacao">
	Distribui��o realizada com sucesso! <br />
	<html:link action="/pesquisa/distribuirProjetoPesquisa?dispatch=resultadoDistribuicao">
		Veja aqui a lista de projetos distribu�dos e que aguardam avalia��o pelos consultores
	</html:link>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>