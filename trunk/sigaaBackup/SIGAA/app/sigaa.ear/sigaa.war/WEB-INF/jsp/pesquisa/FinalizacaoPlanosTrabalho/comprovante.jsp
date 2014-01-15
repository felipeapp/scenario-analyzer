<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Finalização de Planos de Trabalho
</h2>

<style>
	#comprovanteEnvio {
		background: #F5F5F5;
	}

	#comprovanteEnvio p{
		line-height: 3em;
		text-align: center;
		margin: 20px 100px;
		font-weight: bold;
	}
</style>

<div id="comprovanteEnvio">
	<p> Foram finalizados ${total} planos de trabalho da cota ${formPlanoTrabalho.obj.cota.descricao} com sucesso !	</p>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>