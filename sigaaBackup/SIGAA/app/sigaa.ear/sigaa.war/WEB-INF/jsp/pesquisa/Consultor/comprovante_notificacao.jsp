<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Notificação de Consultores
</h2>

<div class="descricaoOperacao">
	<p>
	Todas as notificações foram enviadas com sucesso para os <b>${total}</b> consultores que possuem
	avaliações pendentes.
	</p>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>