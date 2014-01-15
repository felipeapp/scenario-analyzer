<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/graduacao/solicitacao_matricula.js"></script>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<h2 class="title">Solicitação de Abertura de Turma > Dados da Solicitação</h2>
	<h:outputText value="#{solicitacaoTurma.create}"/>

	<h:form id="solicitacaoTurma">
	<div class="descricaoOperacao">
		
		<p>Caro Coordenador,</p>
		<c:if test="${not solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<p>Selecione o horário para esta turma. Observe que o chefe de departamento não poderá alterar o horário selecionado
			por você, então o horário tem que ser compatível com o plano de matrícula de todos os alunos que irão cursar a ${solicitacaoTurma.obj.tipoString}. 
			Enquanto o chefe do departamento da disciplina não criar a turma é possível alterar o horário e os alunos desta solicitação.</p>
		</c:if>
		<p></p>
			
		<c:if test="${solicitacaoTurma.obj.componenteCurricular.bloco}">
			<p>Este componente é de bloco. Neste passo deve ser informado o horário todas as sub-unidades do tipo disciplina deste bloco. </p>  
		</c:if>
		
		<c:if test="${ !solicitacaoTurma.modoDireto && ( solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias ) }">
			<p> Esta solicitação é de uma turma de ${solicitacaoTurma.obj.tipoString}, você deve selecionar os discentes que deseja que curse esta turma.
			<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }"> 
			Pode ser no máximo quatro discentes.
			</c:if>
			</p>
		</c:if>
		
		<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<p>Na opção <b><h:graphicImage value="/img/view.gif"style="overflow: visible;"/> Consultar Solicitações de Matrícula</b> você 
				ver o plano de matrícula do aluno no período corrente. Utilize estas informações para saber qual horário da turma solicitar 
				de acordo com os horários disponíveis dos alunos que irão cursar a turma de ensino individual.</p>
		</c:if>
		
		<c:if test="${ !(solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias) }">
			<p>Neste passo devem ser informados as vagas para cada Matriz Curricular (Turno/Modalidade/Habilitação).
			Caso não deseje solicitar vagas para uma determinada Matriz, deixe o campo com zero.</p>
		</c:if>
		
		<c:if test="${ solicitacaoTurma.obj.componenteCurricular.aceitaSubturma }">
			<p><b>Atenção! Este é um componente curricular que permite a criação de subturmas porém, para que isso aconteça é necessário que 
			as turmas que serão agrupadas tenham algum horário compartilhado.</b></p>
		</c:if>
		
		<p hidden="true">As vagas de ingressantes são destinadas apenas aos alunos que ingressarem na Universidade no período de destino da solicitação de turma, onde os demais discentes não poderão ocupá-las, 
		mesmo que após o processamento das matrículas estas vagas não tenham sido ocupadas, destinando-as aos alunos ingressantes de forma extraordinária. </p>
		<p hidden="true">As vagas para alunos ingressantes serão aceitas apenas nos casos, cujo o componente seja oferecido no primeiro período de algum currículo da matriz curricular, que a vaga seja ofertada.</p>
		</div>
		
		<table class="visualizacao">
			<tr>
				<th width="30%"> Componente Curricular: </th>
				<td> ${solicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
			</tr>
			<tr>
				<th> Código: </th>
				<td> ${solicitacaoTurma.obj.componenteCurricular.codigo } </td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${solicitacaoTurma.obj.componenteCurricular.tipoComponente.descricao } </td>
			</tr>
			<c:if test="${solicitacaoTurma.obj.componenteCurricular.bloco}">
			<tr>
				<th> Subunidades: </th>
				<td>
					<c:forEach items="${solicitacaoTurma.obj.componenteCurricular.subUnidades}" var="subunidade">
						${subunidade} - ${subunidade.chTotal}h  <br/>						
					</c:forEach> 
				</td>
			</tr>
			</c:if>
			<tr>
				<th> Total de Créditos: </th>
				<td> 
					${solicitacaoTurma.obj.componenteCurricular.crTotal} Total - ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.crAula} Teóricos ) / ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.crLaboratorio} Práticos ) 
				</td>
			</tr>
			<tr>
				<th> Carga Horária Total: </th>
				<td> ${solicitacaoTurma.obj.componenteCurricular.chTotal} Total - ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.chAula} Teóricos ) / ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.chLaboratorio} Práticos )</td>
			</tr>
			<tr>
				<th> Ano-Período: </th>
				<td> ${solicitacaoTurma.obj.ano}-${solicitacaoTurma.obj.periodo}</td>
			</tr>
			<tr>
				<th> Tipo da Turma: </th>
				<td> ${solicitacaoTurma.obj.tipoString} </td>
			</tr>
			
			<c:if test="${solicitacaoTurma.alterarHorario and not empty solicitacaoTurma.obj.observacoes }">
			<tr>
				<th> Solicitação de Alteração do Chefe: </th>
				<td> ${solicitacaoTurma.obj.observacoes} </td>
			</tr>
			</c:if>
			
		</table>
		<table class="formulario" width="100%">
		<caption class="formulario">Dados da Solicitação</caption>
			<tr>
				<td>
				<table class="subformulario" width="100%">
			
				<c:if test="${ !solicitacaoTurma.obj.turmaEnsinoIndividual }">								
				<caption>Reservas</caption>
				<c:if test="${not empty solicitacaoTurma.obj.reservas}">
				<tr>
					<td colspan="2">
					<t:dataTable id="dataTableReserva" value="#{solicitacaoTurma.obj.reservas}" var="reserva" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">

						<t:column>
							<f:facet name="header">
								<f:verbatim>Curso</f:verbatim>
							</f:facet>
							<h:outputText value="#{reserva.matrizCurricular.curso.descricao}" rendered="#{(not empty reserva.matrizCurricular)}" />
						</t:column>

						<t:column>
							<f:facet name="header">
								<f:verbatim>Turno</f:verbatim>
							</f:facet>
							<h:outputText value="#{reserva.matrizCurricular.turno.descricao}" rendered="#{not empty reserva.matrizCurricular}"/>
						</t:column>

						<t:column>
							<f:facet name="header">
								<f:verbatim>Modalidade</f:verbatim>
							</f:facet>
							<h:outputText value="#{reserva.matrizCurricular.grauAcademico.descricao}" rendered="#{not empty reserva.matrizCurricular}"/>
						</t:column>

						<t:column>
							<f:facet name="header">
								<f:verbatim>Habilitação/Ênfase</f:verbatim>
							</f:facet>
							<h:outputText value="#{reserva.matrizCurricular.habilitacao.nome}" rendered="#{not empty reserva.matrizCurricular.habilitacao }"/>
							<h:outputText value="#{reserva.matrizCurricular.enfase.nome}" rendered="#{not empty reserva.matrizCurricular.enfase }"/>
						</t:column>

						<t:column width="10%" style="text-align:center;">
							<f:facet name="header">
								<f:verbatim><div style="text-align:center;">Vagas</div></f:verbatim>
							</f:facet>
							<h:inputText value="#{reserva.vagasSolicitadas}" rendered="#{not empty reserva.matrizCurricular}" 
								size="4" onkeyup="formatarInteiro(this);" id="numeroVagasSolicitadas" style="text-align:right;" />
						</t:column>
						
						<t:column width="10%" style="text-align:center;" rendered="false">
							<f:facet name="header">
								<f:verbatim><div style="text-align:center;">Vagas para Ingressantes</div></f:verbatim>
							</f:facet>
							<h:inputText value="#{reserva.vagasSolicitadasIngressantes}" disabled="#{not reserva.possuiVagaIngressantes}" rendered="#{not empty reserva.matrizCurricular}" 
								size="4" onkeyup="formatarInteiro(this);" id="numeroVagasIngressantes" style="text-align:right;"/>
						</t:column>

					</t:dataTable>
					</td>
				</tr>
				</c:if>
				</c:if>

				<c:if test="${ !solicitacaoTurma.modoDireto && ( solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias ) }">
				
				<tr><td colspan="2">
					
					<table class="subformulario" width="100%">
						<caption>Discentes Solicitantes da ${solicitacaoTurma.obj.tipoString}
							<ufrn:help img="/img/ajuda.gif">Marque os alunos que irão cursar nesta turma.</ufrn:help>
						</caption>
					
						<thead>
							<td></td>
							<td>Discente</td>
							<td>
								<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">Sugestão de Horário</c:if>
							</td>
							<td width="2%"></td>
						</thead>
					
						<c:forEach var="solicitacao" items="${solicitacaoTurma.solicitacoesEnsinoIndividual}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td>
									<input type="checkbox" name="discentesSelecionados" value="${solicitacao.id}" id="${solicitacao.discente.id}" class="noborder" ${ empty solicitacao.solicitacaoTurma ? '' : 'checked="checked"' }>
								</td>
								<td> <label for="${solicitacao.discente.id}"> ${solicitacao.discente} </label></td>
								<td> 
									<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">${solicitacao.sugestaoHorario}</c:if> 
								</td>
								<td> 
									<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">
									<a href="javascript:void(0);" onclick="PainelSolicitacoesMatricula.show(${solicitacao.discente.id});">
										<img src="/sigaa/img/view.gif" alt="" class="noborder" title="Ver Plano de Matrícula"/> 
									</a>
									</c:if>
								</td>
							</tr>
						
						</c:forEach>
					
					</table>
				
				</td></tr>
				
				</c:if>
				
				</table>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Submeter Dados da Solicitação" action="#{solicitacaoTurma.submeterDados}" id="btAvancar" />
					<h:commandButton value="<< Voltar" action="#{solicitacaoTurma.telaBusca}" id="btVoltar" rendered="#{not acesso.coordenadorCursoGrad}"/>
					<h:commandButton value="<< Voltar" action="#{solicitacaoTurma.listar}" id="btVoltarCoord" rendered="#{acesso.coordenadorCursoGrad}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoTurma.cancelar}" id="btCancelar"/>
				</td>
			</tr>
		</tfoot>

		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>