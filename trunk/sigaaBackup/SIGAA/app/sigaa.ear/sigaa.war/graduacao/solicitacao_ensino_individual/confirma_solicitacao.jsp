<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Solicitação de Turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</h2>
	<div class="descricaoOperacao">
	<p>Caro Discente,</p>
	<P>Por favor, confirme com calma os dados abaixo de sua solicitação antes de prosseguir.</P>
	</div>
	<h:form>
	<table class="listagem" style="width: 100%">
		<caption>Solicitações de turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</caption>
		<tbody>
			<tr>
				<td colspan="2">
					<table class="listagem" style="width:100%">
						<caption>Dados Gerais do Componente Curricular</caption>
						<tr>
							<th width="30%">Código:</th>
							<td align="left"><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.codigo}" /></td>
						</tr>
						<tr>
							<th>Nome:</th>
							<td><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.nome}" /></td>
						</tr>
						<tr>
							<th>Tipo do Componente Curricular:</th>
							<td><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.tipoComponente.descricao}" /></td>
						</tr>
						
						<c:if test="${solicitacaoEnsinoIndividual.obj.componente.atividade}">
							<tr>
								<th>Tipo de Atividade:</th>
								<td><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.tipoAtividade.descricao}" /></td>
							</tr>
						</c:if>			
						<c:if test="${not empty solicitacaoEnsinoIndividual.obj.componente.programa}">
							<tr>
								<th> Programa: </th>
								<td>
									<h:form target="_blank">
										
										<input type="hidden" value="${solicitacaoEnsinoIndividual.obj.componente.id}" name="idComponente" />
										<h:commandButton alt="Ver Componente Curricular" image="/img/report.png" 
										 	style="background: white; border: none;" 
											title="Ver Componente Curricular"
											action="#{programaComponente.gerarRelatorioPrograma}" />
										
										<h:commandButton alt="Ver Componente Curricular" 
										 	style="background: #F8F8FF; border: none; color: #0000CD" 
										 	value="Consultar Programa do Componente"
											title="Ver Componente Curricular"
											action="#{programaComponente.gerarRelatorioPrograma}" />
			
									</h:form>
								</td>
						</c:if>			
						
						<tr>
							<td class="subFormulario" colspan="2" style="text-align: center;">Carga Horária</td>
						</tr>
						
						<c:if test="${solicitacaoEnsinoIndividual.obj.componente.disciplina or solicitacaoEnsinoIndividual.obj.componente.moduloOuAtividadeColetiva}">
						<tr>
							<th>Créditos Teóricos:</th>
							<td>
								<h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.crAula}" /> crs.
								(${solicitacaoEnsinoIndividual.obj.componente.detalhes.chAula} horas)
							</td>
						</tr>
						<tr>
							<th>Créditos Práticos:</th>
							<td>
								<h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.crLaboratorio}" /> crs. 
								(${solicitacaoEnsinoIndividual.obj.componente.detalhes.chLaboratorio} horas)
							</td>
						</tr>
						</c:if>
						<c:if test="${solicitacaoEnsinoIndividual.obj.componente.atividade}">
							<tr>
								<th>Carga Horária Teórica:</th>
								<td><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.chAula}" /> h.</td>
							</tr>
							<tr>
								<th>Carga Horária Prática:</th>
								<td><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.chLaboratorio}" /> h.</td>
							</tr>
						</c:if>
						<c:if test="${solicitacaoEnsinoIndividual.obj.componente.bloco}">
							<tr>
								<th>Carga Horária Total:</th>
								<td><h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.chAula}" /> h.</td>
							</tr>
						</c:if>
						
						<tr>
							<td class="subFormulario" colspan="2" style="text-align: center;">Pré-requisitos, Co-Requisitos e Equivalências</td>
						</tr>
						<tr>
							<th>Pré-Requisitos:</th>
							<td>
								<h:outputText value="#{componenteCurricular.preRequisitoForm}" escape="false"/>
							</td>
						</tr>
						<tr>
							<th>Co-Requisitos:</th>
							<td>
								<h:outputText value="#{componenteCurricular.coRequisitoForm}" escape="false"/>
							</td>
						</tr>
						<tr>
							<th>Equivalências:</th>
							<td>
								<h:outputText value="#{componenteCurricular.equivalenciaForm}" escape="false"/>
							</td>
						</tr>
			
						
						<!-- Dados do bloco -->
						<c:if test="${solicitacaoEnsinoIndividual.obj.componente.bloco }">
							<tr>
								<td colspan="2">
								<table class="subFormulario" width="100%">
									<caption>Sub-unidades do Bloco</caption>
									<c:forEach items="${solicitacaoEnsinoIndividual.obj.componente.subUnidades}" var="unid">
										<tr>
											<td>${unid.nome}</td>
										</tr>
									</c:forEach>
								</table>
								</td>
							</tr>
						</c:if>
						
						
						<c:if test="${not solicitacaoEnsinoIndividual.obj.componente.bloco }">
							<tr>
								<td colspan="2" class="subFormulario" style="text-align: center;"> Ementa/Descrição </td>
							</tr>
							<tr>
								<td colspan="2">
									<p style="padding: 5px; line-height: 1.2em;">
										<h:outputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.ementa}" converter="convertTexto" 
												rendered="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.ementa != null}"/> 
										<h:outputText value="Ementa não cadastrada" rendered="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.ementa == null}"/>
									</p>
								</td>
							</tr>
						</c:if>			
					</table>
				</td>
			</tr>
			<c:if test="${not empty solicitacaoEnsinoIndividual.obj.sugestaoHorario}">
				<tr>	
				<th class="rotulo">Sugestão de Horário:</th>
				<td>${solicitacaoEnsinoIndividual.obj.sugestaoHorario}</td>
				</tr>
			</c:if>
			<tr>
				<th class="rotulo" width="35%">Data Solicitação:</th>
				<td><ufrn:format type="data" valor="${solicitacaoEnsinoIndividual.obj.dataSolicitacao}" /></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center">
					<h:commandButton action="#{solicitacaoEnsinoIndividual.submeterSolicitacao}" value="Confirmar Solicitação" id="comprovante" />
					<h:commandButton action="#{solicitacaoEnsinoIndividual.formBusca}" value="<< Voltar" id="voltar" />
					<h:commandButton action="#{solicitacaoEnsinoIndividual.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
				</td>
		</tfoot>
	</table>
	</h:form>
	<br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>