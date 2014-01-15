<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%--  INÍCIO CONTEÚDO --%>
<div id="conteudo">
	<div class="titulo">
		<h:outputText value="#{idioma.turmas}"/>
	</div>

				<h:form id="form">
					<div id="caixa_formulario">
                    	
                    	<div id="icon_cf"><img src="img/icone_bus.png" /></div>
	                     
	                    <div id="formulario">
	                          <div id="head_f">
	                               <h:outputText value="#{idioma.buscar}"/>
	                          </div>
	                           <div id="body_f">
	                               	<span class="campo">
	                                   	<label><h:outputText value="#{idioma.ano}"/> . <h:outputText value="#{idioma.periodo}"/>:</label>
	                                    <h:inputText id="anoTurma" value="#{portalPublicoPrograma.anoTurma}" maxlength="4" />
	                                    &nbsp;.&nbsp;
	                                    <h:selectOneMenu value="#{portalPublicoPrograma.periodoTurma}" id="inputPeriodo">
											<f:selectItem itemLabel="1" itemValue="1"/>
											<f:selectItem itemLabel="2" itemValue="2"/>
											<f:selectItem itemLabel="3" itemValue="3"/>
											<f:selectItem itemLabel="4" itemValue="4"/>
										</h:selectOneMenu>&nbsp;
                                   </span>
                              		<h:commandButton styleClass="bt_buscar" action="#{portalPublicoPrograma.buscarTurmas}" id="buscarTurmas" value=""/>
	                           </div>
	                    </div>
	                     
                    </div>
				</h:form>
				
				<br clear="all"/>
					
				<div id="listagem_tabela">
				<c:if test="${not empty portalPublicoPrograma.turmas}">
					
					<c:set var="disciplinaAtual" value="" />
					<h:form id="formListaTurmas">

	
						<div id="head_lt">
							<h:outputText value="#{idioma.resultadoConsulta}"/>
								 (${fn:length(portalPublicoPrograma.turmas)})
						</div>

						<c:forEach items="#{portalPublicoPrograma.turmas}" var="turmaPrograma" varStatus="status">
	
							<c:if test="${disciplinaAtual != turmaPrograma.disciplina}">
	
								<c:if test="${!status.first}">
									</tbody>
									</table>
									<br clear="all"/>	
								</c:if>

								<div id="group_lt">
								<c:if test="${not empty turmaPrograma.disciplina && turmaPrograma.disciplina.id>0}">
									<h:commandLink value="#{turmaPrograma.disciplina.descricaoResumida}"
										id="descricaoResumida" 
										target="_blank" title="#{idioma.visualizarDetalhesComponente}" 
										rendered="#{not empty turmaPrograma.disciplina}" 
										action="#{componenteCurricular.detalharComponente}">								
										<f:param name="id" value="#{turmaPrograma.disciplina.id}"/>
										<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>							
						
									</h:commandLink>
								</c:if>	
								</div>
								<table id="table_lt">	
									<tbody>
										<tr class="campos">
											<td class="centro" width="60px"><h:outputText value="#{idioma.turmas}"/></td>
											<td class="centro" width="90px"><h:outputText value="#{idioma.periodo}/#{idioma.ano}"/></td>
											<td width="720px"><h:outputText value="#{idioma.professores}"/></td>
											<td class="centro" width="65px"><h:outputText value="#{idioma.horarios}"/></td>
										</tr>
								
							</c:if>
							
							<tr class="${status.index % 2 == 0 ? '' : 'linha_impar'}">
								<td class="centro" width="60px">Turma ${fn:replace(turmaPrograma.codigo, "Turma", "")}</td>
								<td class="centro" width="90px">${turmaPrograma.ano}.${turmaPrograma.periodo}</td>
								<td width="720px">${turmaPrograma.docentesNomesCh}</td>
								<td class="centro" width="65px"> ${turmaPrograma.descricaoHorario}</td>
							</tr>
						  <c:set var="disciplinaAtual"  value="${turmaPrograma.disciplina}"/>
						
						</c:forEach>
						</tbody>
					</table>
				</h:form>
				</c:if>
			</div>	
	
		<c:if test="${portalPublicoPrograma.submetido && empty portalPublicoPrograma.turmas}">
					<p class="vazio">
						<h:outputText value="#{idioma.vazio}" />
					</p>
		</c:if>
		<%--  FIM CONTEÚDO  --%>	
	</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>