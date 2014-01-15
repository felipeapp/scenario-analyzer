<%@ include file="./include/cabecalho.jsp" %>
<f:view  locale="#{portalPublicoDepartamento.lc}">
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<c:set var="monitorias" value="#{portalPublicoDepartamento.projetosMonitoria}" />
<div id="colEsq">
	<%@ include file="./include/menu.jsp" %>
</div>

	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
		<div id="projetoMonitoria">
		<div id="titulo">
			<h1>Projetos de Monitoria</h1>
	</div>
	<br/>
		
	<h:form id="formListagemMonitoria">
	<c:if test="${not empty monitorias}">
		<table class="listagem">
			<tfoot >
				<td>
				<h:graphicImage url="/img/view.gif" width="12px" height="12px" />
				<b>: Visualizar Detalhes do Projeto de Monitoria</b>
				</td>
			</tfoot>
		</table>
		<table class="listagem">
				<tbody>
				<c:set var="ano" value=""/>
				<c:set var="coord" value=""/>
				<c:forEach var="monitDpt" items="#{monitorias}" varStatus="loop">
				<c:set var="cont" value="${loop.index}"/>
				<c:if test="${coord != monitDpt.coordenacao.nome}">
					<c:set var="cont" value="2"/>
					<c:set var="coord" value="${monitDpt.coordenacao.nome}"/>
					<c:set var="ano" value=""/>
					<c:if test="${not loop.first}">
					<tr> <td class="spacer" colspan="4"> </td> </tr>
					</c:if>
					<tr><td class="coord" colspan="4"> ${coord}</td></tr>
				</c:if>
				<tr class="${cont % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td  class="ano">${monitDpt.ano}.</td>
					<td >${monitDpt.titulo}	</td>
					<td  class="ver">
					<h:commandLink title="Visualizar Projeto de Monitoria" target="_self" action="#{projetoMonitoria.view}" id="visualizar">	
							<f:param name="id" id="id" value="#{monitDpt.id}"/>
							<h:graphicImage url="/img/view.gif"/><br clear="all"/>
					</h:commandLink>
					</td>
				</tr>
				
				</c:forEach>
			</tbody>
			<tr><td colspan="4">&nbsp;<td></tr>
			<tfoot>
				<td colspan="4"><b>${fn:length(monitorias)} Projeto(s) encontrado(s) </b></td>
			</tfoot>
		</table>
	</c:if>
	<c:if test="${empty monitorias}">
		<p class="vazio">
			Nenhum projeto de monitoria cadastrado
		</p>
	</c:if>
			</h:form>
		</div>
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
</f:view>	
<%@ include file="../include/rodape.jsp" %>