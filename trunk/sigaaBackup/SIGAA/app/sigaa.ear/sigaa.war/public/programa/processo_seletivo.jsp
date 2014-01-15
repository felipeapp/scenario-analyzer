<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>

	 
<div id="conteudo">
<%-- INÍCIO CONTEÚDO --%>
	
	<div class="titulo">
		<h:outputText value="#{idioma.processoSeletivo}"/>
	</div>
	
	<c:set var="nivelUltimo" value=""/>
	<c:set var="processos" value="#{portalPublicoPrograma.processosSeletivos}"/>

	<h:form id="formListagemProcessoSeletivo">

	<c:if test="${not empty processos}">

		<%-- LISTAGEM DOS PROCESSOS SELETIVOS AGRUPADOS PELO NÍVEL DO CURSO --%>
		<div id="listagem_tabela">	
			
			
			<%-- DOUTORADO --%>
			<div id="group_lt">
				${idioma.doutorado} 
			</div>
						
			<table id="table_lt">
				<tbody>	
					<tr class="campos">
						<td class="centro" width="120px"><h:outputText value="#{idioma.periodo}"/></td>
						<td class="esquerda"><h:outputText value="#{idioma.processoSeletivo}"/></td>
					</tr>
		
					<c:forEach items="#{portalPublicoPrograma.processosSeletivosDoutorado}" var="psd" varStatus="status">
					<tr>
						<td class="centro" width="120px"> 
							<fmt:formatDate value="${psd.editalProcessoSeletivo.inicioInscricoes}"/> à
							<fmt:formatDate value="${psd.editalProcessoSeletivo.fimInscricoes}"/>
						</td>
						<td class="esquerda">
					   		<h:commandLink target="_blank" id="visualizarProcessoSeletivoMestrado" title="#{idioma.visualizarProcessoSeletivo}" 
						    	action="#{processoSeletivo.viewPublico}">
							    	<h:outputText value="#{psd.editalProcessoSeletivo.nome}" styleClass="#{psd.inscricoesAbertas?'destaqueProcessoeletivo':''}" />
							        <f:param name="id" value="#{psd.id}" />
							        <span class="data ${psd.inscricoesAbertas?'destaqueProcessoeletivo':''}">
							       	 (<ufrn:format type="data" valor="${psd.editalProcessoSeletivo.inicioInscricoes}"/>
									 : <ufrn:format type="data" valor="${psd.editalProcessoSeletivo.fimInscricoes}"/>)
									</span>
						    </h:commandLink>
						</td>
					</tr>	
					</c:forEach>
				</tbody>
			</table>
			
			<br clear="all"/>
			
			
			<%-- MESTRADO --%>
			<div id="group_lt">
				${idioma.mestrado} 
			</div>
			
			<table id="table_lt">
				<tbody>	
					<tr class="campos">
						<td class="centro" width="120px"><h:outputText value="#{idioma.periodo}"/></td>
						<td class="esquerda"><h:outputText value="#{idioma.processoSeletivo}"/></td>
					</tr>
		
					<c:forEach items="#{portalPublicoPrograma.processosSeletivosMestrado}" var="psd" varStatus="status">
					<tr>
						<td class="centro" width="120px"> 
							<fmt:formatDate value="${psd.editalProcessoSeletivo.inicioInscricoes}"/> à
							<fmt:formatDate value="${psd.editalProcessoSeletivo.fimInscricoes}"/>
						</td>
						<td class="esquerda">
					   		<h:commandLink target="_blank" id="visualizarProcessoSeletivoDoutorado" title="#{idioma.visualizarProcessoSeletivo}" 
						    	action="#{processoSeletivo.viewPublico}">
							    	<h:outputText value="#{psd.editalProcessoSeletivo.nome}" styleClass="#{psd.inscricoesAbertas?'destaqueProcessoeletivo':''}" />
							        <f:param name="id" value="#{psd.id}" />
							        <span class="data ${psd.inscricoesAbertas?'destaqueProcessoeletivo':''}">
							       	 (<ufrn:format type="data" valor="${psd.editalProcessoSeletivo.inicioInscricoes}"/>
									 : <ufrn:format type="data" valor="${psd.editalProcessoSeletivo.fimInscricoes}"/>)
									</span>
						    </h:commandLink>
						</td>
					</tr>	
					</c:forEach>
				</tbody>
			</table>
			
			
		</div>	
	</c:if>
	
	</h:form>
	
	<c:if test="${empty processos}">
		<p class="vazio"><h:outputText value="#{idioma.vazio}"/></p>
	</c:if>

<%--  FIM CONTEÚDO  --%>	
</div>	

</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>