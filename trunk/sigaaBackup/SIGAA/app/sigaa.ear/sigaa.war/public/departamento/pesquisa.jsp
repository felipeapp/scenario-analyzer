	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>

	<%@ include file="./include/departamento.jsp" %>
	<div id="colDirCorpo">
	<!--  INÍCIO CONTEÚDO -->
		<div id="projetoPesquisa">
	<div id="titulo">
		<h1>Projetos de Pesquisa</h1>
	</div>
	<br/>
		<c:set var="projetos" value="#{portalPublicoDepartamento.projetosPesquisa}" />
		<c:if test="${not empty projetos}">
		<table class="listagem">
			<tfoot>
				<td colspan="4"  ><h:graphicImage url="/img/view.gif" width="12px" height="12px" />
				<b>: Visualizar Detalhes do Projeto de Pesquisa</b>
				 </td>
			</tfoot>
		</table>
		<h:form id="formListagemPesquisa">
		<table class="listagem">
			<tbody>
				<c:set var="ano" value=""/>
				<c:set var="coord" value=""/>
				<c:forEach var="projeto" items="#{projetos}" varStatus="loop">
				<c:if test="${coord != projeto.coordenador.pessoa.nome}">
					<c:set var="coord" value="${projeto.coordenador.pessoa.nome}"/>
					<c:set var="ano" value=""/>
					<c:if test="${not loop.first}">
						<tr>
							<td class="spacer" colspan="3"></td>
						</tr>
					</c:if>
					<tr>
						<td class="coord" colspan="3">${coord}</td>
					</tr>

				</c:if>
			
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td >${projeto.codigo.ano},</td>
					<td >${projeto.titulo}</td>
					<td>
					<h:commandLink title="Visualizar Projeto de Pesquisa" action="#{consultaProjetos.view}" id="visualizar">	
						<f:param name="id" value="#{projeto.id}"/>
						<h:graphicImage url="/img/view.gif"/><br clear="all"/>
					</h:commandLink>
					</td>
				</tr>
				</c:forEach>
				
			</tbody>
			<tr><td colspan="3">&nbsp;<td></tr>
			<tfoot>
				<td colspan="3"><b>${fn:length(projetos)} Projeto(s) encontrado(s) </b></td>
			</tfoot>
		</table>
		</h:form>
	</c:if>
	<c:if test="${empty projetos}">
		<p class="vazio">
			Nenhum projeto de pesquisa cadastrado
		</p>
	</c:if>
		<!--  FIM CONTEÚDO  -->
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	