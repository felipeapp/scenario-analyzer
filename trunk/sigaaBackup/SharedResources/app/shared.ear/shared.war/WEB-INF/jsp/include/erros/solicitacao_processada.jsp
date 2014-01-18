<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="hideSubsistema" value="true"/>

<style type="text/css">
#solicitacao { margin: 0 auto 1em; width: 85%; padding: 60px 2em; border: 1px solid #EFF3FA; background: #F7F9F8 url(/shared/img/background_erros.jpg) no-repeat; border-top: 0; }
#solicitacao h1 { text-align: center; color: #404E82; }
#solicitacao p { margin: 10px 0; text-indent: 20px; line-height: 1.5em; font-size: 1.1em; }
</style>

<div id="solicitacao">
	<h1>Solicitação já processada</h1>

	<p>A operação que você tentou realizar já foi completada anteriormente. Esta 
	mensagem provavelmente está aparecendo porque você utilizou o botão "Voltar" 
	do navegador para acessar a operação, o que não é permitido por motivos
	de segurança.</p> 

	<p>Para realizar novamente a operação, por favor, retorne a(o) <ufrn:subSistema/> 
	clicando <a href="${ sessionScope.subsistema.link }">aqui</a></p> 
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>