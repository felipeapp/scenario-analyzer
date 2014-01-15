<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
<h2> Validação de Documentos </h2>
<center>
<div class="descricaoOperacao">
	<h1>
	<img src="/sigaa/public/images/icones/autenticacao.gif"> <br><i>
	 Documento válido e emitido pelo ${ configSistema['siglaSigaa'] }! </i>
	 </h1>
	<h:form id="form">
		<h1>
			<br /> <br />
			<img src="/sigaa/img/view.gif"/>
			<h:commandLink action="#{autenticidade.visualizarEmissao}" value="Visualizar documento" />
			<br />
		</h1>
	</h:form>
</center>
</f:view>
<br /> <br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;"><< voltar ao menu principal</a>
	</div>
<br>
<%@include file="/public/include/rodape.jsp"%>