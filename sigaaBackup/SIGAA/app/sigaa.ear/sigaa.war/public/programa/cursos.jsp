<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
<h:outputText value="#{consultaPublicaCursos.create}"/>

<%--  INÍCIO CONTEÚDO --%>
<div id="conteudo">
	<div class="titulo">
		<h:outputText value="#{idioma.cursos}"/>
	</div>

	<c:set var="cursosProg" value="#{portalPublicoPrograma.cursosPrograma}"/>
	<c:choose>

		<c:when test="${not empty cursosProg}">

			<h:form id="formListaCursoPosGraduacao">
				<c:forEach items="#{cursosProg}" var="cursoPrograma" varStatus="status">

					<div id="listagem_tabela">
						
						<div id="group_lt">
								${cursoPrograma.nome} -
								 <h:outputText value="#{idioma.mestrado}" rendered="#{cursoPrograma.mestrado && !cursoPrograma.mestradoProfissional}" />
								 <h:outputText value="#{idioma.mestradoProfissional}" rendered="#{cursoPrograma.mestradoProfissional}" />  
								 <h:outputText value="#{idioma.doutorado}" rendered="#{cursoPrograma.doutorado}" />
						</div>
						
						<table id="table_lt">
							<tbody>
								<tr>
									<td class="${status.index % 2 == 0 ? '' : 'linha_impar'}">
										 <a class="cor" href="${cursoPrograma.enderecoUrlCapes}" target="_blank" class="link">
											&rsaquo; <h:outputText value="#{idioma.documentoCapes}" /> 
										 </a>
										 <br clear="all"/>
										 <h:commandLink styleClass="cor" target="_blank" title="Visualizar Detalhes do Curso"
											action="#{consultaPublicaCursos.detalhes}">
												&rsaquo;	
												<f:param name="id" value="#{cursoPrograma.id}" />
												<f:param name="nivel" value="#{cursoPrograma.nivel}" />
												<h:outputText value="#{idioma.detalhesCurso}" /> 
										 </h:commandLink>  
									</td>
								</tr>
							</tbody>
						</table>
						<br clear="all"/>
					</div>
				</c:forEach>
			</h:form>
		
		</c:when>
		<c:otherwise>
			<p class="vazio">
				<h:outputText value="#{idioma.vazio}"/>	
			</p>
		</c:otherwise>
	</c:choose>

<%--  FIM CONTEÚDO  --%>	
	</div>
</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>