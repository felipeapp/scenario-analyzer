<%@include file="/public/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>


<%-- Tela de confirmação dos castrastros de extensão, tando no cadastro quando na reenvio de senha  --%>

<f:view>	
	
	<h:form id="formConfirmacaInscricao">
		
		<h2>Cadastro nos Cursos e Eventos de Extensão</h2>
		
		<%-- Exibe a mensagem que vem na sessão do usuário, porque essa tela é para ser chamada usando sempre redirect ai o a4j:keepAlive não funciona --%>
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

	<%-- Após exibir a mensagem, remove da sessão --%>
	<%
		session.removeAttribute("mensagemTelaConfirmacao");
	%>

</f:view>

<%@include file="/public/include/rodape.jsp" %>
