<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-public-nivel-curso" data-theme="b">
		<h:form id="form-lista-nivel-curso-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkPortalPublico">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center">
					<strong>
						Buscar Cursos <br/>
					</strong>
				</p>
				
				<div style="text-align: center;">
					<b>Selecione um Nível</b>
				</div>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<ul data-role="listview" data-inset="true" data-theme="b">
					<li>
	           			<h:commandLink action="#{buscaCursoTouch.selecionaNivelBusca}">
			       			<f:param name="nivelCurso" value="G" />
				    		
		        			Graduação
		        		</h:commandLink>
					</li>
					<li>
	           			<h:commandLink action="#{buscaCursoTouch.selecionaNivelBusca}">
			        		<f:param name="nivelCurso" value="S" />
					   		
		           			Stricto Sensu
		           		</h:commandLink>
					</li>
		        	<li>
	           			<h:commandLink action="#{buscaCursoTouch.selecionaNivelBusca}">
			        		<f:param name="nivelCurso" value="L" />
					    		
		           			Lato Sensu
		           		</h:commandLink>
					</li>
					<li>
	           			<h:commandLink action="#{buscaCursoTouch.selecionaNivelBusca}">
			        		<f:param name="nivelCurso" value="T" />
					   		
		           			Técnico
		           		</h:commandLink>
					</li>
				</ul>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-nivel-curso-public\\:lnkPortalPublico").attr("data-icon", "home");
				$("#form-lista-nivel-curso-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>