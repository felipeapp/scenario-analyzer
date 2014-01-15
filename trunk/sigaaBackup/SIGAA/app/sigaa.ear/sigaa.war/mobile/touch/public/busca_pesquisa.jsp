<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaPesquisaTouch" />

<f:view>
	<div data-role="page" id="page-public-buscar-pesquisa" data-theme="b">
		<h:form id="form-busca-pesquisa" onkeypress="return tratamentoOnKey(event);">
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
				<p align="center"><strong>Buscar Projetos de Pesquisa</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				Título:
				<h:inputText value="#{buscaPesquisaTouch.tituloBusca }" />
				Centro:
				<h:selectOneMenu id="centro" value="#{buscaPesquisaTouch.centroBusca.id }">
					<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0"/>
					<f:selectItems value="#{unidade.allCentroCombo }"/>
				</h:selectOneMenu>
				Departamento:
    			<h:selectOneMenu id="buscaUnidade" value="#{buscaPesquisaTouch.unidadeBusca.id }">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu>
				Ano:
	    		<h:inputText id="buscaAno" value="#{buscaPesquisaTouch.anoBusca}" maxlength="4" />
	    		
				<h:commandButton id="btnBuscarPesquisa" value="Buscar" action="#{buscaPesquisaTouch.buscarProjetos }" />
			</div>
			
			<script>
				$("#form-busca-pesquisa").attr('data-ajax','false');
				
				$("#form-busca-pesquisa\\:lnkInicio").attr("data-icon", "home");
				$("#form-busca-pesquisa\\:acessar").attr("data-icon", "forward");
			</script>
			
			<br/>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/touch/include/rodape.jsp"%>

<script type="text/javascript">
function tratamentoOnKey(evt) {
	var enter = evt.keyCode == 13;
	
	if(enter) {
		$("#form-busca-pesquisa\\:btnBuscarPesquisa").click();
	}
	
	return !enter;
}
</script>