<%@ taglib uri="/tags/mf_core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>
	<h2>Grupos da Tarefa</h2>


	<h:outputText value="#{grupoTarefaTurma.create}" />
	
	<h:form>
	<input type="hidden" name="id" value="${ grupoTarefaTurma.obj.tarefa.id }"/>
	<p align="center"><h:commandLink value="Novo Grupo" action="#{ grupoTarefaTurma.novoGrupo }"/></p>
	</h:form>
	
	<table class="listagem">
		<caption>Lista de Grupos</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td>Alunos</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${grupoTarefaTurma.allTarefa}" var="item" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>${item.descricao}</td>
				<td>
				<c:forEach var="discente" items="${ item.discentes }">
					${ discente.pessoa.nome }<br/>
				</c:forEach>
				</td>
				<h:form>
					<td><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" title="Alterar Grupo" value="Alterar"
						action="#{grupoTarefaTurma.atualizar}" /></td>
				</h:form>
				<h:form>
					<td><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" title="Remover Grupo" alt="Remover"
						action="#{grupoTarefaTurma.preRemover}" /></td>
				</h:form>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
