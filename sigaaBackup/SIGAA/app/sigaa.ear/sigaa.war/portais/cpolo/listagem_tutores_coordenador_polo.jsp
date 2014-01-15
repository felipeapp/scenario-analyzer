<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${acesso.coordenadorPolo}">
	<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
</c:if>

<h2> Listagem de Tutores </h2>

	<table class="listagem">
		<caption>Listagem de Tutores</caption>
		<thead>
		<tr>
			<th>Nome</th>
			<th>Curso</th>
			<th>Vínculo</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${portalCoordPolo.listagemTutores}" var="aluno" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td width="50%">
					${ aluno.nome }
				</td>
				<td width="50%">
					${ aluno.poloCurso.curso.descricao }
				</td>			
				<td>
					${ aluno.vinculo.nome }
				</td>			
			</tr>
		</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>