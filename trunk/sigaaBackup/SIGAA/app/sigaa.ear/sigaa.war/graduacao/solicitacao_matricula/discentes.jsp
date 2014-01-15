<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<c:choose>
		<c:when test="${acesso.coordenadorCursoStricto && analiseSolicitacaoMatricula.portalCoordenadorStricto }">
			<%@include file="/stricto/menu_coordenador.jsp" %>
		</c:when>
		<c:when test="${acesso.orientadorStricto && portalDocente.portalDocente }">
			<%@include file="/portais/docente/menu_docente.jsp" %>
		</c:when>
	</c:choose>

<h2> <ufrn:subSistema /> > An�lise de Solicita��es de Matr�cula</h2>

	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Analisar Matr�culas
		<c:if test="${(analiseSolicitacaoMatricula.portalGraduacao || analiseSolicitacaoMatricula.portalCoordenadorGraduacao || analiseSolicitacaoMatricula.portalDocente) && !analiseSolicitacaoMatricula.alunoEspecial}">
			<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />: Integraliza��es do Curr�culo
		</c:if>
	</div>
	<br>
	<h:form>
	
	<table class="formulario" width="100%">
		<caption>Ordenar Busca</caption>
		<tbody>
			<tr>
				<td>
					<table class="subFormulario" width="100%">
						<tbody>
							<tr>
								<td colspan="2" align="center">
									<strong>Ordenar Por:</strong> &nbsp;
									<h:selectOneRadio value="#{analiseSolicitacaoMatricula.orderByNome}">
										<f:selectItem itemLabel="Nome" itemValue="true" />
										<f:selectItem itemLabel="Matr�cula" itemValue="false" />
									</h:selectOneRadio>	
									<c:if test="${acesso.dae}">
									<h:selectOneMenu style="width: 30%;" value="#{analiseSolicitacaoMatricula.filtroAlunoEspecial}" onchange="submit()" 
										valueChangeListener="#{analiseSolicitacaoMatricula.selecionaCategoriaAlunoEspecial}">
										<f:selectItem itemValue="0" itemLabel=" --- TODOS --- " />
										<f:selectItems value="#{categoriaDiscenteEspecial.allCombo}" />
									</h:selectOneMenu>
									</c:if>									
								</td>
							</tr>						
						</tbody>
						<tfoot>
							<tr>
								<td colspan="2">
									<h:commandButton actionListener="#{analiseSolicitacaoMatricula.ordenarSolicitacoes}" value="Ordenar" />
								</td>
							</tr>
						</tfoot>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table class="listagem" id="lista-turmas" width="100%">
						<caption>Selecione um dos discentes abaixo para analisar suas matr�culas (${fn:length(analiseSolicitacaoMatricula.discentesPendentes) + fn:length(analiseSolicitacaoMatricula.discentes)})</caption>
						<tbody>
						
							<%-- Pendentes --%>
							<c:if test="${not empty analiseSolicitacaoMatricula.discentesPendentes}">
							<tr>
								<td colspan="3" class="subFormulario"> Solicita��es pendentes de an�lise (${fn:length(analiseSolicitacaoMatricula.discentesPendentes)}) <ufrn:help>Enquanto todas as matr�culas do discente n�o forem analisadas, a solicita��o de matr�cula vai permanecer como pendente de an�lise.</ufrn:help></td> 
							</tr>
							<c:forEach items="#{analiseSolicitacaoMatricula.discentesPendentes}" var="d" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${d.matriculaNome} </td>
									<td  width="1%">
										<h:commandLink title="Integraliza��es do Curr�culo" id="btaoIntCurriculoPendentes"
											action="#{relatorioIntegralizacaoCurriculoMBean.selecionaDiscente}" 
											rendered="#{(analiseSolicitacaoMatricula.portalGraduacao || analiseSolicitacaoMatricula.portalCoordenadorGraduacao || analiseSolicitacaoMatricula.portalDocente) && !analiseSolicitacaoMatricula.alunoEspecial}">
											<f:param name="idDiscente" value="#{d.id}" />
											<h:graphicImage url="/img/listar.gif"/>
										</h:commandLink>
									</td>		
									<td width="1%">
										<h:commandLink action="#{analiseSolicitacaoMatricula.selecionaDiscente}" title="Analisar Matr�cula" >
											<f:param name="id" value="#{d.id}" />
										 	<h:graphicImage url="/img/seta.gif"/>
										</h:commandLink>
									</td>
								</tr>
							</c:forEach>
							</c:if>		
				
							<%-- Outras --%>
							<c:if test="${not empty analiseSolicitacaoMatricula.discentes}">
							<tr>
								<td colspan="3" class="subFormulario"> Solicita��es analisadas (${fn:length(analiseSolicitacaoMatricula.discentes)})</td> 
							</tr>
							<c:forEach items="#{analiseSolicitacaoMatricula.discentes}" var="d" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${d.matriculaNome} </td>
									<td  width="1%">
										<h:commandLink title="Integraliza��es do Curr�culo" id="btaoIntCurriculoDiscentes"
											action="#{relatorioIntegralizacaoCurriculoMBean.selecionaDiscente}"
											rendered="#{(analiseSolicitacaoMatricula.portalGraduacao || analiseSolicitacaoMatricula.portalCoordenadorGraduacao || analiseSolicitacaoMatricula.portalDocente) && !analiseSolicitacaoMatricula.alunoEspecial}">
											<f:param name="idDiscente" value="#{d.id}" />
											<h:graphicImage url="/img/listar.gif"/>
										</h:commandLink>
									</td>		
									<td width="1%">
										<h:commandLink action="#{analiseSolicitacaoMatricula.selecionaDiscente}" title="Analisar Matr�cula" >
											<f:param name="id" value="#{d.id}" />
										 	<h:graphicImage url="/img/seta.gif"/>
										</h:commandLink>
									</td>
								</tr>
							</c:forEach>
							</c:if>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="3" align="center">
									<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{analiseSolicitacaoMatricula.cancelar}" />	
								</td>
							</tr>
						</tfoot>
					</table>
				</td>
			</tr>
		</tbody>
	</table>

	<br />
	
	</h:form>

	<c:if test="${empty analiseSolicitacaoMatricula.discentes and empty analiseSolicitacaoMatricula.discentesPendentes}">
		<center>
		<i>
		N�o h� nenhuma solicita��o de matr�cula pendente de an�lise.
		</i>
		<br><br>
		<h:form>
			<h:commandButton value="Voltar"  action="#{analiseSolicitacaoMatricula.cancelar}" />
		</h:form>
		</center>
	</c:if>

	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
