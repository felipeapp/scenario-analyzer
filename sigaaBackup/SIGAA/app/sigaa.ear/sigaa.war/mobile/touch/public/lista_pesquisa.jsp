<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaPesquisaTouch" />

<f:view>
	<div data-role="page" id="page-public-listar-pesquisa" data-theme="b">
		<h:form id="form-lista-pesquisa-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaPesquisaTouch.iniciarBusca }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Buscar Projetos de Pesquisa</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<c:if test="${ not empty buscaPesquisaTouch.projetos }">				
					    <ul data-role="listview" data-inset="true" data-theme="b">
					    	<c:forEach var="p" items="#{buscaPesquisaTouch.projetos }">
				           		<li>
				           			<h:commandLink action="#{consultaProjetos.view}" target="_blank">	
										<f:param name="id" value="#{p.id}"/>
										
										<label style="white-space: normal;">${p.codigo} - ${p.titulo}</label>
										<br />
										<label style="white-space: normal;">${p.centro.nome} (${p.situacaoProjeto.descricao})</label>
									</h:commandLink>
								</li>
							</c:forEach>
					    </ul>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-pesquisa-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-pesquisa-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-pesquisa-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>