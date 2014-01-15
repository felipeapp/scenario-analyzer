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
		<div class="titulo">
			<h:outputText value="#{idioma.turmas}"/>
		</div>	
   	    
   	    <h:form id="form">
				<!-- INÍCIO DO FORMULÁRIO DE BUSCA -->
				<div id="caixa_formulario">
					   	<div id="icon_cf"><img src="img/icone_bus.png" /></div>
	                     <div id="formulario">
						   	 <div id="head_f">
		                          <h:outputText value="#{idioma.buscar}"/>
		                     </div>
		                     <div id="body_f">
		                     		<span class="campo_grande">
	                                   	<label>
	                                   		<h:outputText value="#{idioma.ano}"/> . 
											<h:outputText value="#{idioma.periodo}"/>:
										</label>
									</span>			
	 								<h:inputText value="#{portalPublicoCurso.anoTurma}"  size="4" 
	 								maxlength="4" id="inputAno" onkeyup="return formatarInteiro(this,event);" /> .
	 								<h:selectOneMenu	value="#{portalPublicoCurso.periodoTurma}" 
	 									id="inputPeriodo">
										<f:selectItem itemLabel="1" itemValue="1"/>
										<f:selectItem itemLabel="2" itemValue="2"/>
										<f:selectItem itemLabel="3" itemValue="3"/>
										<f:selectItem itemLabel="4" itemValue="4"/>
									</h:selectOneMenu>&nbsp;	
									<br/>
									<br/>
									<span class="campo_grande">
	                                   	<label>
	                                   		<h:outputText value="#{idioma.codigo}"/>:
										</label>
										<h:inputText value="#{portalPublicoCurso.codigoTurma}" size= "8" maxlength="8"/>
									</span>	
									<br/>
									<br/>
									<t:inputHidden id="id" value="#{portalPublicoCurso.curso.id}" />   
									<t:inputHidden id="lc" value="#{portalPublicoCurso.lc}" />
									<h:commandButton styleClass="bt_buscar" id="buscar" 
										action="#{portalPublicoCurso.buscarTurmas}" 
										value="#{idioma.buscar}"/>
		                     </div>
	                     </div>
	             </div>        
				<!-- FIM DO FORMULÁRIO DE BUSCA -->
				
				<!-- INÍCIO DA LISTAGEM -->
				<br clear="all"/>
				<c:set var="turmasGraduacao" value="#{portalPublicoCurso.turmas}"/>
				
	
				<c:if test="${not empty turmasGraduacao}">
					

					<c:set var="disciplinaAtual" value="" />

					<c:forEach items="#{turmasGraduacao}" var="turmaCurso" varStatus="status">

							<c:if test="${disciplinaAtual != turmaCurso.disciplina.codigo}">
								<c:if test="${!status.first}">
								</tbody>
								</table>
								</div>
								<br/>
								</c:if>
								<div id="listagem_tabela" class="espaco_menor">
								 
								 <div id="group_lt">
									<h:commandLink title="Visualizar Detalhes do Componente Curricular" 
										action="#{componenteCurricular.detalharComponente}">								
										<f:param name="id" value="#{turmaCurso.disciplina.id}"/>
										<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>							
										<h:outputText value="#{turmaCurso.disciplina.descricaoResumida}" />
										<%--<h:outputText value="#{turmaCurso.totalVagasReservadas)"/>--%>
									</h:commandLink>
								 </div>
								 
								<table id="table_lt">
										<tr class="campos">
											<td width="90px" class="anoPeriodo">
												<h:outputText value="#{idioma.periodo}"/>/
												<h:outputText value="#{idioma.ano}"/>
											</td>
											<td width="90px" class="anoPeriodo"><h:outputText value="#{idioma.turma}"/></td>
											<td width="330px"><h:outputText value="#{idioma.professor}"/></td>
											<c:if test="${portalPublicoCurso.curso.graduacao}">
											<td width="110px" class="colVagas">Vgs Reservadas</td>
											</c:if>
											<td width="65px"><h:outputText value="#{idioma.horarios}"/></td>
										</tr>

									<tbody>
										
							</c:if>
								
							<tr class="${status.index % 2 == 0 ? 'linha_par' : 'linha_impar'}">
								<td class="anoPeriodo"> ${turmaCurso.ano}.${turmaCurso.periodo}</td>
								<td align="center">${turmaCurso.codigo}</td>
								<td class="nome">
								
									<c:forEach items="#{turmaCurso.docentesTurmas}" var="_dturmas" varStatus="status2">
										
										<c:if test="${!status2.first}">, </c:if>
										<c:choose>
											<c:when test="${not empty _dturmas.docente.siape}">
											<a href="/sigaa/public/docente/portal.jsf?siape=${_dturmas.docente.siape}"
												target="_blank" title="${idioma.acessarPaginaPublicaDocente}">
												${_dturmas.docente.nome}
											</a>
											</c:when>
											<c:otherwise>
											<a href="/sigaa/public/docente/portal.jsf?siape=${_dturmas.docenteExterno.servidor.siape}"
												target="_blank" title="${idioma.acessarPaginaPublicaDocente}">
												${_dturmas.docenteExterno.nome}
											</a>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</td>
								<c:if test="${portalPublicoCurso.curso.graduacao}">
								<td class="colVagas">
									<h:outputText value="#{turmaCurso.totalVagasReservadas}"/>
								</td>
								</c:if>
								<td class="horario"> ${turmaCurso.descricaoHorario}</td>
							</tr>
							
						  <c:set var="disciplinaAtual"  value="${turmaCurso.disciplina.codigo}"/>
						  
						  <c:if test="${status.last}">
								</tbody>
								</table>
								</div>
						  </c:if>
						  
					</c:forEach>
			
					</tbody>
					
				</table>
				<div id="listagem_tabela" class="espaco_menor">
				<div id="foot_lt">
                     ${fn:length(portalPublicoCurso.turmas)} 
					<h:outputText value="#{idioma.turmas}"/>&nbsp;
					<h:outputText value="#{idioma.disponiveis}"/>
                </div>

				</div>	
				</c:if>
			
				<!-- FIM DA LISTAGEM -->
		</h:form>
		<!--  FIM CONTEÚDO  -->	
	</div>

	</f:view>
	<%@ include file="./include/rodape.jsp" %>