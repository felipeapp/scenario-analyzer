<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jsp:useBean id="sigaaSubSistemas" class="br.ufrn.arq.seguranca.SigaaSubsistemas" scope="page" />

<c:set var="_portalDocente" value="<%=sigaaSubSistemas.PORTAL_DOCENTE%>" />
<c:set var="_portalLato" value="<%=sigaaSubSistemas.LATO_SENSU%>" />

<f:view>
	<c:if test="${programaComponente.portalDocente}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>

	<c:if test="${programaComponente.portalCoordenadorLato}">
		<%@include file="/lato/menu_coordenador.jsp" %>
	</c:if>

	<c:if test="${programaComponente.portalCoordenadorGraduacao}">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</c:if>


	<h2><ufrn:subSistema /> &gt; Programa de Componentes Curriculares &gt; Histórico de Componentes</h2>
	
	<c:if test="${empty programaComponente.programasEncontrados}">
		<br />
		<div style="font-style: italic; text-align:center">Nenhum registro encontrado com esses critérios de busca.</div>
	</c:if>
	
	<c:if test="${not empty programaComponente.programasEncontrados}">
		<br />
		<div class="infoAltRem">
		    <h:graphicImage url="/img/buscar.gif" style="overflow: visible;"/>: Visualizar Programa Existente
		</div>
		<br />
		<table class="listagem">
			<caption class="listagem">COMPONENTES CURRICULARES ENCONTRADOS</caption>
			<thead>
				<tr>
					<td>Período</td>
					<td style="text-align: right;">Código</td>
					<td>Nome</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{programaComponente.programasEncontrados}" var="programa" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${(programa.ano != null and programa.periodo != null) ? programa.anoPeriodo : '' }</td>
					<td width="20" align="right">${programa.componenteCurricular.codigo}</td>
					<td>${programa.componenteCurricular.detalhes.nome}</td>

					<td width="1%" align="center">
						<h:commandLink styleClass="noborder" title=" Visualizar Programa Existente"
								action="#{programaComponente.gerarRelatorioProgramaHistorico}" id="visualizar">
							<f:param name="idComponente" value="#{programa.componenteCurricular.id}" />
							<f:param name="idPrograma" value="#{programa.id}" />
							<f:param name="visualizar" value="visualizar" />
							<h:graphicImage url="/img/buscar.gif" />
						</h:commandLink>
					</td>

				</tr>
			</c:forEach>
			</h:form>
			<h:form>
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton id="selecionarOutro" value="Voltar" action="#{programaComponente.telaBuscaComponentes}" />
					</td>
				</tr>
			</tfoot>
			</h:form>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
