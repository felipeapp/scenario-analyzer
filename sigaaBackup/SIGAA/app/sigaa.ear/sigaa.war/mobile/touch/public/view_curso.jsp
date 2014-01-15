<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaCursoTouch" />

<f:view>
	<div data-role="page" id="page-public-view-curso" data-theme="b">
		<h:form id="form-view-curso-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaCursoTouch.forwardListaCursos }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Visualizar Curso</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
				
				<strong>Curso:</strong>
				${buscaCursoTouch.obj.descricao}
				<br />
				<strong>Nível:</strong>
				${buscaCursoTouch.obj.nivelDescricao}
				<br />
				<c:if test="${!buscaCursoTouch.obj.lato }">
					<strong>Município:</strong>
					<i>${buscaCursoTouch.obj.municipio.nome}</i>
					<br />
				</c:if>
				<c:if test="${buscaCursoTouch.obj.graduacao }">
					<strong>Modalidade:</strong>
					<i>${buscaCursoTouch.obj.modalidadeEducacao.descricao}</i>
					<br />
				</c:if>
				<br />
				<a href="${ctx}/sigaa/public/curso/portal.jsf?id=${buscaCursoTouch.obj.id}&lc=pt_BR" target="_blank">
					Acessar Página do Curso
				</a>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-view-curso-public\\:lnkVoltarBusca").attr("data-icon", "back");
				//$("#form-view-curso-public\\:btnVoltarBusca").attr("data-role", "button");
				$("#form-view-curso-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-view-curso-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="../include/rodape.jsp"%>