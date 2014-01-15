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
			<h1>Cursos de Graduação do Centro</h1>
			<br>
			<c:set var="c" value="${portalPublicoCentro.cursos}" />
			<c:if test="${not empty cursos}">
			<div class="legenda">
					<td colspan="4">
					<h:graphicImage url="/img/view.gif" width="12px" height="12px" />
					<b>: Visualizar Portal Público do Curso de Graduação</b>
					</td>
			</div>
			<br clear="all"/>
			<h:form id="form">
			<table class="listagem" style="width: 99%;">
					<tbody>
						<thead>
							<tr>
							<th>Nome do Curso</th>
							<th>Municipio</th>
							<th>Modalidade</th>
							<th>Coordenador</th>
							<th></th>
							</tr>
						</thead>
						<tbody>	
						<c:forEach var="curso" items="#{cursos}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td >
									${curso.descricao} 
								</td>
								<td >
									${curso.municipio.nome}
								</td>
								<td>${curso.modalidadeEducacao.descricao}</td>
								<td>
								<a href="/sigaa/public/docente/portal.jsf?lc=${portalPublicoCentro.lc}
								&siape=${curso.coordenacaoAtual.servidor.siape}" id="coordenador">
								${curso.coordenacaoAtual.servidor.pessoa.nome}
								</a>
								</td>
								<td width="18px" >
									<a href="/sigaa/public/curso/portal.jsf?lc=${portalPublicoCentro.lc}
									&id=${curso.id}" id="visualizacao">
										<h:graphicImage url="/img/view.gif" id="view"/>
									</a>
								</td>
							</tr>
						</c:forEach>
						
					</tbody>
	
				<tfoot >
					<tr>
						<td colspan="5" align="center">
						<b>${fn:length(cursos)}</b> Curso(s) encontrado(s)
						</td>
					</tr>
				</tfoot>
			</table>		
				</h:form>		
			</c:if>
				
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	