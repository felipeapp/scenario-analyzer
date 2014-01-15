<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-public-nivel-processo" data-theme="b">
		<h:form id="form-lista-nivel-processo-public">
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
				<p align="center"><strong>Listar Processos Seletivos<br/><br/>Selecione um Nível</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
			    <ul data-role="listview" data-inset="true" data-theme="b">
		           		<li>
	           				<h:commandLink action="#{buscaProcessoSeletivoTouch.selecionaNivelBusca}">
			           			<f:param name="nivelProcesso" value="L" />
					    		
		           				<label style="white-space: normal;">Lato Sensu</label>
		           			</h:commandLink>
						</li>
						<li>
	           				<h:commandLink action="#{buscaProcessoSeletivoTouch.selecionaNivelBusca}">
			           			<f:param name="nivelProcesso" value="S" />
					    		
		           				<label style="white-space: normal;">Stricto Sensu</label>
		           			</h:commandLink>
						</li>
						<li>
	           				<h:commandLink action="#{buscaProcessoSeletivoTouch.selecionaNivelBusca}">
			           			<f:param name="nivelProcesso" value="T" />
					    		
		           				<label style="white-space: normal;">Técnico</label>
		           			</h:commandLink>
						</li>
						<li>
	           				<h:commandLink action="#{buscaProcessoSeletivoTouch.selecionaNivelBusca}">
			           			<f:param name="nivelProcesso" value="F" />
					    		
		           				<label style="white-space: normal;">Formação Complementar</label>
		           			</h:commandLink>
						</li>
				    </ul>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-lista-nivel-processo-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-lista-nivel-processo-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>