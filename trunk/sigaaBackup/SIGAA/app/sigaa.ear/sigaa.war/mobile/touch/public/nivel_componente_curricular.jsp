<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-public-nivel-componente-curricular" data-theme="b">
		<h:form id="form-lista-nivel-componente-curricular-public">
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
				<p align="center"><strong>Buscar Componentes Curriculares <br/><br/> Selecione um Nível</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<ul data-role="listview" data-inset="true" data-theme="b">
				   	<c:forEach items="#{nivelEnsino.allCombo }" var="n">
			       		<li>
		        			<h:commandLink action="#{buscaComponenteCurricularTouch.selecionaNivelBusca}">
				       			<f:param name="nivelComponente" value="#{n.value }" />
					    		
			       				<label style="white-space: normal;">${n.label }</label>
			       			</h:commandLink>
						</li>
				    </c:forEach>
				</ul>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-nivel-componente-curricular-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-nivel-componente-curricular-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>