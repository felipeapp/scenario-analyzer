<%@include file="/portais/turma/cabecalho.jsp"%>
<f:view>
	<h2>planoCurso</h2>
	<br>
	<h:outputText value="#{planoCurso.create}" />
	<table class=listagem>
		<caption>Lista de planoCursos</caption>
		<tr>
			<td>metodoAvaliacao</td>
			<td>descricao</td>
			<td>turma</td>
			<td>usuario</td>
			<td>data</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${turmaVirtual.all}" var="item">
			<tr>
				<td>${item.metodoAvaliacao}</td>
				<td>${item.descricao}</td>
				<td>${item.turma}</td>
				<td>${item.usuario}</td>
				<td>${item.data}</td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{turmaVirtual.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{turmaVirtual.preRemover}" /></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/portais/turma/rodape.jsp"%>
