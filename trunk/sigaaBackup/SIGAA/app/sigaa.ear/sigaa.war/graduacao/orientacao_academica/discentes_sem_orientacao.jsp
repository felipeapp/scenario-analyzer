<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
<div id="parametrosRelatorio">
	<table>
		<tr>
		<c:if test="${acesso.coordenadorCursoGrad || acesso.secretarioGraduacao}">
			<tr>
				<th>
					Curso:
				</th>
				<td>
					${orientacaoAcademica.cursoAtualCoordenacao}
				</td>
			</tr>
		</c:if>
		<c:if test="${acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto}">
			<tr>
				<th>
					Programa:
				</th>
				<td>
					${orientacaoAcademica.programaStricto}
				</td>
			</tr>
		</c:if>
		<c:if test="${acesso.tecnico}">
			<tr>
				<th>
					Unidade Gestora:
				</th>
				<td>
					${orientacaoAcademica.unidadeGestora}
				</td>
			</tr>
		</c:if>
	</table>
</div>
<br/>

<table class="tabelaRelatorioBorda" width="100%">
	<caption> Discentes sem Orientação Acadêmica </caption>
	<thead>
		<tr>
			<th style="text-align: right;"> Matrícula </th>
			<th> Nome </th>
			<th> Matriz Curricular </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{orientacaoAcademica.resultadoBusca}" var="discente" varStatus="status">
		<tr>
			<td style="text-align: right;"> ${discente.matricula}</td>
			<td>${discente.nome}</td>
			<td>${discente.curriculo.matriz.descricaoMin} </td>
		</tr>
		</c:forEach>
	</tbody>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>