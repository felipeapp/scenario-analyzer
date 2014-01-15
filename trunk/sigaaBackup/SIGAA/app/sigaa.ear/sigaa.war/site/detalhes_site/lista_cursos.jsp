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
	
	<h2><ufrn:subSistema /> &gt; Lista dos Portais dos Cursos de N�vel ${documentoSite.subSistema.nome}</h2>

	<h:outputText value="#{documentoSite.create}" />

	<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
	<p>
		Nesta opera��o possibilita ao administrador gerenciar todos os portais dos cursos de n�vel ${documentoSite.subSistema.nome}
		vinculados a unidade, ou seja, alterar informa��es da p�gina principal do portal, listar/alterar e remover not�cias, documento e se��es extras.
	</p>
	</div>
	<h:form>
	<div class="infoAltRem">
	   <!--	
       <h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Publicar
	   <h:graphicImage value="/img/check.png" style="overflow: visible;" />: Despublicar 
	   -->
	   <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Detalhes
	   <h:graphicImage value="/img/sites/gerenciar_noticias.gif" style="overflow: visible;" />: Gerenciar Not�cias
	   <h:graphicImage value="/img/sites/gerenciar_documentos.gif" style="overflow: visible;" />: Gerenciar Documentos
	   <h:graphicImage value="/img/sites/gerenciar_secao_extra.gif" style="overflow: visible;" />: Gerenciar Se��es Extras
	</div>

		<table class="listagem" style="width: 100%">
			<caption>Lista dos Portais P�blicos do Cursos</caption>
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
							<h:commandLink styleClass="noborder" title="Gerenciar Not�cias do Portal"
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
							<h:commandLink styleClass="noborder" title="Gerenciar Se��es Extra do Portal"
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
						<i>	Nenhum documento cadastrado at� o momento</i>
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