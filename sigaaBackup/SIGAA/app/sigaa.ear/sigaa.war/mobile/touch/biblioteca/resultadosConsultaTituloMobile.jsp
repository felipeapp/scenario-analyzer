<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="visualizarEmprestimos" data-theme="b">
		<h:form id="formVisualizarEmprestimos">
			<div data-role="header" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink value="Voltar" id="lnkVoltar" action="#{operacoesBibliotecaMobileMBean.iniciarConsultaTitulo}"><f:param name="touch" value="true" /></h:commandLink></li>
					<c:if test="${not empty acesso.usuario.servidor }">
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicioDocente"/></li>
					</c:if>
					<c:if test="${not empty acesso.usuario.discente }">
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicioDiscente"/></li>
					</c:if>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
			
			<div data-role="content">
				<p align="center"><strong>Biblioteca</strong></p>
				<p align="center"><strong>Resultado da Busca</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<a4j:outputPanel rendered="#{fn:length(operacoesBibliotecaMobileMBean.resultadosPaginadosEmMemoria) > 0}">				
					<p align="center">
						<strong><h:outputText value="Títulos Encontrados (#{operacoesBibliotecaMobileMBean.descricaoPaginacao})"/></strong>
					</p>
					
					<div data-role="collapsible-set" data-content-theme="b">
		    
						<a4j:repeat var="titulo" value="#{operacoesBibliotecaMobileMBean.resultadosPaginadosEmMemoria}" rowKeyVar="status">
							<div data-role="collapsible" data-theme="b" data-collapsed="true">
								<h3><h:outputText style="white-space: normal;" value="#{titulo.titulo}" /></h3>
		
								<b>Autor:</b><h:outputText value="#{titulo.autor}" /><br/>
								<b>Quantidade de Materiais Inf.:</b><h:outputText value="#{titulo.quantidadeMateriaisAtivosTitulo}" /><br/>
								<br/>
								
								<h:commandButton styleClass="detalhes" value="Ver Materiais" action="#{operacoesBibliotecaMobileMBean.visualizarMateriais}" rendered="#{titulo.quantidadeMateriaisAtivosTitulo > 0}">
									<f:setPropertyActionListener target="#{operacoesBibliotecaMobileMBean.idTituloSelecionado}" value="#{titulo.idTituloCatalografico}" />
								</h:commandButton>
							</div>
						</a4j:repeat>
					</div>
					<br/>
					
					<c:if test="${operacoesBibliotecaMobileMBean.quantidadeTotalResultados > operacoesBibliotecaMobileMBean.quantideResultadosPorPagina}">
						<h:selectOneMenu value="#{operacoesBibliotecaMobileMBean.paginaAtual}" onchange="submeter();">
							<f:selectItems value="#{operacoesBibliotecaMobileMBean.paginas}"/>
						</h:selectOneMenu>
					</c:if>
				</a4j:outputPanel>
				
				<h:commandButton style="display: none;" value="Submeter" id="btnSubmeter" action="#{operacoesBibliotecaMobileMBean.atualizaResultadosPaginacao}"/>
				
				<c:if test="${not empty acesso.usuario.servidor }">
					<script>
						$("#formVisualizarEmprestimos\\:lnkInicioDocente").attr("data-icon", "home");
					</script>
				</c:if>
				<c:if test="${not empty acesso.usuario.discente }">
					<script>
						$("#formVisualizarEmprestimos\\:lnkInicioDiscente").attr("data-icon", "home");
					</script>
				</c:if>
					
				<script>
					$("#formVisualizarEmprestimos").attr("data-ajax", false);
					
					$("#formVisualizarEmprestimos\\:lnkVoltar").attr("data-icon", "back");
					$("#formVisualizarEmprestimos\\:lnkSair").attr("data-icon", "sair");
					$("#formVisualizarEmprestimos\\:btnSubmeter").attr("data-role","none");
					
					$(".detalhes").attr("data-icon", "search");
					
					function submeter(){
						$("#formVisualizarEmprestimos\\:btnSubmeter").click();
					}
				</script>
			</div>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
		
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>