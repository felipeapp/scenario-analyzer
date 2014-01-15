<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-biblioteca" data-theme="b">			
		<h:form id="form-biblioteca">
			<div data-role="header" data-theme="b">
				<%--<a href="#" data-rel="back" data-role="link" styleClass="ui-btn-left" data-icon="back">Voltar</a> --%>
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<c:if test="${not empty acesso.usuario.servidor }">
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicioDocente"/></li>
					</c:if>
					<c:if test="${not empty acesso.usuario.discente }">
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicioDiscente"/></li>
					</c:if>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
			
			<div data-role="content" data-theme="b">
				<p align="center"><strong>Biblioteca</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
								
			    <ul data-role="listview" data-inset="true">
		            <li><h:commandLink value="Visualizar Empréstimos" action="#{operacoesBibliotecaMobileMBean.iniciarVisualizarEmprestimos}"><f:param name="touch" value="true" /></h:commandLink></li>
					<li><h:commandLink value="Renovar Empréstimos" action="#{operacoesBibliotecaMobileMBean.iniciarRenovacao}"><f:param name="touch" value="true" /></h:commandLink></li>
					<li><h:commandLink value="Últimos Empréstimos" action="#{operacoesBibliotecaMobileMBean.consultaUltimosEmprestimosUsuario}"><f:param name="touch" value="true" /></h:commandLink></li>
					<li><h:commandLink value="Consultar Acervo" action="#{operacoesBibliotecaMobileMBean.iniciarConsultaTitulo}"><f:param name="touch" value="true" /></h:commandLink></li>
		        </ul>
			</div>
			
			<c:if test="${not empty acesso.usuario.servidor }">
				<script>
					$("#form-biblioteca\\:lnkInicioDocente").attr("data-icon", "home");
					$("#form-biblioteca\\:lnkSair").attr("data-icon", "sair");
				</script>
			</c:if>
			<c:if test="${not empty acesso.usuario.discente }">
				<script>
					$("#form-biblioteca\\:lnkInicioDiscente").attr("data-icon", "home");
					$("#form-biblioteca\\:lnkSair").attr("data-icon", "sair");
				</script>
			</c:if>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
		</h:form>
		
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
</f:view>

<%@include file="../include/rodape.jsp" %>
