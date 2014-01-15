<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
	 
<style>
	.descricao{display: none;}
</style>

<script>
		function exibirResumo(el,view){
			jQuery(view).toggle();	
		}
</script>	 
	 
<%-- conteudo --%>
<div id="conteudo">

	<div class="titulo">
		<h:outputText value="#{idioma.areaConcentracao}"/>
	</div>
	
	<c:set var="areas" value="${portalPublicoPrograma.areaConcentracao}"/>
	
	<c:if test="${not empty areas}">

		<c:set var="nivelUltimo" value=""/>
		<c:forEach items="#{areas}" var="area" varStatus="status">
					
			<c:set var="linhas" value="#{area.linhasPesquisaStricto}" />
			<c:if test="${nivelUltimo != area.nivel}">

				<c:if test="${!status.first}">
					</div>
				</c:if>
						
				<div id="listagem_tabela">
					
						<div id="group_lt">
							<c:choose>
								<%-- SE AREA PERTENCE A DOUTORADO --%>
								<c:when test="${area.nivel=='D'}">
									<h:outputText value="#{idioma.doutorado}" />
								</c:when>
								<%-- SE AREA PERTENCE A MESTRADO --%>	
								<c:otherwise>
									<h:outputText value="#{idioma.mestrado}" />
								</c:otherwise>	
							</c:choose>
						</div>
						
						<c:set var="areaUltimo" value=""/>
							
			</c:if>
				<table id="table_lt">
					<tbody>
						<c:if test="${not empty area.denominacao && fn:length(area.denominacao)>3 && (empty areaUltimo || areaUltimo != area.denominacao)}">
							<tr class="campos">
								<td colspan="2">
									${area.denominacao}
								</td>
							</tr>		
						</c:if>
					
					</tbody>
				</table>	
							
				<table id="table_lt">
					<tbody>	
					<c:choose>		
						<c:when test="${not empty linhas}">		
									<tr>
										<td class="txtLinhaPesquisa" colspan="2">
											<h:outputText value="#{idioma.linhasPesquisas}"/> :
										</td>
									</tr>
							
									<c:forEach items="#{linhas}" var="linha" varStatus="status2">
										<tr>
											<td  class="denominacao"> &rsaquo; ${linha.denominacao}</td>
											<c:if test="${not empty linha.descricao}">
												<td width="5%"> 
													<img src="${ctx}/img/biblioteca/emprestimos_ativos.png" 
													 onClick="exibirResumo(this,'#trOpcoes${linha.id}')" style="cursor: pointer"/>
												</td>
											</c:if>
										</tr>
										<tr  >
											<td  class="descricao" id="trOpcoes${linha.id}">
				             				   ${area.denominacao}-	${linha.descricao}
				             				</td>
				             			</tr>
									</c:forEach>
									
						</c:when>
						<c:otherwise>
								<tr>
									<td class="txtLinhaPesquisa" colspan="2">
									  <h:outputText value="#{idioma.vazio}" />
									</td>
								</tr>
						</c:otherwise>
					</c:choose>		
					</tbody>
				</table>
											
					
			<c:set var="areaUltimo" value="${area.denominacao}"/>
			<c:set var="nivelUltimo" value="${area.nivel}"/>

		</c:forEach>
	
	</c:if>	
	
	</div>
	
	<c:if test="${empty areas}">
		<p class="vazio">
			<h:outputText value="#{idioma.vazio}"/>
		</p>	
	</c:if>
	
</div>
</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>
