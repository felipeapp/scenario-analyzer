<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaProcessoSeletivoTouch" />

<f:view>
	<div data-role="page" id="page-public-view-processo" data-theme="b">
		<h:form id="form-view-processo-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaProcessoSeletivoTouch.forwardListaProcessos }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Visualizar Processo Seletivo<br/><br/>${buscaProcessoSeletivoTouch.obj.nome }</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<c:choose>
					<c:when test="${not empty processoSeletivo.obj.curso}">
						<strong>Curso:</strong>
						<a href="${ctx}/sigaa/public/programa/portal.jsf?id=${buscaProcessoSeletivoTouch.obj.curso.unidade.id}" target="_blank">${buscaProcessoSeletivoTouch.obj.curso.descricao }</a><br />
						<i>${buscaProcessoSeletivoTouch.obj.curso.unidade.nome }</i><br />
						<strong>Nível:</strong>
						${buscaProcessoSeletivoTouch.obj.curso.nivelDescricao }<br />
					</c:when>
					<c:otherwise>
						<strong>Curso:</strong>
						${buscaProcessoSeletivoTouch.obj.matrizCurricular.curso.descricao }<br />
						<i>${buscaProcessoSeletivoTouch.obj.matrizCurricular.curso.unidade.nome }</i><br />
						<strong>Nível:</strong>
						${buscaProcessoSeletivoTouch.obj.matrizCurricular.curso.nivelDescricao }<br />
					</c:otherwise>
				</c:choose>
				<strong>Período de Inscrição:</strong>
				${buscaProcessoSeletivoTouch.obj.editalProcessoSeletivo.descricaoInicioInscricoes } - 
				${buscaProcessoSeletivoTouch.obj.editalProcessoSeletivo.descricaoFimInscricoes }<br />
				<c:if test="${not empty buscaProcessoSeletivoTouch.obj.vaga && buscaProcessoSeletivoTouch.obj.vaga > 0 }">
					<strong>Número de Vagas:</strong>
					${buscaProcessoSeletivoTouch.obj.vaga}<br />
					
					<c:if test="${buscaProcessoSeletivoTouch.obj.editalProcessoSeletivo.verificaExisteVaga}">
					<strong>Número de Vagas Restantes:</strong>
						${buscaProcessoSeletivoTouch.obj.vagaRestante}
					</c:if>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				 $("#form-view-processo-public\\:lnkVoltarBusca").attr("data-icon", "back");
				 $("#form-view-processo-public\\:lnkInicio").attr("data-icon", "home");
				 $("#form-view-processo-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="../include/rodape.jsp"%>