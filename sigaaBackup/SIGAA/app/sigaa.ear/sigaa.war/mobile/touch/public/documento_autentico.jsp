<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="autenticacaoDocumentoTouch" />

<f:view>
	<div data-role="page" id="page-public-documento-autentico" data-theme="b">
		<h:form id="form-documento-autentico-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content" style="text-align: center;">
				<p><strong>Documento Autêntico</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
		
				<h:graphicImage url="/public/images/icones/autenticacao.gif" /><br />
				<i>Documento válido e emitido pelo SIGAA!</i><br /><br />
					
				<h:commandLink value="Baixar documento" action="#{autenticacaoDocumentoTouch.visualizarEmissao }" target="_blank" />
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			<script>
				$("#form-documento-autentico-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-documento-autentico-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="../include/rodape.jsp"%>