<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete"
	value="return confirm('Tem certeza que deseja remover este arquivo?');"
	scope="request" />
<f:view>
	<c:if test="${documentoSite.portalDocente}"> 
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${documentoSite.portalCoordenadorStricto}">
		<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${documentoSite.portalCoordenadorGraduacao}">
		<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h2><ufrn:subSistema /> &gt; Lista dos Portais dos Cursos de Nível ${documentoSite.subSistema.nome}</h2>

	<h:outputText value="#{documentoSite.create}" />

	<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>
		Nesta operação possibilita ao administrador gerenciar todos os portais dos cursos de nível ${documentoSite.subSistema.nome}
		vinculados a unidade, ou seja, alterar informações da página principal do portal, listar/alterar e remover notícias, documento e seções extras.
	</p>
	</div>
	<h:form>
	<div class="infoAltRem">
	   <!--	
       <h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Publicar
	   <h:graphicImage value="/img/check.png" style="overflow: visible;" />: Despublicar 
	   -->
	   <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Detalhes
	   <h:graphicImage value="/img/sites/gerenciar_noticias.gif" style="overflow: visible;" />: Gerenciar Notícias
	   <h:graphicImage value="/img/sites/gerenciar_documentos.gif" style="overflow: visible;" />: Gerenciar Documentos
	   <h:graphicImage value="/img/sites/gerenciar_secao_extra.gif" style="overflow: visible;" />: Gerenciar Seções Extras
	</div>

		<table class="listagem" style="width: 100%">
			<caption>Lista dos Portais Públicos do Cursos</caption>
			<thead>
				<tr>
					<th>Curso</th>
					<th colspan="6" width="1%"></th>
				</tr>
			</thead>
			<c:set var="_cursos" value="#{detalhesSite.cursos}" />
			<c:choose>

				<c:when test="${not empty _cursos}">
					<c:forEach items="#{_cursos}" var="c" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td width="50%">${c.nome}</td>
							<%-- 
							<td>
								<h:commandLink title="#{item.publicado ? 'Desp' : 'P'}ublicar" action="#{detalhesSite.iniciarDetalhesCurso}">
								    <h:graphicImage url="/img/check#{not item.publicado ? '_cinza' : ''}.png" />
									<f:param name="idCurso" value="#{c.id}"/>
									<f:setPropertyActionListener value="#{true}" target="#{detalhesSite.obj.publicado}"/>
								</h:commandLink>	
							</td>
							--%>
							<td>
							<h:commandLink styleClass="noborder" title="Alterar Detalhes do Portal"
								action="#{detalhesSite.iniciarDetalhesCurso}" immediate="true">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="idCurso" value="#{c.id}" />
							</h:commandLink>
							</td>
							<td>
							<h:commandLink styleClass="noborder" title="Gerenciar Notícias do Portal"
								action="#{noticiaSite.listarCurso}" immediate="true">
								<h:graphicImage url="/img/sites/gerenciar_noticias.gif" />
								<f:param name="idCurso" value="#{c.id}" />
							</h:commandLink>
							</td>
							<td>
							<h:commandLink styleClass="noborder" title="Gerenciar Documentos do Portal"
								action="#{documentoSite.listarCurso}" immediate="true">
								<h:graphicImage url="/img/sites/gerenciar_documentos.gif" />
								<f:param name="idCurso" value="#{c.id}" />
							</h:commandLink>
							</td>
							<td>
							<h:commandLink styleClass="noborder" title="Gerenciar Seções Extra do Portal"
								action="#{secaoExtraSite.listarCurso}" immediate="true">
								<h:graphicImage url="/img/sites/gerenciar_secao_extra.gif" />
								<f:param name="idCurso" value="#{c.id}" />
							</h:commandLink>
							</td>
						
						</tr>
					</c:forEach>
				</c:when>

				<c:otherwise>
					<tr>
						<td colspan="6" align="center">
						<i>	Nenhum documento cadastrado até o momento</i>
						</td>
					</tr>
				</c:otherwise>

			</c:choose>

			<tfoot>
				<tr>
					<td colspan="6" align="center">&nbsp;
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>