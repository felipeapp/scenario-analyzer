<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{exclusaoMatricula.create}"/>

	<h2> Exclusão de Matrícula </h2>

	<c:set var="discente" value="#{exclusaoMatricula.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<table class="listagem">
		<caption>Selecione a disciplina cuja matrícula será excluída:</caption>
		<thead>
		<tr>
			<th> Disciplina </th>
			<th> Turma</th>
			<th> CH</th>
			<th> Co</th>
			<th> </th>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="matricula" items="${exclusaoMatricula.matriculas}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td> ${matricula.turma.disciplina.codigo} - ${matricula.turma.disciplina.detalhes.nome} </td>
				<td align="center">${matricula.turma.codigo}</td>
				<td>${matricula.turma.disciplina.detalhes.chTotal}</td>
				<td>${matricula.turma.disciplina.coRequisito}</td>
				<td>
					<h:form>
					<input type="hidden" value="${matricula.id}" name="idMatricula"/>
					<h:commandButton image="/img/avaliar.gif" alt="Trancar Matrícula" title="Trancar Matrícula" id="btaoTrancarMatricula"
						action="#{exclusaoMatricula.selecionarDisciplina}" />
					</h:form>
				</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton value="Selecionar Outro Discente" action="#{exclusaoMatricula.buscarDiscente}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{exclusaoMatricula.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>