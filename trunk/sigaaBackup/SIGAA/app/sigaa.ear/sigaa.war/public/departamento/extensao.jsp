	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  IN�CIO CONTE�DO -->
		<div id="acoesExtensao">
		
			<div id="titulo">
				<h1>A��es de Extens�o</h1>
			</div>
			<br/>
		
			<c:set var="projetos" value="#{portalPublicoDepartamento.projetosExtensao}" />
		
			<c:if test="${not empty projetos}">
				<table class="listagem">
					<tfoot >
						<td colspan="4">
						<h:graphicImage url="/img/view.gif" width="12px" height="12px" />
						<b>: Visualizar Detalhes das A��es de Extens�o</b>
						</td>
					</tfoot>
				</table>
				<h:form id="formListagemExtensao">
		 		<table class="listagem">
					<thead>
						<tr>
							<th class="ano" >Ano</th>
							<th colspan="2">T�tulo</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:set var="ano" value=""/>
						<c:set var="coord" value=""/>
						<c:forEach var="projeto" items="#{projetos}" varStatus="loop">
						<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td >${projeto.ano}</td>
							<td >${projeto.titulo}</td>
							<td >${projeto.unidade.gestora.sigla} </td>
							<td width="2%">
							<h:commandLink title="Visualizar A��es em Extens�o" action="#{inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento}">	
								<%-- <f:param name="buscaTitulo" value="#{projeto.titulo}"/> --%>
								<f:param name="idAtividadeExtensaoSelecionada" value="#{projeto.id}"/>
								<h:graphicImage url="/img/view.gif"/><br clear="all"/>
							</h:commandLink>
							</td>
						</tr>
						</c:forEach>
					</tbody>
					<tr><td colspan="4">&nbsp;<td></tr>
					<tfoot>
						<td colspan="4"><b>${fn:length(projetos)} Resultado(s) encontrada(s) </b></td>
					</tfoot>
				</table>
				</h:form>
			</c:if>
			
			<c:if test="${empty projetos}">
				<p class="vazio">
					Nenhum a��o cadastrada
				</p>
			</c:if>
	
		</div>
		<!--  FIM CONTE�DO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	