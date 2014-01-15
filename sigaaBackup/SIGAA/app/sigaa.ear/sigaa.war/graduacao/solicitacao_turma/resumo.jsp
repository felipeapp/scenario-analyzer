<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2 class="title">Solicitação de Abertura de Turma > Resumo</h2>
	<h:outputText value="#{solicitacaoTurma.create}"/>
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
		Confira os dados da solicitação e clique em Cadastrar Solicitação de Turma caso
		os dados estejam corretos.
		</div>

	<table class="formulario" width="100%">
		<caption>Resumo da Solicitação</caption>


		<tr><td>
			<table width="100%">
				<caption>Dados Gerais</caption>

				<tr>
					<th width="20%" class="rotulo"> Componente Curricular: </th>
					<td> ${solicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
				</tr>
				<tr>
					<th class="rotulo"> Código: </th>
					<td> ${solicitacaoTurma.obj.componenteCurricular.codigo } </td>
				</tr>
				<tr>
					<th class="rotulo"> Tipo: </th>
					<td> ${solicitacaoTurma.obj.componenteCurricular.tipoComponente.descricao } </td>
				</tr>
				<c:if test="${solicitacaoTurma.obj.componenteCurricular.bloco}">
				<tr>
					<th  class="rotulo"> Subunidades: </th>
					<td>
						<c:forEach items="${solicitacaoTurma.obj.componenteCurricular.subUnidades}" var="subunidade">
							${subunidade} - ${subunidade.chTotal}h  <br/>						
						</c:forEach> 
					</td>
				</tr>
				</c:if>
				<tr>
					<th  class="rotulo"> Carga Horária: </th>
					<td> ${solicitacaoTurma.obj.componenteCurricular.detalhes.chTotal } horas </td>
				</tr>
				<c:if test="${ not solicitacaoTurma.obj.turmaEnsinoIndividual }">
					<tr>
						<th  class="rotulo">Horário: </th>
						<td> ${solicitacaoTurma.obj.horario}</td>
					</tr>
				</c:if>
				<tr>
					<th  class="rotulo">Ano-Período: </th>
					<td> ${solicitacaoTurma.obj.ano}-${solicitacaoTurma.obj.periodo}</td>
				</tr>
				
			</table>
		</td></tr>

		<c:if test="${ solicitacaoTurma.possivelInformarQntVagasTurmaFerias }">
		<tr><td>
			<table width="100%">
				<caption>Matrizes Reservadas</caption>

				<tr>
					<td colspan="2">
						<t:dataTable id="dataTableReserva" value="#{solicitacaoTurma.reservasValidas}" var="reserva" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Curso</f:verbatim>
								</f:facet>
								<h:outputText value="#{reserva.matrizCurricular.curso.descricao}"/>
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Turno</f:verbatim>
								</f:facet>
								<h:outputText value="#{reserva.matrizCurricular.turno.descricao}"/>
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Modalidade</f:verbatim>
								</f:facet>
								<h:outputText value="#{reserva.matrizCurricular.grauAcademico.descricao}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Habilitação/Ênfase</f:verbatim>
								</f:facet>
								<h:outputText value="#{reserva.matrizCurricular.habilitacao.nome}"/>
								<h:outputText value="#{reserva.matrizCurricular.enfase.nome}" />
							</t:column>

							<t:column width="10%" style="text-align:center;">
								<f:facet name="header">
									<f:verbatim><div style="text-align:center;">Vagas</div></f:verbatim>
								</f:facet>
								<h:outputText value="#{reserva.vagasSolicitadas}" style="text-align:right;" />
							</t:column>
							
							<t:column width="10%" style="text-align:center;" rendered="false">
								<f:facet name="header">
									<f:verbatim><div style="text-align:center;">Vagas para Ingressantes</div></f:verbatim>
								</f:facet>
								<h:outputText value="#{reserva.vagasSolicitadasIngressantes}" style="text-align:right;" />
							</t:column>
						</t:dataTable>
						<h:outputText value="Não há reserva para matrizes curriculares" rendered="#{ empty solicitacaoTurma.reservasValidas }" style="color: red;"/>
					</td>
				</tr>

			</table>
		</td></tr>
		</c:if>
		
		<c:if test="${ not solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<tr>
				<td>
					<table width="100%">
					<caption>Discentes da Solicitação</caption>
						<c:choose>
							<c:when test="${ not empty solicitacaoTurma.obj.discentes  }">
								<c:forEach var="discente" items="#{solicitacaoTurma.obj.discentes}" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td>
											${discente.discenteGraduacao}				
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td style="color: red;"> Nenhum discente Solicitou essa turma. </td>
								</tr>
							</c:otherwise>
						</c:choose>
					</table>
				</td>
			</tr>
			
		</c:if>

		<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias }">
		<tr><td>
			<table width="100%">
				<caption>Discentes Interessados na Turma de 
				<h:outputText value="Férias" rendered="#{ solicitacaoTurma.obj.turmaFerias }" />
				<h:outputText value="Ensino Individual" rendered="#{ solicitacaoTurma.obj.turmaEnsinoIndividual }" /> 
				</caption>
				<c:choose>
					<c:when test="${ not empty solicitacaoTurma.obj.discentes  }">
						<c:forEach items="${solicitacaoTurma.obj.discentes}" var="discenteLoop" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td></td>
								<td>${discenteLoop.discenteGraduacao}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
							<tr>
								<td style="color: red;"> Não há nenhum Discente Interessado na Turma de 
									<h:outputText value="Férias" rendered="#{ solicitacaoTurma.obj.turmaFerias }" />
									<h:outputText value="Ensino Individual" rendered="#{ solicitacaoTurma.obj.turmaEnsinoIndividual }" />
								 </td>
							</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</td></tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:form>
							<h:commandButton value="#{solicitacaoTurma.confirmButton}" action="#{solicitacaoTurma.cadastrar}" id="btCadastrar" rendered="#{!sugestaoSolicitacaoTurma.chefeDepartamento}"/>
							<h:commandButton value="#{solicitacaoTurma.confirmButton}" action="#{sugestaoSolicitacaoTurma.cadastrar}" id="btCadastrarSugestao" rendered="#{sugestaoSolicitacaoTurma.chefeDepartamento}"/>
							<h:commandButton value="<< Voltar" action="#{solicitacaoTurma.listar}" id="btVoltarRemover" rendered="#{solicitacaoTurma.operacaoAtivaRemover}" />
							<h:commandButton value="<< Voltar" action="#{solicitacaoTurma.voltarResumo}" id="btVoltar" rendered="#{not solicitacaoTurma.operacaoAtivaRemover}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoTurma.cancelar}" id="btCancelar"/>
					</h:form>
				</td>
			</tr>
		</tfoot>

	</table>


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>