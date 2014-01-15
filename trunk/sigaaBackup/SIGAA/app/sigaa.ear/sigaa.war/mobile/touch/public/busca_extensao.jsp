<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaExtensaoTouch" />

<f:view>
	<div data-role="page" id="page-public-buscar-extensao" data-theme="b">
		<h:form id="form-busca-extensao" onkeypress="return tratamentoOnKey(event);">
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
				<p align="center"><strong>Buscar Projetos de Extensão</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				Título da Ação: 
				<h:inputText value="#{buscaExtensaoTouch.tituloBusca }" />
				Tipo de Atividade:
				<h:selectOneMenu id="tipoAtividade" value="#{buscaExtensaoTouch.tipoAtividadeBusca.id }" style="width:95%">
					<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0"/>
					<f:selectItems value="#{tipoAtividadeExtensao.allCombo }"/>
				</h:selectOneMenu>
				Departamento:
    			<h:selectOneMenu id="departamento" value="#{buscaExtensaoTouch.unidadeBusca.id }">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allDeptosEscolasCombo}" />
				</h:selectOneMenu>
				Ano:
	    		<h:inputText id="buscaAno" value="#{buscaExtensaoTouch.obj.ano}" maxlength="4" />
	    		
				<h:commandButton id="btnBuscarExtensao" value="Buscar" action="#{buscaExtensaoTouch.buscarAtividades }" />
			</div>
			
			<br/>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			<script>
				$("#form-busca-extensao").attr('data-ajax','false');
				
				$("#form-busca-extensao\\:lnkInicio").attr("data-icon", "home");
				$("#form-busca-extensao\\:acessar").attr("data-icon", "forward");
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
		$("#form-busca-extensao\\:btnBuscarExtensao").click();
	}
	
	return !enter;
}
</script>