<%@include file="../include/cabecalho.jsp"%>

<a4j:keepAlive beanName="buscaComponenteCurricularTouch" />

<f:view>
	<div data-role="page" id="page-public-view-componente-curricular" data-theme="b">
		<h:form id="form-view-componente-curricular-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaComponenteCurricularTouch.forwardListaComponentes }" styleClass="ui-btn-left" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Visualizar Componente Curricular<br/><br/>${buscaComponenteCurricularTouch.obj.codigo }</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<strong>Tipo do Componente:</strong>
				${buscaComponenteCurricularTouch.obj.tipoComponente.descricao }<br />
				<c:if test="${ buscaComponenteCurricularTouch.obj.passivelTipoAtividade }">
					<strong>Tipo de ${buscaComponenteCurricularTouch.obj.atividade ? 'Atividade' : 'Disciplina'}:</strong>
					${buscaComponenteCurricularTouch.obj.tipoAtividade.descricao}<br />
					<strong>Forma de Participação:</strong>
					${buscaComponenteCurricularTouch.obj.formaParticipacao.descricao}<br />
				</c:if>
				<strong>Unidade Responsável:</strong>
				${buscaComponenteCurricularTouch.obj.unidade.nome }<br />
				<strong>Código:</strong>
				${buscaComponenteCurricularTouch.obj.codigo }<br />
				<strong>Nome:</strong>
				${buscaComponenteCurricularTouch.obj.nome }<br />
				<c:if test="${buscaComponenteCurricularTouch.exibeCargaHorariaTotal}">
					<c:if test="${buscaComponenteCurricularTouch.exibeCrTeorico}">
						<strong>Créditos Teóricos:</strong>
						${buscaComponenteCurricularTouch.obj.detalhes.crAula}<br />
					</c:if>
					<c:if test="${buscaComponenteCurricularTouch.exibeCrPratico}">
						<strong>Créditos Práticos:</strong>
						${buscaComponenteCurricularTouch.obj.detalhes.crLaboratorio}<br />
					</c:if>
					<c:if test="${buscaComponenteCurricularTouch.exibeCrEad}">
						<strong>Créditos Ead:</strong>
						${buscaComponenteCurricularTouch.obj.detalhes.crEad}<br />
					</c:if>
					<c:if test="${buscaComponenteCurricularTouch.exibeChTeorico}">
						<strong>Carga Horária Teórica:</strong>
						${buscaComponenteCurricularTouch.obj.detalhes.chAula}<br />
					</c:if>
					<c:if test="${buscaComponenteCurricularTouch.exibeChPratico}">
						<strong>Carga Horária Prática:</strong>
						${buscaComponenteCurricularTouch.obj.detalhes.chLaboratorio}<br />
					</c:if>
					<c:if test="${buscaComponenteCurricularTouch.exibeChEad}">
						<strong>Carga Horária de Ead:</strong>
						${buscaComponenteCurricularTouch.obj.detalhes.chEad}<br />
					</c:if>
					<c:if test="${buscaComponenteCurricularTouch.exibeChDedicadaDocente}">
							<strong>Carga Horária Dedicada do Docente:</strong>
							${buscaComponenteCurricularTouch.obj.detalhes.chDedicadaDocente}<br />
					</c:if>
				</c:if>
				<br />
				<input type="hidden" value="${buscaComponenteCurricularTouch.obj.id}" name="idComponente" id="idComponenteCurricular" />
				<h:commandLink action="#{buscaComponenteCurricularTouch.abrirPrograma}" target="_blank">
					<h:graphicImage url="/img/report.png" />
					Abrir Programa Atual do Componente
				</h:commandLink>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-view-componente-curricular-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-view-componente-curricular-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-view-componente-curricular-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="../include/rodape.jsp"%>