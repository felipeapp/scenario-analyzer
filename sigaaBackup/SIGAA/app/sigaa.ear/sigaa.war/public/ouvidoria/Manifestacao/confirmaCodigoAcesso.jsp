<%@include file="/public/include/cabecalho.jsp"%>

<%-- Tela de confirma��o do c�digo de acesso a ouvidoria  --%>

<f:view>	
	
	<h:form id="formConfirmacaCodigoAcesso">
		
		<h2>Tela de Confirma��o do C�digo de Acesso de Ouvidoria</h2>
	
		<br />
		<div style="margin: 0pt auto; width: 80%; text-align: center;">
			<h:commandLink action="#{esclarecimentoOuvidoria.telaAcessoManifestacoes}" immediate="true"> Ir Tela de Acesso >> </h:commandLink>
		</div>
		<br />
	
	</h:form>



</f:view>

<%@include file="/public/include/rodape.jsp" %>
