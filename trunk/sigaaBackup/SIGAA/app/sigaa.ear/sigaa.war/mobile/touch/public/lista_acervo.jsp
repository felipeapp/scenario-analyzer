<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-public-listar-acervo" data-theme="b">
		<h:form id="form-lista-acervo-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaAcervoTouch.iniciarBusca }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Consultar Acervo</strong></p>
				<p align="center"><strong>Resultado da Busca</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<c:if test="${ not empty buscaAcervoTouch.resultadosPaginadosEmMemoria }">
					<p align="center">
						<strong><h:outputText value="Títulos Encontrados (#{buscaAcervoTouch.descricaoPaginacao})"/></strong>
					</p>
					<ul data-role="listview" data-inset="true" data-theme="b">
						<c:forEach var="r" varStatus="status" items="#{buscaAcervoTouch.resultadosPaginadosEmMemoria}">
							<li>
			          			<h:commandLink action="#{buscaAcervoTouch.view }">
				          			<f:param name="id" value="#{r.id}"/>
									<label style="white-space: normal;">${r.titulo} ${r.meioPublicacao} ${r.subTitulo}</label>
		           				</h:commandLink>
							</li>
						</c:forEach>
				    </ul>
				    
				    <c:if test="${buscaAcervoTouch.quantidadeTotalResultados > buscaAcervoTouch.quantideResultadosPorPagina}">
						<h:selectOneMenu value="#{buscaAcervoTouch.paginaAtual}" onchange="submeter();">
							<f:selectItems value="#{buscaAcervoTouch.paginas}"/>
						</h:selectOneMenu>
					</c:if>
					<h:commandButton style="display: none;" value="Submeter" id="btnSubmeter" action="#{buscaAcervoTouch.atualizaResultadosPaginacao}"/>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-acervo-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-acervo-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-acervo-public\\:acessar").attr("data-icon", "forward");
				
				$("#form-lista-acervo-public\\:btnSubmeter").attr("data-role", "none");
				
				$("#form-lista-acervo-public").attr("data-ajax", false);
				
				function submeter(){
					$("#form-lista-acervo-public\\:btnSubmeter").click();
				}
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<script type="text/javascript">
	$(document).ready(function(){
		$('html, body').animate({
		    scrollTop: $(".anchorLink:last").position().top
		}, 300);
	});
</script>

<%@include file="/mobile/commons/rodape.jsp" %>