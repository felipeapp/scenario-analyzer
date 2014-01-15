<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>


<f:view locale="#{portalPublicoCurso.lc}">
<a4j:keepAlive beanName="portalPublicoCurso"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>	
<%@ include file="include/curso.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>

 
<%-- conteudo --%>
<div id="conteudo">
	
	<div class="titulo"><h:outputText value="#{idioma.alunosAtivos}"/></div>


			<c:set var="nivelUltimo" value=""/>
			<c:set var="alunosAt" value="${portalPublicoCurso.discentes}"/>
			<c:set var="areaUltima"  value=""/>

			<div id="listagem_tabela">
				<c:if test="${not empty alunosAt}">
	
					<table  id="table_lt" cellpadding="0" cellspacing="0">
					<tr  class="campos">
						<td  width="100px" class="colMatricula"><h:outputText value="#{idioma.matricula}"/></td>
						<td><h:outputText value="#{idioma.aluno}"/></td>
					</tr>
		
					<c:forEach items="${alunosAt}" var="aluno" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td  class="colMatricula">${aluno.matricula}</td>
						<td>${aluno.nome}</td>
					</tr>	
					</c:forEach>
					
					<tr class="campos">
						<td colspan="3">
						<b>${fn:length(alunosAt)}
						<h:outputText value="#{idioma.alunos}"/>&nbsp;<h:outputText value="#{idioma.disponiveis}"/>
						</b>
						</td>
					</tr>
					</table>
				</c:if>
				<c:if test="${empty alunosAt}">
					<p class="vazio"><h:outputText value="#{idioma.vazio}"/></p>
				</c:if>
			</div>	
		<!--  FIM CONTEÚDO  -->	

	</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>