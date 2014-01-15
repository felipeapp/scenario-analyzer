<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaProcessoSeletivoTouch" />

<f:view>
	<div data-role="page" id="page-public-listar-processos" data-theme="b">
		<h:form id="form-lista-processos-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaProcessoSeletivoTouch.iniciarBusca }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Listar Processos Seletivos</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<c:if test="${ not empty buscaProcessoSeletivoTouch.processos }">				
					    <ul data-role="listview" data-inset="true" data-theme="b">
					    	<c:forEach var="p" items="#{buscaProcessoSeletivoTouch.processos }">
				           		<li>
				           			<h:commandLink action="#{buscaProcessoSeletivoTouch.view}">	
										<f:param name="id" value="#{p.id}"/>
										
										<label style="white-space: normal;">${p.editalProcessoSeletivo.nome}</label>
										<br/>
										<c:choose>
											<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
											<c:when test="${not empty p.curso}">
												<label style="white-space: normal;">${p.curso.descricao} (${p.curso.nivelDescricao})</label>
											</c:when>
											<%-- SE PROCESSO SELETIVO CURSO GRADUACÃO --%>
											<c:otherwise>
												<label style="white-space: normal;">${p.matrizCurricular.descricao} (${p.matrizCurricular.curso.nivelDescricao})</label>
											</c:otherwise>
										</c:choose>
									</h:commandLink>
								</li>
							</c:forEach>
					    </ul>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-processos-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-processos-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-processos-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>