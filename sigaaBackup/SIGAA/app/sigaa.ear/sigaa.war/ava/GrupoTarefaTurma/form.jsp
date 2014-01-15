<%@ taglib uri="/tags/mf_core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>
	<h2>Adicionar Grupo</h2>

<h:outputText value="#{grupoTarefaTurma.create}" />

	<h:form>
	
		<table class="formulario" width="50%">
			<caption>Dados do Grupo</caption>
			<tr>
				<th>Tarefa:</td>
				<td><h:outputText value="#{ grupoTarefaTurma.obj.tarefa.nome }"/>
				</td>
			</tr>
			<tr>
				<th>Descrição:</td>
				<td><h:inputText value="#{ grupoTarefaTurma.obj.descricao }" size="30"/></td>
			</tr>
			<tr>
				<th>Alunos:</td>
				<td>
				<h:selectManyCheckbox value="#{ grupoTarefaTurma.discentes }" layout="pageDirection">
					<f:selectItems value="#{ grupoTarefaTurma.alunosTurma }"/>
				</h:selectManyCheckbox>
				</td>
			</tr>
			<tfoot>
			<tr><td colspan="2">
					<h:inputHidden value="#{ grupoTarefaTurma.obj.id }"/>
					<h:inputHidden value="#{ grupoTarefaTurma.obj.tarefa.id }"/>
			     <h:commandButton value="#{grupoTarefaTurma.confirmButton}" action="#{ grupoTarefaTurma.cadastrar }"/>
			     <h:inputHidden value="#{grupoTarefaTurma.confirmButton}" /> 
			     <h:commandButton value="Cancelar" action="#{ grupoTarefaTurma.cancelar }"/>
			</td></tr>
			</tfoot>
		</table>

	</h:form>

</f:view>
<br><br>
<%@include file="/portais/turma/rodape.jsp"%>
