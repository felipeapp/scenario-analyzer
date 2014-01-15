<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina"><html:link action="/verMenuEnsinoTecnico">Ensino Técnico</html:link>
&gt; Histórico</h2>



<table class="formulario" width="90%">
	<caption>Dados do Aluno</caption>
	<tr>
		<td width="25%">Matrícula:</td>
		<td colspan="5"><b>${tecDiscente.matricula}</b></td>
	</tr>
	<tr>
		<td>Nome:</td>
		<td colspan="5"><b>${tecDiscente.pessoa.nome}</b></td>
	</tr>
	<tr>
		<td>Endereço</td>
		<td colspan="5"><b>${tecDiscente.pessoa.enderecoResidencial.logradouro} -
		${tecDiscente.pessoa.enderecoResidencial.numero}
		${tecDiscente.pessoa.enderecoResidencial.complemento}</b></td>
	<tr>
		<td>Bairro:</td>
		<td colspan="3"><b>${tecDiscente.pessoa.enderecoResidencial.bairro}</b></td>
	</tr>
	<tr>
		<td>Municipio:</td>
		<td><b>${tecDiscente.pessoa.enderecoResidencial.municipio.nome} -
		${tecDiscente.pessoa.enderecoResidencial.municipio.unidadeFederativa.sigla}</b></td>

	</tr>
	<tr>
		<td>Nome da Mãe:</td>
		<td colspan="5"><b>${tecDiscente.pessoa.nomeMae}</b></td>
	</tr>
	<tr>
		<td>&nbsp</td>
		<td>&nbsp</td>
	</tr>
	<tr>
		<td>Ano/Período Ingresso:</td>
		<td colspan="5"><b>${tecDiscente.anoIngresso} /
		${tecDiscente.periodoIngresso}</b></td>
	</tr>
	<tr>
		<td>Média Geral:</td>
		<td colspan="5"><b>${mediaFinal}</b></td>
	</tr>
	<tr>
		<td>Carga Horária Integralizada:</td>
		<td colspan="5"><b>${tecDiscente.chIntegralizada}</b></td>
	</tr>
	<tr>
		<td>Forma de Ingresso:</td>
		<td colspan="5"><b>${tecDiscente.formaIngresso.descricao}</b></td>
	</tr>
	<tr>
		<td>Tipo de Regime do Aluno:</td>
		<td colspan="5"><b>${tecDiscente.tipoRegimeAluno.descricao}</b></td>
	</tr>
	<tr>
		<td colspan="6">
		<table class=formulario width="95%">
			<caption>Curso</caption>
			<tr>
				<td width="20%">Curso:</td>
				<td><b>${tecDiscente.cursoTecnico.nome}</b></td>
			</tr>
			<tr>
				<td>Modalidade:</td>
				<td><b>${tecDiscente.cursoTecnico.modalidadeCursoTecnico.descricao}</b></td>
			</tr>
			<tr>
				<td nowrap="nowrap">Estrutura Curricular:</td>
				<td><b>${tecDiscente.turmaEntradaTecnico.estruturaCurricularTecnica.codigo}</b></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<br>
<br>
<c:if test="${not empty tecDiscente.matriculasDisciplina}">

	<ufrn:table collection="${tecDiscente.matriculasDisciplina}"
		properties="turma.ano,turma.periodo,turma.disciplina.codigo,turma.disciplina.nome,turma.codigo,
	turma.chTotalTurma,
	situacaoMatricula.descricao,mediaFinal,numeroFaltas,tipoResultado.descricao"
		headers="Ano,Período,Cod,Disciplina,Tur,CH,Situação,Média,Freq,Resultado"
		title="Disciplinas Cursadas/Cursando" crud="false" />

<br>
<br>
</c:if>
<c:if test="${not empty listaAproveitamentoDisciplinas}">

	<ufrn:table collection="${listaAproveitamentoDisciplinas}"
		properties="disciplina.codigo,disciplina.nome,tipoConcessao.descricao,
	disciplina.chTotal"
		headers="Cod,Disciplina,Concessão,CH"
		title="Disciplinas com Aproveitamento" crud="false" />

<br>
<br>
</c:if>
listaAproveitamentoDisciplinas
<table class=formulario width="80%" >
	<caption>Totais:</caption>
	<thead>
		<tr>
			<th colspan="2">Integralizadas</th>
			<th colspan="2">Pendentes</th>
		</tr>
	<tbody>
		<tr>
			<td width="15%">Carga Horária</td>
			<td width="35%"><b>${tecDiscente.chIntegralizada}</b></td>
			<td width="15%">Carga Horária</td>
			<td width="35%"><b>${cargaHorariaPendente}</b></td>
		</tr>
		<tr>
			<td nowrap="nowrap">Total de Disciplinas</td>
			<td><b>${totalDisciplinasIntegralizadas}</b></td>
			<td nowrap="nowrap">Total de Disciplinas</td>
			<td><b>${totalDisciplinasPendentes}</b></td>
		</tr>
	</tbody>
</table>
<br>
<br>

		<c:if test="${empty listaDisciplinasPendentes}">
		<tr>
			<td>
			<table class="formulario" width="80%">
			<caption>Disciplinas Pendentes</caption>
			<tbody>
			<th><font color="red">Não Possui</font></th>
			</tbody>
			</table>
			</td>
		</tr>
		</c:if>
		<c:if test="${not empty listaDisciplinasPendentes }">
		<tr>
			<td>
			<ufrn:table collection="${listaDisciplinasPendentes}"
				properties="codigo,nome,chTotal"
				headers="Código, Nome, CH" title="Disciplinas Pendentes" crud="false" />
			</td>
		</tr>
		</c:if>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
