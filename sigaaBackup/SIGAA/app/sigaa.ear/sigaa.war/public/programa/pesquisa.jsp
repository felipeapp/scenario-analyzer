<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- conteudo --%>
<div id="conteudo">
	<div class="titulo"><h:outputText value="#{idioma.projetoPesquisa}"/></div>

	<c:set var="projetos" value="#{portalPublicoPrograma.projetosPesquisa}" />

	<c:if test="${not empty projetos}">
	<h:form id="formProjetosPesquisa">
			<c:set var="ano" value=""/>
			
				<c:forEach var="proj" items="#{projetos}" varStatus="loop">
						<c:if test="${ano != proj.projeto.codigo.ano}">
							<c:set var="ano" value="${proj.projeto.codigo.ano}"/>
							<c:if test="${not loop.first}">
									</tbody>
								</table>
								</div>
								<br clear="all"/>	
							</c:if>
	
							<div id="listagem_tabela">
								<div id="group_lt">
									${ano}
								</div>
		
								<table id="table_lt">
									<tbody>
						</c:if>
			
					<tr class="${loop.index % 2 == 0 ? '' : 'linha_impar'}">
						<td  valign="top" colspan="2">
							<h:commandLink target="_blank" title="#{idioma.visualizarDetalhesPesquisa}" 
								action="#{consultaProjetos.view}">	
							 	${proj.projeto.titulo}
								<f:param name="id" value="#{proj.projeto.id}"/>
							</h:commandLink>
						 </td>
					</tr>
					
				</c:forEach>
				</tbody>
			</table>
	</h:form>		
	</c:if>
	<c:if test="${empty projetos}">
		<p class="vazio">
		<h:outputText value="#{idioma.vazio}" />
		</p>
	</c:if>
		</div>
		<%--  FIM CONTEÚDO  --%>	
		</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>