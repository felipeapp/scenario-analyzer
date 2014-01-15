<table class="visualizacao" style="width: 90%">
	<tr>
		<td colspan="2" class="subFormulario">Dados do Discente</td>
	</tr>
	<tr>
		<th width="30%">Matrícula:</th>
		<td>${discente.matricula}</td>
	</tr>
	<tr>
		<th>Nome:</th>
		<td>${discente.nome}</td>
	</tr>
	<tr>
		<th>Curso:</th>
		<td>${discente.curso.descricao}</td>
	</tr>
	<c:if test="${discente.pessoa.tipoNecessidadeEspecial != null}">
		<tr>
			<th>Necessidade Especial:</th>
			<td>${discente.pessoa.tipoNecessidadeEspecial.descricao}</td>
		</tr>		
	</c:if>
</table>