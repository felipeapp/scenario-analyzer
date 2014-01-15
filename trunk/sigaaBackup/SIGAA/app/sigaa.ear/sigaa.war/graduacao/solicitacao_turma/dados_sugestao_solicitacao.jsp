<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/graduacao/solicitacao_matricula.js"></script>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<h2 class="title">Solicita��o de Abertura de Turma > Dados da Solicita��o</h2>
	<h:outputText value="#{sugestaoSolicitacaoTurma.create}"/>

	<h:form id="sugestaoTurma">
	<div class="descricaoOperacao">
		
		<p>Caro Chefe de Departamento,</p>
		<p>Utilize este procedimento para sugerir uma turma ao curso selecionado. Selecione o hor�rio para esta turma, 
		o qual tem que ser compat�vel com o plano de matr�cula de todos os alunos que ir�o cursar a ${sugestaoSolicitacaoTurma.obj.tipoString}. 
		</p>
		
		<c:if test="${sugestaoSolicitacaoTurma.obj.componenteCurricular.bloco}">
			<p>Este componente � de bloco. Neste passo deve ser informado o hor�rio todas as sub-unidades do tipo disciplina deste bloco. </p>  
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual || sugestaoSolicitacaoTurma.obj.turmaFerias }">
			<p> Esta solicita��o � de uma turma de ${sugestaoSolicitacaoTurma.obj.tipoString}, voc� deve selecionar os discentes que deseja que curse esta turma.
			<c:if test="${ sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual }"> 
			Pode ser no m�ximo quatro discentes.
			</c:if>
			</p>
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.obj.turmaEnsinoIndividual }">
			<p>Na op��o <b><h:graphicImage value="/img/view.gif"style="overflow: visible;"/> Consultar Solicita��es de Matr�cula</b> voc� 
				ver o plano de matr�cula do aluno no per�odo corrente. Utilize estas informa��es para saber qual hor�rio da turma solicitar 
				de acordo com os hor�rios dispon�veis dos alunos que ir�o cursar a turma de ensino individual.</p>
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.chefeDepartamento }">
			<p>Neste passo devem ser informados o curso que receber� a sugest�o de turma para o pr�ximo semestre e 
			a observa��o de sugest�o, que ser� visualizado pelo coordenador de curso.</p>
		</c:if>
		
		<c:if test="${ sugestaoSolicitacaoTurma.obj.componenteCurricular.aceitaSubturma }">
			<p><b>Aten��o! Este � um componente curricular que permite a cria��o de subturmas. 
			Por�m, para que isso aconte�a, � necess�rio que as turmas que ser�o agrupadas tenham algum hor�rio compartilhado.</b></p>
		</c:if>
		
		</div>
		
		<table class="visualizacao">
			<tr>
				<th width="30%"> Componente Curricular: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
			</tr>
			<tr>
				<th> C�digo: </th>
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
				<th> Total de Cr�ditos: </th>
				<td> 
					${sugestaoSolicitacaoTurma.obj.componenteCurricular.crTotal} Total - ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.crAula} Te�ricos ) / ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.crLaboratorio} Pr�ticos ) 
				</td>
			</tr>
			<tr>
				<th> Carga Hor�ria Total: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.chTotal} Total - ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.chAula} Te�ricos ) / ( ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.chLaboratorio} Pr�ticos )</td>
			</tr>
			<tr>
				<th> Ano-Per�odo: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.ano}-${sugestaoSolicitacaoTurma.obj.periodo}</td>
			</tr>
			<tr>
				<th> Tipo da Turma: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.tipoString} </td>
			</tr>
			
			<c:if test="${sugestaoSolicitacaoTurma.alterarHorario and not empty sugestaoSolicitacaoTurma.obj.observacoes }">
			<tr>
				<th> Solicita��o de Altera��o do Chefe: </th>
				<td> ${sugestaoSolicitacaoTurma.obj.observacoes} </td>
			</tr>
			</c:if>
			
		</table>
		<table class="formulario" width="100%">
		<caption class="formulario">Dados da Solicita��o</caption>
			<tr>
				<td>
				<table class="subformulario" width="100%">
				
				
				<c:if test="${sugestaoSolicitacaoTurma.chefeDepartamento}">
					<caption>Curso para Sugest�o de Turma</caption>
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
							<th>Observa��o:</th>
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
								<f:verbatim>Habilita��o/�nfase</f:verbatim>
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