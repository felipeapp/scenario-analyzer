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
	
	<div class="titulo"><h:outputText value="#{idioma.curriculos}"/></div>

		<h:form id="formCurriculosCurso">
			  <div class="legenda"> 
				<h:graphicImage url="/img/view.gif" />:&nbsp;<h:outputText value="#{idioma.visualizarEstruturaCurricular}"/>
				<h:graphicImage url="/img/report.png" rendered="#{portalPublicoCurso.curso.graduacao || portalPublicoCurso.curso.stricto}" style="overflow: visible; left: 4px;"/>:&nbsp;<h:outputText value="#{idioma.relatorioEstruturaCurricular}" rendered="#{portalPublicoCurso.curso.graduacao || portalPublicoCurso.curso.stricto}" />
			  </div>
			  <div id="listagem_tabela">
				<c:set var="areaUltima" value=""/>		
					<br clear="all"/>
		            <div id="group_lt">
                    	<h:outputText value="#{idioma.nome}"/>
					</div>
					<table id="table_lt" width="100%">
						
						<tbody>
						<input type="hidden" name="nivel" value="${portalPublicoCurso.curso.nivel}" />
						<!-- CURSO GRADUAÇÃO, STRICTO E LATOS -->
						<c:if test="${portalPublicoCurso.curso.graduacao || portalPublicoCurso.curso.stricto || portalPublicoCurso.curso.lato}">
							<c:set var="curriculosGraduacao" value="#{portalPublicoCurso.curriculos}"/>
							<c:set var="matriz" />
							<c:forEach items="#{curriculosGraduacao}" var="curriculoLoop" varStatus="loop">
	
								<c:if test="${not empty curriculoLoop.matriz 
									and matriz != curriculoLoop.matriz.id}">
									<c:set var="matriz" value="${curriculoLoop.matriz.id}" />
									<tr class="campos">
										<td  colspan="2">${curriculoLoop.matriz.descricaoMin}</td>
									</tr>
								</c:if>
	
								<tr class="${loop.index % 2 == 0 ? 'linha_par' : 'linha_impar' }">
								
									<td>
									<h:outputText value="#{idioma.detalhesEstruturaCurricular}" /> ${curriculoLoop.codigo},
									<h:outputText value="#{idioma.implementadaEm}" /> ${curriculoLoop.anoEntradaVigor}
									</td>
									<td width="5%" align="right">
									<h:commandLink title="Visualizar Estrutura Curricular"
										action="#{consultaPublicaCursos.detalhesCurriculo}">
										<h:graphicImage url="/img/view.gif" />
										<f:param name="id" value="#{curriculoLoop.id}" />
									</h:commandLink>
									<h:commandLink
										title="Relatório da Estrutura Curricular"
										action="#{consultaPublicaCursos.relatorioCurriculo}">
										<h:graphicImage url="/img/report.png" />
										<f:param name="id" value="#{curriculoLoop.id}" />
									</h:commandLink>
									</td>
									
								</tr>
							</c:forEach>
						</c:if>
						
						<!-- CURSO TÉCNICO -->
						<c:if test="${portalPublicoCurso.curso.tecnico}">
							<c:set var="curriculosTec" value="#{portalPublicoCurso.curriculosTecnicos}"/>
								<c:forEach items="#{curriculosTec}"
									var="itemLoop" varStatus="loop">
									
									<c:if test="${not empty itemLoop.cursoTecnico.nome
										and matriz != itemLoop.cursoTecnico.nome}">
										<c:set var="matriz" value="${itemLoop.cursoTecnico.id}" />
										<tr class="campos">
											<td colspan="2">${itemLoop.cursoTecnico.nome}</td>
										</tr>
									</c:if>
									
									<tr class="${loop.index % 2 == 0 ? 'linha_par' : 'linha_impar' }">
										<td>
										<h:outputText value="#{idioma.detalhesEstruturaCurricular}" /> ${itemLoop.codigo},
										<h:outputText value="#{idioma.implementadaEm}" /> ${itemLoop.anoEntradaVigor}
										</td>
										<td width="1%" align="right">
										<h:commandLink title="Visualizar Estrutura Curricular"
											action="#{consultaPublicaCursos.detalhesCurriculoTecnico}">
											<h:graphicImage url="/img/view.gif" />
											<f:param name="id" value="#{itemLoop.id}" />
										</h:commandLink>
										</td>
									</tr>
								</c:forEach>
							</c:if>
							
							<tr  class="campos">
								<td colspan="2">
									<b>${fn:length(curriculoLoop)+fn:length(curriculosTec)+fn:length(curriculosGraduacao)} 
									<h:outputText value="#{idioma.curriculos}" />&nbsp;<h:outputText value="#{idioma.disponiveis}" /> 
									</b>
								</td>
							</tr>
						</tbody>
					</table>	
			
				</div>	
		</h:form>
		<!--  FIM CONTEÚDO  -->	
</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>