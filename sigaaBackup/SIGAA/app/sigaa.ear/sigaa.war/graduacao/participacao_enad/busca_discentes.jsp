<%@page import="br.ufrn.academico.dominio.StatusDiscente"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<c:set var="graduando" value="<%=StatusDiscente.GRADUANDO %>"/>
		<a4j:keepAlive beanName="participacaoDiscenteEnade"></a4j:keepAlive>
		<h2>
			<ufrn:subSistema /> > Cadastro de Participação no ENADE em Lote 
			<h:outputText value="de Curso" rendered="#{ participacaoDiscenteEnade.loteCurso }"/>
		</h2>
		
		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p>Utilize o formulário abaixo para buscar por discentes.</p>
		</div>
		
		<table class="formulario" width="90%">
			<caption> Parâmetros da Busca </caption>
			<tbody>
			
			<c:choose>
				<%-- CADASTRO EM PARTICIPAÇÃO INDIVIDUAL --%>
				<c:when test="${!participacaoDiscenteEnade.loteCurso}">
					<tr>
						<th class="required">Tipo do ENADE:</th>
						<td>
							<h:selectOneMenu value="#{participacaoDiscenteEnade.tipoEnade}" 
								id="tipoEnade1" onchange="submit()" onselect="submit()">
								<f:selectItems value="#{participacaoEnade.tipoEnadeCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="required"> Curso: </th>
						<td>
							<h:selectOneMenu value="#{participacaoDiscenteEnade.curso.id}" id="curso">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<c:if test="${participacaoDiscenteEnade.tipoEnadeIngressante}">
						<tr>
							<th class="obrigatorio">Ano de Ingresso:</th>
							<td>
								<h:inputText value="#{participacaoDiscenteEnade.ano}" size="4" 
									maxlength="4" id="ano1" onkeyup="return formatarInteiro(this);" 
									converter="#{ intConverter }"/>
							</td>
						</tr>
					</c:if>
					<c:if test="${!participacaoDiscenteEnade.tipoEnadeIngressante}">
						<tr>
							<th>Status do Discente:</th>
							<td>
								<h:selectOneMenu value="#{participacaoDiscenteEnade.obj.status}" 
									id="idStatusDiscente1" onchange="submit()" immediate="false">
									<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
									<f:selectItems value="#{participacaoDiscenteEnade.statusDiscenteCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						<c:if test="${!participacaoDiscenteEnade.obj.graduando}">
							<tr>
								<th class="obrigatorio">Percentual Mínimo Concluído:</th>
								<td>
									<h:inputText value="#{participacaoDiscenteEnade.percentualConcluido}"
										 size="3" maxlength="3" id="percentualConcluido1" onkeyup="return formatarInteiro(this);"
										  converter="#{ intConverter }"/>
									<ufrn:help>
											Serão listados discentes que tenham concluído, da carga horária do currículo, 
											no mínimo o valor percentual aqui informado. Para discentes GRADUANDO não é necessário informar este percentual,
									 		uma vez que possuem 100% do currículo concluído.
									 </ufrn:help>
								</td>
							</tr>
						</c:if>
					</c:if>
				</c:when>
				
				<%-- CADASTRO EM PARTICIPAÇÃO EM LOTE --%>
				<c:otherwise>
					
					<tr>
						<th width="20%">
							<h:selectBooleanCheckbox value="#{ participacaoDiscenteEnade.usarCalendarioEnade }" 
							id="usarCalendarioEnade" onchange="submit()"/>
						</th>
						<td>
							Buscar por cursos cadastrados no calendário ENADE. 
						</td>
					</tr>
					
					<c:choose>
						
						<%-- UTILIZANDO O CALENDÁRIO DO ENADE --%>
						<c:when test="${participacaoDiscenteEnade.usarCalendarioEnade}">
							<tr>
								<th width="20%">Calendário ENADE:</th>
								<td>
									<h:selectOneMenu value="#{participacaoDiscenteEnade.calendarioEnade.id}" 
										id="calendarioEnade" onchange="submit()" immediate="false" 
										valueChangeListener="#{participacaoDiscenteEnade.calendarioEnadeListener}">
										<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
										<f:selectItems value="#{calendarioEnadeMBean.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<c:if test="${not empty participacaoDiscenteEnade.calendarioEnade.cursosGrauAcademico}">
								<tr>
									<th class="rotulo" style="vertical-align: top;">Cursos:</th>
									<td>
										<c:forEach items="#{participacaoDiscenteEnade.calendarioEnade.cursosGrauAcademico}"
											 var="itemCurso"varStatus="loopCurso" >
											<c:if test="${ loopCurso.index > 0 }">
												<h:outputText value=", "/>
											</c:if>
											<h:outputText value="#{ itemCurso.curso.descricao }"/> - 
											<h:outputText value="#{ itemCurso.grauAcademico.descricao }"/> 
											( <h:outputText value="#{ itemCurso.curso.municipio }" /> )
										</c:forEach>
									</td>
								</tr>
								<tr>
									<th><h:selectBooleanCheckbox value="#{ participacaoDiscenteEnade.outrosCursos }"/> </th>
									<td>
										Buscar por outros cursos que não os listados no Calendário ENADE (cursos listados acima).
									</td>
								</tr>
							</c:if>
							
							<c:if test="${!participacaoDiscenteEnade.calendarioEnade.tipoEnade.ingressante}">
								<tr>
									<th>Status do Discente:</th>
									<td>
										<h:selectOneMenu value="#{participacaoDiscenteEnade.obj.status}" 
											id="idStatusDiscente3" onchange="submit()" immediate="false">
											<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
											<f:selectItems value="#{participacaoDiscenteEnade.statusDiscenteCombo}" />
										</h:selectOneMenu>
									</td>
								</tr>
								<c:if test="${!participacaoDiscenteEnade.obj.graduando}">
									<tr>
										<th class="obrigatorio">Percentual Mínimo Concluído:</th>
										<td>
											<h:inputText value="#{participacaoDiscenteEnade.percentualConcluido}" 
								
										size="3" maxlength="3" id="percentualConcluido3" 
												onkeyup="return formatarInteiro(this);" 
												converter="#{ intConverter }"/>
											<ufrn:help>
													Serão listados discentes que tenham concluído, da carga horária do currículo, 
													no mínimo o valor percentual aqui informado. Para discentes GRADUANDO não é necessário 
													informar este percentual, uma vez que possuem 100% do currículo concluído.
											</ufrn:help>
										</td>
									</tr>
								</c:if>
							</c:if>
						</c:when>
						
						<%-- (NÃO) UTILIZANDO O CALENDÁRIO DO ENADE --%>
						<c:otherwise>
							<tr>
								<th class="required">Tipo do ENADE:</th>
								<td>
									<h:selectOneMenu value="#{participacaoDiscenteEnade.tipoEnade}" id="tipoEnade2" onchange="submit()">
										<f:selectItems value="#{participacaoEnade.tipoEnadeCombo}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							
							<c:choose>
							
								<c:when test="${participacaoDiscenteEnade.tipoEnadeIngressante}">
									<tr>
										<th class="obrigatorio">Ano de Ingresso:</th>
										<td>
											<h:inputText value="#{participacaoDiscenteEnade.ano}" size="4" maxlength="4" 
												id="ano2" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
										</td>
									</tr>
									<tr>
									<th>Período de Ingresso:</th>
										<td>
											<h:inputText value="#{participacaoDiscenteEnade.periodo}" size="1" maxlength="1" 
												id="periodo2" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
										</td>
									</tr>									
								</c:when>
								
								<c:otherwise>
									<tr>
										<th>Status do Discente:</th>
										<td>
											<h:selectOneMenu value="#{participacaoDiscenteEnade.obj.status}" 
												id="idStatusDiscente2" onchange="submit()" immediate="false">
												<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
												<f:selectItems value="#{participacaoDiscenteEnade.statusDiscenteCombo}" />
											</h:selectOneMenu>
										</td>
									</tr>
									<c:if test="${!participacaoDiscenteEnade.obj.graduando}">
										<tr>
											<th class="obrigatorio">Percentual Mínimo Concluído:</th>
											<td>
												<h:inputText value="#{participacaoDiscenteEnade.percentualConcluido}" 
													size="3" maxlength="3" id="percentualConcluido2" 
													onkeyup="return formatarInteiro(this);"	converter="#{ intConverter }"/>
												<ufrn:help>
													Serão listados discentes que tenham concluído, da carga horária do currículo, 
													no mínimo o valor percentual aqui informado. Para discentes GRADUANDO não é 
													necessário informar este percentual, uma vez que possuem 100% do currículo concluído.
												</ufrn:help>
											</td>
										</tr>
									</c:if>
								</c:otherwise>
								
							</c:choose>	
							
						</c:otherwise>
					
					</c:choose>
					
				</c:otherwise>
			</c:choose>
				
			</tbody>
		</table>
		
		<table class="formulario" width="90%">
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{participacaoDiscenteEnade.buscar}"
							 id="btnCadastrar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" 
							action="#{participacaoDiscenteEnade.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>