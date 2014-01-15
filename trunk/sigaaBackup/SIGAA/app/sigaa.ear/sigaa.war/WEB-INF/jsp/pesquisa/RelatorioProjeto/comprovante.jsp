<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>

#comprovanteEnvio {
	background: #F5F5F5;
}

#comprovanteEnvio h4{
	text-align: center;
	font-variant: small-caps;
	background: #404E82;
	padding: 2px;
	color: #FEFEFE;
}

#comprovanteEnvio p{
	padding: 5px;
	text-align: center;
	margin: 20px 100px;
}

#comprovanteEnvio div.resumo{
	text-align: left;
	line-height: 1.25em;
	font-style: italic;
	margin: 20px 60px;
}


</style>

<h2> Relatório Anual de Projeto de Pesquisa </h2>

<div id="comprovanteEnvio">
	<h4> Comprovante de Envio de Relatório de Projeto de Pesquisa </h4>
	<p>
		O relatório anual do projeto de pesquisa <b>${ relatorioProjeto.projetoPesquisa.codigoTitulo }</b>, foi enviado
		com sucesso	em <ufrn:format type="datahora" name="relatorioProjeto" property="dataEnvio" />
	 	por ${ sessionScope.usuario.pessoa.nome }
	 	(usuário <i>${ sessionScope.usuario.login }</i>)
	</p>
	<div class="resumo">
		<ufrn:format type="texto" name="relatorioProjeto" property="resumo" />
	</div>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
