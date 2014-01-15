<%@include file="/public/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>


<%-- Tela de confirma��o dos castrastros de extens�o, tando no cadastro quando na reenvio de senha  --%>

<f:view>	
	
	<h:form id="formConfirmacaInscricao">
		
		<h2>Cadastro nos Cursos e Eventos de Extens�o</h2>
		
		<%-- Exibe a mensagem que vem na sess�o do usu�rio, porque essa tela � para ser chamada usando sempre redirect ai o a4j:keepAlive n�o funciona --%>
		<div class="descricaoOperacao">
			<p> ${sessionScope.mensagemTelaConfirmacao} </p>
			<br/>
		</div>
		
		<br />
		<div style="margin: 0pt auto; width: 80%; text-align: center;">
			<h:commandLink action="#{logonCursosEventosExtensaoMBean.telaLoginCursosEventosExtensao}" immediate="true"> Ir Tela de Login >> </h:commandLink>
		</div>
		<br />
		
	</h:form>

	<%-- Ap�s exibir a mensagem, remove da sess�o --%>
	<%
		session.removeAttribute("mensagemTelaConfirmacao");
	%>

</f:view>

<%@include file="/public/include/rodape.jsp" %>
