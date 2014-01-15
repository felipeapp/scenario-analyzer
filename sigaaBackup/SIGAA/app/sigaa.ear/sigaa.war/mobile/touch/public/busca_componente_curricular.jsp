<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaComponenteCurricularTouch" />

<f:view>
	<div data-role="page" id="page-public-buscar-docentes" data-theme="b">
		<h:form id="form-busca-docente" onkeypress="return tratamentoOnKey(event);">
			<script type="text/JavaScript">
				function up(lstr){ 
					var str=lstr.value;
					lstr.value=str.toUpperCase();
				}
			</script>
		
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{buscaComponenteCurricularTouch.iniciarBusca }" id="lnkVoltar">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Buscar Componentes Curriculares<br/><br/>${buscaComponenteCurricularTouch.descricaoNivel }</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				Nome: 
				<h:inputText value="#{buscaComponenteCurricularTouch.nomeBusca }" />
				Código: 
				<h:inputText onkeyup="up(this)" value="#{buscaComponenteCurricularTouch.codigoBusca }" />
				Tipo do Componente:
				<h:selectOneMenu id="tipo" value="#{buscaComponenteCurricularTouch.tipoComponenteBusca.id}" >
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{componenteCurricular.allTiposComponentes}" />
				</h:selectOneMenu>
				Departamento:
				<h:selectOneMenu id="departamento" value="#{buscaComponenteCurricularTouch.unidadeBusca.id }" style="width:95%">
					<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0"/>
					<f:selectItems value="#{unidade.allDeptosEscolasCoordCursosCombo }"/>
				</h:selectOneMenu>
				<h:commandButton id="btnBuscarComponente" value="Buscar" action="#{buscaComponenteCurricularTouch.buscarComponentes }" />
			</div>
			
			<script>
				$("#form-busca-docente").attr('data-ajax','false');
				
				$("#form-busca-docente\\:lnkInicio").attr("data-icon", "home");
				$("#form-busca-docente\\:acessar").attr("data-icon", "forward");
				$("#form-busca-docente\\:lnkVoltar").attr("data-icon", "back");
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
		$("#form-busca-docente\\:btnBuscarComponente").click();
	}
	
	return !enter;
}
</script>