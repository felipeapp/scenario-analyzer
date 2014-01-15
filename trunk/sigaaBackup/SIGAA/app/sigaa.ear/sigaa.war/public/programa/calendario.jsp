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
	
	<div class="titulo"><h:outputText value="#{idioma.calendario}"/></div>


				
				<c:set var="calendarioVigente" value="#{portalPublicoPrograma.calendarioVigente}" />
				<c:set var="proximoPeriodo" value="#{portalPublicoPrograma.calendarioProximoPeriodo}" />
				<c:if test="${not empty calendarioVigente}">
					<div id="listagem_tabela">
					
						<div id="group_lt">
							${calendarioVigente.ano}.${calendarioVigente.periodo} (${idioma.vigente})
						</div>	
	
						<table id="table_lt">
							<tbody>
								<c:if test="${not empty calendarioVigente.inicioMatriculaOnline}">
									<tr>
									<td>
									<b><ufrn:format type="data" valor="${calendarioVigente.inicioMatriculaOnline}"/> à <ufrn:format type="data" valor="${calendarioVigente.fimMatriculaOnline}"/></b><br clear="all"/>
										&rarr;&nbsp;<h:outputText value="#{idioma.matricular}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</td>
									</tr>
								</c:if>
								<c:if test="${not empty calendarioVigente.inicioReMatricula}">
									<tr>
									<td>
									<b><ufrn:format type="data" valor="${calendarioVigente.inicioReMatricula}"/> à <ufrn:format type="data" valor="${calendarioVigente.fimReMatricula}"/></b><br clear="all"/>
										&rarr;&nbsp;<h:outputText value="#{idioma.rematricular}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</td>
									</tr>
								</c:if>
								<c:if test="${not empty calendarioVigente.inicioPeriodoLetivo}">
									<tr>
									<td>
										<b>	<ufrn:format type="data" valor="${calendarioVigente.inicioPeriodoLetivo}"/> </b><br clear="all"/>
										&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoLetivo}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</td>
									</tr>
								</c:if>
								<c:if test="${not empty calendarioVigente.inicioTrancamentoTurma}">
								<tr>
									<td>
										<b>
										<ufrn:format type="data" valor="${calendarioVigente.inicioTrancamentoTurma}"/></b><br clear="all"/>
										&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoTrancamento}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</td>
									</tr>
								</c:if>
								<c:if test="${not empty calendarioVigente.fimTrancamentoTurma}">
									<tr>
									<td>
										<b><ufrn:format type="data" valor="${calendarioVigente.fimTrancamentoTurma}"/> </b><br clear="all"/>
										&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoTrancamento}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</td>
									</tr>
								</c:if>
								<c:if test="${not empty calendarioVigente.fimPeriodoLetivo}">
									<tr>
									<td>
										<b>	<ufrn:format type="data" valor="${calendarioVigente.fimPeriodoLetivo}"/></b><br clear="all"/>
										&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoLetivo}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</td>
									</tr>
								</c:if>
							</tbody>	
						</table>
					</div>
				</c:if>			
				<c:if test="${not empty proximoPeriodo}">
					<div id="listagem_tabela">
					
						<div id="group_lt">
							${proximoPeriodo.ano}.${proximoPeriodo.periodo}
						</div>	
	
						<table id="table_lt">
							<tbody>
							<c:if test="${not empty proximoPeriodo.inicioMatriculaOnline}">
								<tr>
								<td>
								<b><ufrn:format type="data" valor="${proximoPeriodo.inicioMatriculaOnline}"/> - <ufrn:format type="data" valor="${proximoPeriodo.fimMatriculaOnline}"/></b><br clear="all"/>
									&rarr;&nbsp;<h:outputText value="#{idioma.matricular}"/> ${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
								</td>
								</tr>
							</c:if>
						<c:if test="${not empty proximoPeriodo.inicioReMatricula}">
							<tr>
								<td>
								<b><ufrn:format type="data" valor="${proximoPeriodo.inicioReMatricula}"/> - <ufrn:format type="data" valor="${proximoPeriodo.fimReMatricula}"/></b><br clear="all"/>
									&rarr;&nbsp;<h:outputText value="#{idioma.rematricular}"/> ${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty proximoPeriodo.inicioPeriodoLetivo}">
							<tr>
								<td>
									<b><ufrn:format type="data" valor="${proximoPeriodo.inicioPeriodoLetivo}"/> </b><br clear="all"/>
									&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoLetivo}"/> ${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty proximoPeriodo.inicioTrancamentoTurma}">
							<tr>
								<td>
									<b>
									<ufrn:format type="data" valor="${proximoPeriodo.inicioTrancamentoTurma}"/></b><br clear="all"/>
									&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoTrancamento}"/> ${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty proximoPeriodo.fimTrancamentoTurma}">
							<tr>
								<td>
									<b><ufrn:format type="data" valor="${proximoPeriodo.fimTrancamentoTurma}"/> </b><br clear="all"/>
									&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoTrancamento }"/> ${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty proximoPeriodo.fimPeriodoLetivo}">
							<tr>
							<td>
							<b>	<ufrn:format type="data" valor="${proximoPeriodo.fimPeriodoLetivo}"/></b><br clear="all"/>
									&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoLetivo }"/> ${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
							</td>
							</tr>
						</c:if>
						</tbody>
						</table>

					</c:if>	
						
					<c:set var="_outrosEventos" value="${portalPublicoPrograma.calendarioOutrosEventos}" />
					<c:if test="${not empty _outrosEventos}">
						<div id="listagem_tabela">
							<div id="group_lt">
								<h:outputText value="#{idioma.outrosEventos}"/>
							</div>	
							<table id="table_lt">
								<tbody>
									<c:forEach items="${_outrosEventos}" var="_outroEvento" >
										<tr>
											<td>
												<b>
													<ufrn:format type="data" valor="${_outroEvento.inicio}"/> 
													<c:if test="${_outroEvento.inicio != _outroEvento.fim}">
													à 
													<ufrn:format type="data" valor="${_outroEvento.fim}"/>
													</c:if>
												</b><br clear="all"/>
												&rarr;&nbsp; ${_outroEvento.descricao}
											</td>
										</tr>
									</c:forEach>	
								</tbody>
							</table>
						</div>	
					</c:if>
		
		<%--  FIM CONTEÚDO  --%>	
</div>

</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>