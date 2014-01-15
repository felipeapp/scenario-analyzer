<%-- Pagina Principal do sub sistema da biblioteca mobile  --%>

<%@include file="/mobile/commons/cabecalho.jsp" %>

	<f:view>
	
		<center>
	
			<strong> Menu Principal Biblioteca Mobile </strong><br/>
		
			<%-- Informacoes obtidas no login
			<small>
			${sessionScope.infoUsuarioBiblioteca.matricula}   <br/>
			${sessionScope.infoUsuarioBiblioteca.nomeUsuario} <br/>
			${sessionScope.infoUsuarioBiblioteca.tipoUsuario} <br/>
			</small>  --%>

			<br/>
		
			 
			<h:form>
				<h:commandButton value="Visualizar Emprestimos" action="#{operacoesBibliotecaMobileMBean.iniciarVisualizarEmprestimos}" styleClass="botaoMenuMobile"/> <br/>
				<h:commandButton value="Renovar Emprestimos" action="#{operacoesBibliotecaMobileMBean.iniciarRenovacao}" styleClass="botaoMenuMobile"/> <br/>
				<h:commandButton value="Ultimos Emprestimos" action="#{operacoesBibliotecaMobileMBean.iniciarConsultaUltimoEmpretimos}" styleClass="botaoMenuMobile"/> <br/>
				<h:commandButton value="Consultar Acervo" action="#{operacoesBibliotecaMobileMBean.iniciarConsultaTitulo}" styleClass="botaoMenuMobile"/> <br/>
				<h:commandButton value="<< Voltar " action="#{operacoesBibliotecaMobileMBean.voltarMenuPrincipal}" styleClass="botaoMenuMobile"/> <br/>
			</h:form> 




			<%-- Usando From normal do HTML porque com o do JSF nao funcionou a operacao de logoff --%>

			<form method="post">
				<br/><br/>
				<a href="/sigaa/mobile/logonMobile.do?dispatch=logoff">Sair</a>
			</form> 


		</center>	
		
	</f:view>	

<%@include file="/mobile/commons/rodape.jsp" %>
