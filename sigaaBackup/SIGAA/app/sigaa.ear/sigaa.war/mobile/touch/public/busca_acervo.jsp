<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-public-buscar-acervo" data-theme="b">
		<h:form id="form-busca-acervo" onkeypress="return tratamentoOnKey(event);">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>				
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Consultar Acervo</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				Título: 
				<h:inputText value="#{buscaAcervoTouch.titulo }" />
				
				Autor:
				<h:inputText value="#{buscaAcervoTouch.autor }" />
				
				Assunto:
				<h:inputText value="#{buscaAcervoTouch.assunto }" />
				
				Editora:
				<h:inputText value="#{buscaAcervoTouch.editora }" />
				
				<h:commandButton onclick="desativaAjax();" id="btnBuscarAcervo" value="Buscar" action="#{buscaAcervoTouch.buscarAcervo }" />
				
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			<script>
				$("#form-busca-acervo\\:lnkInicio").attr("data-icon", "home");
				$("#form-busca-acervo\\:acessar").attr("data-icon", "forward");
				
				function desativaAjax(){
					$("#form-busca-acervo").attr('data-ajax','false');
				}
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/touch/include/rodape.jsp"%>

<script type="text/javascript">
function tratamentoOnKey(evt) {
	var enter = evt.keyCode == 13;
	
	if(enter) {
		$("#form-busca-acervo\\:btnBuscarAcervo").click();
	}
	
	return !enter;
}
</script>