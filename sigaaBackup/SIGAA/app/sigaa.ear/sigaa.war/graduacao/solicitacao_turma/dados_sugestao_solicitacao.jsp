<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/graduacao/solicitacao_matricula.js"></script>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<h2 class="title">Solicitação de Abertura de Turma > Dados da Solicitação</h2>
	<h:outputText value="#{sugestaoSolicitacaoTurma.create}"/>

	<h:form id="sugestaoTurma">
	<div class="descricaoOperacao">
		
		<p>Caro Chefe de Departamento,</p>
		<p>Utilize este procedimento para sugerir uma turma ao curso selecionado. Selecione o horário para esta turma, 
		o qual tem que ser compatível com o plano de matrícula de todos os alunos que irão cursar a ${sugestaoSolicitacaoTurma.obj.tipoString}. 
		</p>
		
		<c:if test="${sugestaoSolicitacaoTurma.obj.componenteCurricular.bloco}">
			<p>Este componente é de bloco. Neste passo deve ser informado o horário todas as sub-unidades do tipo disciplina deste bloco. </p>  
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual || sugestaoSolicitacaoTurma.obj.turmaFerias }">
			<p> Esta solicitação é de uma turma de ${sugestaoSolicitacaoTurma.obj.tipoString}, você deve selecionar os discentes que deseja que curse esta turma.
			<c:if test="${ sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual }"> 
			Pode ser no máximo quatro discentes.
			</c:if>
			</p>
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual }">
			<p>Na opção <b><h:graphicImage value="/img/view.gif"style="overflow: visible;"/> Consultar Solicitações de Matrícula</b> você 
				ver o plano de matrícula do aluno no período corrente. Utilize estas informações para saber qual horário da turma solicitar 
				de acordo com os horários disponíveis dos alunos que irão cursar a turma de ensino individual.</p>
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.chefeDepartamento }">
			<p>Neste passo devem ser informados o curso que receberá a sugestão de turma para o próximo semestre e 
			a observação de sugestão, que será visualizado pelo coordenador de curso.</p>
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.obj.componenteCurricular.aceitaSubturma }">
			<p><b>Atenção! Este é um componente curricular que permite a criação de subturmas. 
			Porém, para que isso aconteça, é necessário que as turmas que serão agrupadas tenham algum horário compartilhado.</b></p>
		</c:if>
		
		</div>
		
		<table class="visualizacao">
			<tr>
				<th width="30%"> Componente Curricular: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
			</tr>
			<tr>
				<th> Código: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.codigo } </td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.tipoComponente.descricao } </td>
			</tr>
			<c:if test="${sugestaoSolicitacaoTurma.obj.componenteCurricular.bloco}">
			<tr>
				<th> Subunidades: </th>
				<td>
					<c:forEach items="${sugestaoSolicitacaoTurma.obj.componenteCurricular.subUnidades}" var="subunidade">
						${subunidade} - ${subunidade.chTotal}h  <br/>						
					</c:forEach> 
				</td>
			</tr>
			</c:if>
			<tr>
				<th> Total de Créditos: </th>
				<td> 
					${sugestaoSolicitacaoTurma.obj.componenteCurricular.crTotal} Total - ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.crAula} Teóricos ) / ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.crLaboratorio} Práticos ) 
				</td>
			</tr>
			<tr>
				<th> Carga Horária Total: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.chTotal} Total - ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.chAula} Teóricos ) / ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.chLaboratorio} Práticos )</td>
			</tr>
			<tr>
				<th> Ano-Período: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.ano}-${sugestaoSolicitacaoTurma.obj.periodo}</td>
			</tr>
			<tr>
				<th> Tipo da Turma: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.tipoString} </td>
			</tr>
			
			<c:if test="${sugestaoSolicitacaoTurma.alterarHorario and not empty sugestaoSolicitacaoTurma.obj.observacoes }">
			<tr>
				<th> Solicitação de Alteração do Chefe: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.observacoes} </td>
			</tr>
			</c:if>
			
		</table>
		<table class="formulario" width="100%">
		<caption class="formulario">Dados da Solicitação</caption>
			<tr>
				<td>
				<table class="subformulario" width="100%">
				
				
				<c:if test="${sugestaoSolicitacaoTurma.chefeDepartamento}">
					<caption>Curso para Sugestão de Turma</caption>
					<tbody>
						<tr>
							<th class="required">Curso:</th>
							<td>
								<h:selectOneMenu id="curso" value="#{sugestaoSolicitacaoTurma.obj.curso.id}" >
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{cursoGrad.allCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th>Observação:</th>
							<td>
								<h:inputTextarea id="txtObservacaoSugestao" rows="4" cols="80" value="#{ sugestaoSolicitacaoTurma.obj.observacaoSugestao }"/>
							</td>
						</tr>
					</tbody>	
				</c:if>
				
				<c:if test="${ !( sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual || sugestaoSolicitacaoTurma.obj.turmaFerias ) && !sugestaoSolicitacaoTurma.chefeDepartamento}">				
				<caption>Reservas</caption>
				<c:if test="${not empty sugestaoSolicitacaoTurma.obj.reservas}">
				<tr>
					<td colspan="2">
					<t:dataTable id="dataTableReserva" value="#{sugestaoSolicitacaoTurma.obj.reservas}" var="reserva" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">

						<t:column>
							<f:facet name="header">
								<f:verbatim>Curso</f:verbatim>
							</f:facet>
							<h:outputText value="#{reserva.matrizCurricular.curso.descricao}" rendered="#{not empty reserva.matrizCurricular}"/>
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
							<h:outputText value="#{reserva.matrizCurricular.habilitacao.nome}" rendered="#{not empty reserva.matrizCurricular.habilitacao}"/>
							<h:outputText value="#{reserva.matrizCurricular.enfase.nome}" rendered="#{not empty reserva.matrizCurricular.enfase}"/>
						</t:column>

						<t:column width="10%" styleClass="rightAlign">
							<f:facet name="header">
								<f:verbatim>Vagas</f:verbatim>
							</f:facet>
							<h:inputText value="#{reserva.vagasSolicitadas}" rendered="#{not empty reserva.matrizCurricular}" size="4"  onkeyup="formatarInteiro(this);" id="numeroVagasSolicitadas"/>
						</t:column>

					</t:dataTable>
					</td>
				</tr>
				</c:if>
				</c:if>
				</table>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Submeter Dados da Turma" action="#{sugestaoSolicitacaoTurma.submeterDados}" id="btAvancar" />
					<h:commandButton value="<< Voltar" action="#{sugestaoSolicitacaoTurma.voltarComponente}" id="btVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{sugestaoSolicitacaoTurma.cancelar}" id="btCancelar"/>
				</td>
			</tr>
		</tfoot>

		</table>
	</h:form>
	<br/><%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>