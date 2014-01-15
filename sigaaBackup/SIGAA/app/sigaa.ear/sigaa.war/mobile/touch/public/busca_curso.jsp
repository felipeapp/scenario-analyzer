<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaCursoTouch" />

<f:view>
	<div data-role="page" id="page-public-buscar-cursos" data-theme="b">
		<h:form id="form-busca-curso" onkeypress="return tratamentoOnKey(event);">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaCursoTouch.iniciarBusca }" id="lnkVoltarNivel">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
			
			<div data-role="content">
				<p align="center"><strong>Buscar Cursos<br/><br/>${buscaCursoTouch.obj.nivelDescricao }</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				Nome:<br/>
				<h:inputText value="#{buscaCursoTouch.nomeBusca }" />
				<c:if test="${buscaCursoTouch.obj.graduacao || buscaCursoTouch.obj.lato }">
					Modalidade de Ensino:<br/>
					<h:selectOneMenu id="modalidade" value="#{buscaCursoTouch.modalidadeEducacaoBusca.id }">
						<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0"/>
						<f:selectItems value="#{modalidadeEducacao.allCombo }"/>
					</h:selectOneMenu>
				</c:if>
				<c:if test="${buscaCursoTouch.obj.stricto }">
					Nível:<br/>
					<h:selectOneMenu id="nivel" value="#{buscaCursoTouch.nivelBusca }">
						<f:selectItem itemValue="S" itemLabel="MESTRADO E DOUTORADO" />
						<f:selectItem itemValue="E" itemLabel="MESTRADO" />
						<f:selectItem itemValue="D" itemLabel="DOUTORADO" />
					</h:selectOneMenu>
				</c:if>
				<h:commandButton id="btnBuscarCurso" value="Buscar" action="#{buscaCursoTouch.buscarCursos }" />
			</div>
			
			<script>
				$("#form-busca-curso").attr('data-ajax','false');
				
				$("#form-busca-curso\\:lnkVoltarNivel").attr("data-icon", "back");
				$("#form-busca-curso\\:lnkInicio").attr("data-icon", "home");
				$("#form-busca-curso\\:acessar").attr("data-icon", "forward");
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
		$("#form-busca-curso\\:btnBuscarCurso").click();
	}
	
	return !enter;
}
</script>