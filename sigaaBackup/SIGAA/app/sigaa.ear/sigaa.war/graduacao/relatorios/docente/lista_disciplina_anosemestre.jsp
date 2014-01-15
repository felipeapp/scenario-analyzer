<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de disciplinas minitradas por docentes</b></caption>
			<tr>
				<th>Ano-Semestre:</th>
				<td colspan="3"><b><h:outputText value="#{relatorioDocente.ano}"/>-<h:outputText
					value="#{relatorioDocente.periodo}"/></b>
				</td>
			</tr>
			<tr>
				<th>Centro:</th>
				<td><b><h:outputText
					value="#{relatorioDocente.departamento.gestora.nome }" /></b></td>
				<th>Departamento:</th>
				<td><b><h:outputText
					value="#{relatorioDocente.departamento.nome }" /></b></td>
			</tr>
	</table>
	<hr>
	<table>
	<thead>
		<tr>
			<td>Departamento</td><td>Docente</td><td>Mat.Siape</td><td>Disciplina</td><td>CH.Docente</td><td>CH.Turma</td>
		<tr>
	</thead>
	<c:forEach items="${relatorioDocente.listaDocentes}" var="linha">
		<tr>
			<td>
				${linha.depto}
			</td>
			<td>
				${linha.docente_nome}
			</td>
			<td>
				${linha.siape}
			</td>
			<td>
				${linha.disciplina_codigo}
			-
				${linha.disciplina}
			</td>
			<td>
				${linha.ch_docente_turma}
			</td>
			<td>
				${linha.ch_disciplina}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
