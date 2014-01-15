<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/graduacao/solicitacao_matricula.js"></script>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<h2 class="title">Solicita��o de Abertura de Turma > Dados da Solicita��o</h2>
	<h:outputText value="#{solicitacaoTurma.create}"/>

	<h:form id="solicitacaoTurma">
	<div class="descricaoOperacao">
		
		<p>Caro Coordenador,</p>
		<c:if test="${not solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<p>Selecione o hor�rio para esta turma. Observe que o chefe de departamento n�o poder� alterar o hor�rio selecionado
			por voc�, ent�o o hor�rio tem que ser compat�vel com o plano de matr�cula de todos os alunos que ir�o cursar a ${solicitacaoTurma.obj.tipoString}. 
			Enquanto o chefe do departamento da disciplina n�o criar a turma � poss�vel alterar o hor�rio e os alunos desta solicita��o.</p>
		</c:if>
		<p></p>
			
		<c:if test="${solicitacaoTurma.obj.componenteCurricular.bloco}">
			<p>Este componente � de bloco. Neste passo deve ser informado o hor�rio todas as sub-unidades do tipo disciplina deste bloco. </p>  
		</c:if>
		
		<c:if test="${ !solicitacaoTurma.modoDireto && ( solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias ) }">
			<p> Esta solicita��o � de uma turma de ${solicitacaoTurma.obj.tipoString}, voc� deve selecionar os discentes que deseja que curse esta turma.
			<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }"> 
			Pode ser no m�ximo quatro discentes.
			</c:if>
			</p>
		</c:if>
		
		<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">
			<p>Na op��o <b><h:graphicImage value="/img/view.gif"style="overflow: visible;"/> Consultar Solicita��es de Matr�cula</b> voc� 
				ver o plano de matr�cula do aluno no per�odo corrente. Utilize estas informa��es para saber qual hor�rio da turma solicitar 
				de acordo com os hor�rios dispon�veis dos alunos que ir�o cursar a turma de ensino individual.</p>
		</c:if>
		
		<c:if test="${ !(solicitacaoTurma.obj.turmaEnsinoIndividual || solicitacaoTurma.obj.turmaFerias) }">
			<p>Neste passo devem ser informados as vagas para cada Matriz Curricular (Turno/Modalidade/Habilita��o).
			Caso n�o deseje solicitar vagas para uma determinada Matriz, deixe o campo com zero.</p>
		</c:if>
		
		<c:if test="${ solicitacaoTurma.obj.componenteCurricular.aceitaSubturma }">
			<p><b>Aten��o! Este � um componente curricular que permite a cria��o de subturmas por�m, para que isso aconte�a � necess�rio que 
			as turmas que ser�o agrupadas tenham algum hor�rio compartilhado.</b></p>
		</c:if>
		
		<p hidden="true">As vagas de ingressantes s�o destinadas apenas aos alunos que ingressarem na Universidade no per�odo de destino da solicita��o de turma, onde os demais discentes n�o poder�o ocup�-las, 
		mesmo que ap�s o processamento das matr�culas estas vagas n�o tenham sido ocupadas, destinando-as aos alunos ingressantes de forma extraordin�ria. </p>
		<p hidden="true">As vagas para alunos ingressantes ser�o aceitas apenas nos casos, cujo o componente seja oferecido no primeiro per�odo de algum curr�culo da matriz curricular, que a vaga seja ofertada.</p>
		</div>
		
		<table class="visualizacao">
			<tr>
				<th width="30%"> Componente Curricular: </th>
				<td> ${solicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
			</tr>
			<tr>
				<th> C�digo: </th>
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
				<th> Total de Cr�ditos: </th>
				<td> 
					${solicitacaoTurma.obj.componenteCurricular.crTotal} Total - ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.crAula} Te�ricos ) / ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.crLaboratorio} Pr�ticos ) 
				</td>
			</tr>
			<tr>
				<th> Carga Hor�ria Total: </th>
				<td> ${solicitacaoTurma.obj.componenteCurricular.chTotal} Total - ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.chAula} Te�ricos ) / ( ${solicitacaoTurma.obj.componenteCurricular.detalhes.chLaboratorio} Pr�ticos )</td>
			</tr>
			<tr>
				<th> Ano-Per�odo: </th>
				<td> ${solicitacaoTurma.obj.ano}-${solicitacaoTurma.obj.periodo}</td>
			</tr>
			<tr>
				<th> Tipo da Turma: </th>
				<td> ${solicitacaoTurma.obj.tipoString} </td>
			</tr>
			
			<c:if test="${solicitacaoTurma.alterarHorario and not empty solicitacaoTurma.obj.observacoes }">
			<tr>
				<th> Solicita��o de Altera��o do Chefe: </th>
				<td> ${solicitacaoTurma.obj.observacoes} </td>
			</tr>
			</c:if>
			
		</table>
		<table class="formulario" width="100%">
		<caption class="formulario">Dados da Solicita��o</caption>
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
								<f:verbatim>Habilita��o/�nfase</f:verbatim>
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
							<ufrn:help img="/img/ajuda.gif">Marque os alunos que ir�o cursar nesta turma.</ufrn:help>
						</caption>
					
						<thead>
							<td></td>
							<td>Discente</td>
							<td>
								<c:if test="${ solicitacaoTurma.obj.turmaEnsinoIndividual }">Sugest�o de Hor�rio</c:if>
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
										<img src="/sigaa/img/view.gif" alt="" class="noborder" title="Ver Plano de Matr�cula"/> 
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
					<h:commandButton value="Submeter Dados da Solicita��o" action="#{solicitacaoTurma.submeterDados}" id="btAvancar" />
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