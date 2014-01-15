<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h2>CONVITE PARA A COMUNIDADE VIRTUAL DO SIGAA</h2>

	<center>
		<div class="descricaoOperacao">
		
			<c:set value="${mensagem}" var="name" scope="request" />
			
		
			<h1><i> ${name} </i></h1>
		</div>
	</center>
</f:view>

<br> <br>
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;"><< voltar ao menu principal</a>
	</div>
<br>
<%@include file="/public/include/rodape.jsp"%>