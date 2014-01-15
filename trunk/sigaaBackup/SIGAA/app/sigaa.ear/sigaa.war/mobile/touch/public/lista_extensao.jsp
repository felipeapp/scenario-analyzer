<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaExtensaoTouch" />

<f:view>
	<div data-role="page" id="page-public-listar-extensao" data-theme="b">
		<h:form id="form-lista-extensao-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaExtensaoTouch.iniciarBusca }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Buscar Projetos de Extensão</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<c:if test="${ not empty buscaExtensaoTouch.atividades }">				
					    <ul data-role="listview" data-inset="true" data-theme="b">
					    	<c:forEach var="a" items="#{buscaExtensaoTouch.atividades }">
				           		<li>
				           			<h:commandLink action="#{inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento}" target="_blank">
					           			<f:param name="idAtividadeExtensaoSelecionada" value="#{a.id}" />
							    		<f:param name="acao" value="#{consultaPublicaAtividadeExtensao.buscaTipoAtividade}" />
							    		
				           				<label style="white-space: normal;">${a.anoTitulo} (${a.tipoAtividadeExtensao.descricao})</label>
				           				<br />
										<label style="white-space: normal;">${a.unidade.sigla}</label>
				           			</h:commandLink>
								</li>
							</c:forEach>
					    </ul>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-extensao-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-lista-extensao-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-extensao-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>