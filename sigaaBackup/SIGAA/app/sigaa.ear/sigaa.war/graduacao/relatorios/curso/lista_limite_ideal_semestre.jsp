<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório de alunos por cidade de residência</b></caption>
			<tr>
				<th>Centro:</th>
				<td><b><h:outputText
					value="#{relatorioCurso.centro.nome }" /></b></td>
				<th>Curso:</th>
				<td><b><h:outputText
					value="#{relatorioCurso.curso.descricao }" /></b></td>
			</tr>
	</table>
	<hr>
	<table>
	<thead>
		<tr>
			<td>Curso</td>
			<td>Ingresso</td>
			<td>Discente</td>
			<td>Cod.Currículo</td>
			<td>Cod.Currículo</td>
			<td>CH Disc.</td>
			<td>CH Compl.</td>
			<td>CH Obrigatória</td>
			<td>CH Cumprida Disc.</td>
			<td>CH Cumprida Compl.</td>
			<td>CH Integralizada</td>
			<td>CH Pendente Disc.</td>
		<tr>
	</thead>
	<c:forEach items="${relatorioCurso.listaDiscente}" var="linha">
		<tr>
			<td>
				${linha.centro} - ${linha.curso_nome}
			</td>
			<td>
				${linha.ano_ingresso}-${linha.periodo_ingresso}
			</td>
			<td>
				${linha.matricula} - ${linha.nome_aluno}
			</td>
			<td>
				${linha.curriculo_codigo}
			</td>
			<td>
				${linha.chdisciplina}
			</td>
			<td>
				${linha.chcomplementar}
			</td>
			<td>
				${linha.chobrigatoria}
			</td>
			<td>
				${linha.totalhoradisciplina}
			</td>
			<td>
				${linha.totalhoracomplementar}
			</td>
			<td>
				${linha.carga_hor_integralizada}
			</td>
			<td>
				${linha.totalhoradisciplinafalta}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
