<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="buscarTitulos" data-theme="b">
		<h:form id="formBuscarTitulos">
			<div data-role="header" data-theme="b">				
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<c:if test="${not empty acesso.usuario.servidor }">
						<li><h:commandLink id="lnkVoltarDocente" value="Voltar" action="#{ portalDocenteTouch.forwardBiblioteca}" /></li>
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicioDocente"/></li>
					</c:if>
					<c:if test="${not empty acesso.usuario.discente }">
						<li><h:commandLink id="lnkVoltarDiscente" value="Voltar" action="#{ portalDiscenteTouch.forwardBiblioteca}" /></li>
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicioDiscente"/></li>
					</c:if>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Biblioteca</strong></p>
				<p align="center"><strong>Consultar Acervo</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				Título: 
				<h:inputText value="#{operacoesBibliotecaMobileMBean.titulo}" />
				Autor:
				<h:inputText value="#{operacoesBibliotecaMobileMBean.autor}" />
				Assunto:
				<h:inputText value="#{operacoesBibliotecaMobileMBean.assunto}" />
				Editora:
				<h:inputText value="#{operacoesBibliotecaMobileMBean.editora}" />
				<h:commandButton onclick="desativaAjax();" value="Buscar" action="#{operacoesBibliotecaMobileMBean.consultarTitulos}" />
				
				<c:if test="${not empty acesso.usuario.discente }">
					<script>
						$("#formBuscarTitulos\\:lnkVoltarDiscente").attr("data-icon", "back");
						$("#formBuscarTitulos\\:lnkInicioDiscente").attr("data-icon", "home");
						$("#formBuscarTitulos\\:lnkSair").attr("data-icon", "sair");
					</script>
				</c:if>
			
				<c:if test="${not empty acesso.usuario.servidor }">
					<script>
						$("#formBuscarTitulos\\:lnkVoltarDocente").attr("data-icon", "back");
						$("#formBuscarTitulos\\:lnkInicioDocente").attr("data-icon", "home");
						$("#formBuscarTitulos\\:lnkSair").attr("data-icon", "sair");
					</script>
				</c:if>
				
				<script>
					//É necessário desativar o ajax ao submeter o formulário para que caracteres acentuados sejam
					//considerados corretamente na busca (problema do jQuery Mobile).
					function desativaAjax(){
						$("#formBuscarTitulos").attr("data-ajax", "false");
					}
				</script>
			</div>
			<br/>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
</f:view>

<%@include file="../include/rodape.jsp" %>