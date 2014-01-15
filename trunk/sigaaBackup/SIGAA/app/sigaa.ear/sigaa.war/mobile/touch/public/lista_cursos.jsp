<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaCursoTouch" />

<f:view>
	<div data-role="page" id="page-public-listar-cursos" data-theme="b">
		<h:form id="form-lista-curso-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaCursoTouch.forwardBuscaCursos }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Buscar Cursos</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<c:if test="${ not empty buscaCursoTouch.cursos }">				
					    <ul data-role="listview" data-inset="true" data-theme="b">
					    	<c:forEach var="c" items="#{buscaCursoTouch.cursos }">
				           		<li>
				           			<h:commandLink action="#{buscaCursoTouch.view }">
				           				<f:param name="id" value="#{c.id}"/>
			           				
										<label style="white-space: normal;">
											${c.nome} 
											<c:if test="${c.stricto }">
												(${c.nivelDescricao})
											</c:if>
										</label>
										<br />
										<c:if test="${!c.lato }">
											<label style="white-space: normal;"><i>${c.municipio.nome}</i></label>
											<br />
										</c:if>
										<c:if test="${c.graduacao }">
											<label style="white-space: normal;"><i>${c.modalidadeEducacao.descricao}</i></label>
										</c:if>
			           				</h:commandLink>
								</li>
							</c:forEach>
					    </ul>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			<script>
				$("#form-lista-curso-public\\:btnVoltarBusca").attr("data-icon", "back");
				//$("#form-lista-curso-public\\:btnVoltarBusca").attr("data-role", "button");
				$("#form-lista-curso-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-curso-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-curso-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>