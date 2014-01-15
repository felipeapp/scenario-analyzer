	<%@ include file="./include/cabecalho.jsp" %>
	<f:view locale="#{portalPublicoCentro.lc}">
		<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
		<c:set var="secaoCentro" value="${portalPublicoCentro.secaoExtraSiteDetalhes}" />
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/centro.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
			<h1>Programas de Pós graduação do Centro</h1>
			<br>
			<c:set var="programas" value="${portalPublicoCentro.programas}" />
			<c:if test="${not empty programas}">
			
				<div class="legenda">
						<h:graphicImage url="/img/view.gif" width="12px" height="12px" />
						<b>: Visualizar Portal Público do Programa de Pós-Graduação</b>
						</td>
				</div>
				<br clear="all"/>
				
				<table class="listagem" style="width: 99%;">
						<h:form id="formListagemProgramas">
						<thead>
							<tr>
								<th>Nome do Programa</th>
								<th></th>
							</tr>
							</thead>
						<tbody>
						<c:forEach var="programa" items="#{programas}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td >
									<a href="../programa/portal.jsf?lc=${portalPublicoCentro.lc}
									&id=${programa.id}">
										${programa.nome}
									</a>
								</td>
								<td width="18px" >
									<a href="../programa/portal.jsf?lc=${portalPublicoCentro.lc}
									&id=${programa.id}">
										<h:graphicImage url="/img/view.gif"/>
									</a>
								</td>
							</tr>
								<c:set var="ultCentro" value="${programa.unidadeResponsavel}"/>
						</c:forEach>
						</tbody>
					</h:form>		
		
					<tfoot >
						<td colspan="2" align="center">
							<b>${fn:length(programas)}</b> Programa(s) encontrado(s)
						</td>
					</tfoot>
				</table>
						
			</c:if>
				
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	