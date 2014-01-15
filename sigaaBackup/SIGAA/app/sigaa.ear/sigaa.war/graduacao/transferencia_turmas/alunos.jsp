<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.dadosTurma th {
		background: #EEE;
		border-bottom: 1px solid #DDD;
		font-weight: bold;
	}
	tr.dadosTurma td {
		background: #EEE;
		border-bottom: 1px solid #DDD;
	}
	tr.totalAlunos td {
		text-align: center;
	}
	tr.totalAlunos td input {
		color: #292;
		font-weight: bold;
		font-size: 1.3em;
		padding: 3px;
		text-align: center;
	}
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurma.descricaoTipo}  &gt; Definir Alunos</h2>
	<h:form id="alunos">

	<table class="formulario" width="80%">
		<caption class="formulario">Defina os discentes a serem transferidos</caption>
		<tbody>
		<tr>
			<td colspan="8" class="subFormulario">
				Turma de Origem
			</td>
		</tr>
		<tr>
			<th class="rotulo">Turma:</th>
			<td colspan="6">${transferenciaTurma.turmaOrigem.disciplina.descricaoResumida } - Turma ${transferenciaTurma.turmaOrigem.codigo}</td>
			<td><b>Período:</b> ${transferenciaTurma.turmaOrigem.ano}.${transferenciaTurma.turmaOrigem.periodo} </td>
		</tr>
		<tr>
			<th class="rotulo">Docente(s):</th>
			<td colspan="7">${transferenciaTurma.turmaOrigem.docentesNomes}</td>
		</tr>
		<tr class="dadosTurma">
			<th>Horário:</th>
			<td>${transferenciaTurma.turmaOrigem.descricaoHorario}</td>
			<th>Matriculados:</th>
			<td>${transferenciaTurma.turmaOrigem.qtdMatriculados}</td>
			<th>Solicitações:</th>
			<td>${transferenciaTurma.turmaOrigem.qtdEspera}</td>
			<th>Capacidade:</th>
			<td>${transferenciaTurma.turmaOrigem.capacidadeAluno}</td>
		</tr>
		
		<tr>
			<td colspan="8" class="subFormulario">
				Turma de Destino
			</td>
		</tr>
		<tr>
			<th class="rotulo">Turma:</th>
			<td colspan="6">${transferenciaTurma.turmaDestino.disciplina.descricaoResumida } - Turma ${transferenciaTurma.turmaDestino.codigo}</td>
			</td>
			<td><b>Período:</b> ${transferenciaTurma.turmaDestino.ano}.${transferenciaTurma.turmaDestino.periodo} </td>
		</tr>
		<tr>
			<th class="rotulo">Docente(s):</th>
			<td colspan="7">${transferenciaTurma.turmaDestino.docentesNomes}</td>
		</tr>
		<tr class="dadosTurma">
			<th>Horário:</th>
			<td>${transferenciaTurma.turmaDestino.descricaoHorario}</td>
			<th>Matriculados:</th>
			<td>${transferenciaTurma.turmaDestino.qtdMatriculados}	</td>
			<th>Solicitações:</th>
			<td>${transferenciaTurma.turmaDestino.qtdEspera}</td>
			<th>Capacidade:</th>
			<td>${transferenciaTurma.turmaDestino.capacidadeAluno}	</td>
		</tr>
		
		<c:if test="${ transferenciaTurma.automatica }">
			<tr>
				<td colspan="8" class="subFormulario">
					Alunos a transferir
				</td>
			</tr>		
			<c:if test="${transferenciaTurma.turmaOrigem.qtdEspera > 0}">
			<tr class="totalAlunos">
				<td colspan="4" style="text-align:right"> 
					Número de solicitações a transferir:
					<span class="required" style="vertical-align: top;">&nbsp;</span>
				</td>
				<td>
					<h:inputText value="#{ transferenciaTurma.qtdSolicitacoes }" size="3" maxlength="3"  onkeypress="return ApenasNumeros(event);"/>
				</td>
			</tr>
			</c:if>
			
			<c:if test="${transferenciaTurma.turmaOrigem.qtdMatriculados > 0}">
			<tr class="totalAlunos">
				<th class="obrigatorio" colspan="4" style="text-align:right" > 
					Número de alunos matriculados a transferir: 
				</th>
				<td>
					<h:inputText value="#{ transferenciaTurma.qtdMatriculas }" size="3" maxlength="3"  onkeypress="return ApenasNumeros(event);"/>
					
				</td>
			</tr>
			</c:if>
		</c:if>
		
		<c:if test="${ not transferenciaTurma.automatica }">
			
			<c:if test="${ not empty transferenciaTurma.matriculas}">
			<tr><td class="subFormulario" colspan="8"> Alunos matriculados </td></tr>
			<tr><td colspan="8">
			<t:dataTable id="datatableMatriculas" var="matricula" value="#{ transferenciaTurma.matriculas }" 
				rowClasses="linhaPar, linhaImpar" styleClass="listagem" columnClasses="colCenter,colLeft,colLeft,colLeft"
				headerClass="colCenter" >
				<t:column>
					<f:facet name="header"><f:verbatim><p align="center">Matrícula</p></f:verbatim></f:facet>
					<h:outputText value="#{ matricula.discente.matricula }"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
					<h:outputText value="#{ matricula.discente.nome }"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Status</f:verbatim></f:facet>
					<h:outputText value="#{ matricula.situacaoMatricula.descricao }"/>
				</t:column>
				<t:column>
					<f:facet name="header"></f:facet>
					<h:selectBooleanCheckbox value="#{ matricula.selected }"/>
				</t:column>
			</t:dataTable>
			</td></tr>
			</c:if>
			
			<c:if test="${ not empty transferenciaTurma.solicitacoes}">
			<tr><td class="subFormulario" colspan="8"> Solicitações de matrícula pendentes de processamento </td>
			<tr><td colspan="8">
			<t:dataTable id="datatableSolicitacoes" var="solicitacao" value="#{ transferenciaTurma.solicitacoes }" 
			 	rowClasses="linhaPar, linhaImpar" styleClass="listagem" columnClasses="colCenter,colLeft,colLeft,colLeft"
			 	headerClass="colCenter,colLeft,colLeft,colLeft" >
				<t:column>
					<f:facet name="header"><f:verbatim><p align="center">Matrícula</p></f:verbatim></f:facet>
					<h:outputText value="#{ solicitacao.discente.matricula }"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
					<h:outputText value="#{ solicitacao.discente.nome }"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Status</f:verbatim></f:facet>
					<h:outputText value="#{ solicitacao.statusDescricao }"/>
				</t:column>
				<t:column>
					<f:facet name="header"></f:facet>
					<h:selectBooleanCheckbox value="#{ solicitacao.selected }"/>
				</t:column>
			</t:dataTable>
			</td></tr>
			</c:if>
		</c:if>		
		</tbody>
		<tfoot>
			<tr>
			<td colspan="8">
				<h:commandButton value="Confirmar Transferência" action="#{transferenciaTurma.efetuarTransferencia}" />
				<h:commandButton value="<< Voltar" action="#{transferenciaTurma.voltarTurmaDestino}" />
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurma.cancelar}" />
			</td>
			</tr>
		</tfoot>		
	</table>
</h:form>

<br>
<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
	class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>