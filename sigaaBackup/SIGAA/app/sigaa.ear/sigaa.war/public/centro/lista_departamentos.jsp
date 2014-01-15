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
			<h1>Departamentos do Centro</h1>
			<br>
		
			<c:set var="depto" value="${portalPublicoCentro.departamentos}" />

			<c:if test="${not empty departamentos}">
			<div class="legenda">
					<td colspan="4">
					<h:graphicImage url="/img/view.gif" width="12px" height="12px" />
					<b>: Visualizar Portal Público do Departamento</b>
					</td>
			</div>
			<br clear="all"/>
			<table class="listagem" style="width: 99%;">
					<thead>
						<tr>
						<th>Nome do Departamento</th>
							<th></th>
						</tr>
					</thead>	

					<tbody>
					<h:form id="formListagemDepartamentos">
						<c:forEach var="departamento" items="#{departamentos}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>
									<a href="../departamento/portal.jsf?lc=${portalPublicoCentro.lc}
									&id=${departamento.id}" style="link">
									${departamento.nome}<br/>
									</a>
									<c:if test="${departamento.detalhesSite.url}">
									<br/>${departamento.detalhesSite.url}
									</c:if>
								</td>
									<td width="18px" class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<a title="Clique aqui para acessar a página pública deste 
										departamento" href="../departamento/portal.jsf?id=${departamento.id}">
										<h:graphicImage url="/img/view.gif"/>
										</a>
									</td>
								</tr>
	 					</c:forEach>
					</h:form>		
				</tbody>
		
				<tfoot >
					<td colspan="3" align="center">
						<b>${fn:length(departamentos)}</b> Departamento(s) encontrado(s)
					</td>
				</tfoot>
			</table>		
			</c:if>
				
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	