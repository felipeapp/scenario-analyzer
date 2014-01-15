<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaDocenteTouch" />

<f:view>
	<div data-role="page" id="page-public-buscar-docentes" data-theme="b">
		<h:form id="form-busca-docente" onkeypress="return tratamentoOnKey(event);">
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
				<p align="center"><strong>Buscar Docentes</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				Nome: 
				<h:inputText value="#{buscaDocenteTouch.nomeBusca }" />
				Departamento:
				<h:selectOneMenu id="departamento" value="#{buscaDocenteTouch.unidadeBusca.id }" style="width:95%">
					<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0"/>
					<f:selectItems value="#{unidade.allDeptosEscolasCombo }"/>
				</h:selectOneMenu>
				<h:commandButton id="btnBuscarDocente" value="Buscar" action="#{buscaDocenteTouch.buscarDocentes }" />
			</div>
			
			<script>
				$("#form-busca-docente").attr('data-ajax','false');
				
				$("#form-busca-docente\\:lnkInicio").attr("data-icon", "home");
				$("#form-busca-docente\\:acessar").attr("data-icon", "forward");
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
		$("#form-busca-docente\\:btnBuscarDocente").click();
	}
	
	return !enter;
}
</script>