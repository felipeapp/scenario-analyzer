<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaDocenteTouch" />

<f:view>
	<div data-role="page" id="page-public-listar-docentes" data-theme="b">
		<h:form id="form-lista-docente-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaDocenteTouch.iniciarBusca }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Buscar Docentes</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<c:if test="${ not empty buscaDocenteTouch.docentes }">				
					    <ul data-role="listview" data-inset="true" data-theme="b">
					    	<c:forEach var="d" items="${buscaDocenteTouch.docentes }">
				           		<li>
			           				<a href="${ctx}/sigaa/public/docente/portal.jsf?siape=${d.siape}" title="${d.nome} - ${d.unidade.nome}" target="_blank">
					           			<c:if test="${d.idFoto != null && d.idFoto != 0}">
											<img src="${ctx}/sigaa/verFoto?idFoto=${d.idFoto}&key=${ sf:generateArquivoKey(d.idFoto) }" height="100"/>
										</c:if>
										<c:if test="${d.idFoto == null || d.idFoto == 0}">
											<img src="${ctx}/sigaa/img/no_picture.png" height="100"/>
										</c:if>
										<label style="white-space: normal;">${d.nome} - ${d.unidade.nome}</label>
			           				</a>
								</li>
							</c:forEach>
					    </ul>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			<script>
				$("#form-lista-docente-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-docente-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-docente-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>