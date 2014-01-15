<h2> ${relatorioPorCurso.relatorio.titulo} </h2>

<div id="parametrosRelatorio">
	<table>
		<c:if test="${not empty relatorioPorCurso.relatorio.descricaoFiltroCurso || 
			(not empty relatorioPorCurso.relatorio.curso  && relatorioPorCurso.relatorio.curso.id > 0)}">
		<tr>
			<th>Curso:</th>
			<td>
				<c:choose>
					<c:when test="${relatorioPorCurso.relatorio.filtroCursoSelecionado}">
						${relatorioPorCurso.relatorio.curso.descricao}
					</c:when>
					<c:otherwise>
						${relatorioPorCurso.relatorio.descricaoFiltroCurso}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		</c:if>
		<c:if test="${relatorioPorCurso.relatorio.exibeAnoPeriodo}">
			<tr>
				<th> ${relatorioPorCurso.relatorio.legendaAnoPeriodo}: </th>
				<td>
					${relatorioPorCurso.relatorio.ano}.${relatorioPorCurso.relatorio.periodo}
				</td>
			</tr>
		</c:if>
	</table>
</div>
<br/>