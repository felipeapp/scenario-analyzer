<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaComponenteCurricularTouch" />

<f:view>
	<div data-role="page" id="page-public-listar-componentes-curriculares" data-theme="b">
		<h:form id="form-lista-componente-curricular-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaComponenteCurricularTouch.forwardBuscaComponentes }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Buscar Componentes Curriculares <br/><br/>${buscaComponenteCurricularTouch.descricaoNivel }</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<c:if test="${ not empty buscaComponenteCurricularTouch.componentes }">				
					    <ul data-role="listview" data-inset="true" data-theme="b">
					    	<c:forEach var="c" items="#{buscaComponenteCurricularTouch.componentes }">
				           		<li>
				           			<h:commandLink action="#{buscaComponenteCurricularTouch.view }">
				           				<f:param name="id" value="#{c.id}"/>
				           				
										<label style="white-space: normal;">${c.codigo} - ${c.nome}</label> 
										<br />
										<c:if test="${c.detalhes.crTotal != 0}">
											<label style="white-space: normal;">${c.detalhes.crTotal} crédito(s)</label>
										</c:if>
										<label style="white-space: normal;">(${c.detalhes.chTotal}h)</label>
				           			</h:commandLink>
								</li>
							</c:forEach>
					    </ul>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-componente-curricular-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-componente-curricular-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-componente-curricular-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>