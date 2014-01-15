<%@include file="/mobile/touch/include/cabecalho.jsp"%>

 <f:view>
	<div data-role="page" id="page-public-index" data-theme="b">
		<h:form id="form-lista-public-index">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
				<h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" styleClass="ui-btn-right" value="Entrar" />
			</div>
			
			<div>
				<h2 align="center">Últimas Notícias</h2>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<c:if test="${empty portalPublicoTouch.ultimasNoticiasPortalPublico }">
					<p align="center"><b>Não há notícias cadastradas.</b></p>
				</c:if>
				
				<div data-role="collapsible-set" data-content-theme="b">
				    <a4j:repeat var="noticia" value="#{ portalPublicoTouch.ultimasNoticiasPortalPublico }" rowKeyVar="n">
						<div data-role="collapsible" data-theme="b" data-collapsed="<h:outputText value="false" rendered="#{ n == 0 }"/><h:outputText value="true" rendered="#{ n > 0 }"/>">
				    		<h3><h:outputText value="#{ noticia.titulo }" escape="false" style="white-space: normal;"/></h3>					    		
				    		<p>
								<h:outputText value="<p>#{ noticia.descricao }</p>"  escape="false" />
							</p>
				    	</div>	
			    	</a4j:repeat>
			    </div>				    	
			</div>
			
			<fieldset class="ui-grid-b" width="100%">
				<div class="ui-block-a" align="center" style="margin-top: 2%; margin-bottom: 2%; width: 100%;">
				 	<a data-role="button" data-theme="b" href="http://www.sistemas.ufrn.br/download/sigaa/public/calendario_universitario.pdf" target="_blank">Calendário Acadêmico</a>
					<h:commandLink action="#{ buscaCursoTouch.iniciarBusca }" id="btnCursos">Cursos</h:commandLink>
					<h:commandLink action="#{ buscaComponenteCurricularTouch.iniciarBusca }" id="btnDisciplinas">Componentes Curriculares</h:commandLink>
					<h:commandLink action="#{ buscaDocenteTouch.iniciarBusca }" id="btnDocentes">Docentes</h:commandLink>
					<h:commandLink action="#{ buscaPesquisaTouch.iniciarBusca }" id="btnProjPesquisa">Projetos de Pesquisa</h:commandLink>
					<h:commandLink action="#{ buscaExtensaoTouch.iniciarBusca }" id="btnProjExtensao">Projetos de Extensão</h:commandLink>
					<h:commandLink action="#{ buscaProcessoSeletivoTouch.iniciarBusca }" id="btnProcSeletivo">Processos Seletivos</h:commandLink>
					<h:commandLink action="#{ buscaAcervoTouch.iniciarBusca }" id="btnAcervo">Consultar Acervo</h:commandLink>
					<h:commandLink action="#{ autenticacaoDocumentoTouch.iniciarAutenticacao }" id="btnAutenticacao">Autenticação de Documentos</h:commandLink>
				</div>
			</fieldset>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>	
			
			<script>
				$("#form-lista-public-index\\:ul-calendario").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnCursos").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnCursos").attr("data-role", "button");
				$("#form-lista-public-index\\:btnDisciplinas").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnDisciplinas").attr("data-role", "button");
				$("#form-lista-public-index\\:btnDocentes").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnDocentes").attr("data-role", "button");
				$("#form-lista-public-index\\:btnProjPesquisa").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnProjPesquisa").attr("data-role", "button");
				$("#form-lista-public-index\\:btnProjExtensao").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnProjExtensao").attr("data-role", "button");
				$("#form-lista-public-index\\:btnProcSeletivo").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnProcSeletivo").attr("data-role", "button");
				$("#form-lista-public-index\\:btnAcervo").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnAcervo").attr("data-role", "button");
				$("#form-lista-public-index\\:btnAutenticacao").attr("data-theme", "b");
				$("#form-lista-public-index\\:btnAutenticacao").attr("data-role", "button");
				$("#form-lista-public-index\\:acessar").attr("data-icon", "forward");
			</script>
	  	</h:form>
		
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
</f:view>

<%@include file="/mobile/touch/include/rodape.jsp"%>
