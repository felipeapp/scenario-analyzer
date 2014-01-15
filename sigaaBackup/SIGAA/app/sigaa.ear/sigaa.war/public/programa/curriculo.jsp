<%-- CABEÇALHO --%>
<%@ include file="include/cabecalho.jsp" %>

<style>
.colCodigo{width: 50px !important;text-align: center !important;}	
.colAnoPeriodo{width: 70px !important;text-align: center !important;}
.colComponente{}
.negrito{font-weight: bold !important;}
</style>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- TOPO --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- INÍCIO DO CONTEÚDO --%>
<div id="conteudo">
	<div class="titulo">
		<h:outputText value="#{idioma.gradeCurricular}"/>
	</div>

	<h:form id="formListaCurriculoPosGraduacao">
	
		<c:set var="areaUltima" value=""/>		
		<c:set var="gradesCurr" value="#{portalPublicoPrograma.gradeCurricular}"/>

		<div id="listagem_tabela">
		<c:choose>
		
			<c:when test="${not empty gradesCurr}">
			<c:set var="ultimoCUrso" value=""/>
				
			<c:forEach items="#{gradesCurr}" var="item" varStatus="status">
			
				<%-- INÍCIO DO CABEÇALHO DA LISTAGEM --%>
				<c:if test="${ultimoCurso != item.curso.nivel }">
						
					<c:if test="${!status.first}">
							</tbody>
						</table>
						<br clear="all"/>
					</c:if>
						
					<div id="group_lt">
						<h:outputText value="#{idioma.mestrado}" rendered="#{item.curso.mestrado && !item.curso.mestradoProfissional}" />
						<h:outputText value="#{idioma.mestradoProfissional}" rendered="#{item.curso.mestradoProfissional}" />  
						<h:outputText value="#{idioma.doutorado}" rendered="#{item.curso.doutorado}" />
					</div>
				
					<table id="table_lt">
						
						<tbody>	 
							
							<tr class="campos">
								<td class="colCodigo"> <h:outputText value="#{idioma.codigo}"/> </td>
								<td class="colAnoPeriodo"> <h:outputText value="#{idioma.ano}"/>/<h:outputText value="#{idioma.periodo}"/> </td>
								<td class="colComponente"> <h:outputText value="#{idioma.componente}"/> </td>
							</tr>
				</c:if>	
				<%-- FIM DO CABEÇALHO DA LISTAGEM --%>
				
				<c:set var="ultimoCurso" value="${item.curso.nivel}"/>
					
				<tr class="${status.index % 2 == 0 ? '' : 'linha_impar'}">
					<td class="colCodigo ${status.first?'negrito':''}">${item.codigo}</td>
					<td class="colAnoPeriodo ${status.first?'negrito':''}">
						<h:commandLink title="#{idioma.visualizarDetalhesComponente}" action="#{portalPublicoPrograma.detalharCurriculo}">
							${item.anoPeriodo} 
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>	
					</td>
					<td class="colComponente ${status.first?'negrito':''}">
						<h:commandLink title="#{idioma.visualizarDetalhesComponente}" action="#{portalPublicoPrograma.detalharCurriculo}">
							${item.curso.descricao} 
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
						
			
			</c:forEach>
					</tbody>
					
				</table>
			
			</c:when>	
		
			<c:otherwise>
				<p class="vazio">
					<h:outputText value="#{idioma.vazio}"/>	
				</p>
			</c:otherwise>
			
		</c:choose>
		
		</div>
			
	</h:form>
</div>
<%--  FIM CONTEÚDO  --%>	

</f:view>
<%@ include file="./include/rodape.jsp" %>