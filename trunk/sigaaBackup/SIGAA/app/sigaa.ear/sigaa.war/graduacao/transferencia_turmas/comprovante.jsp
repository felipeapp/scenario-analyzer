<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.dadosTurma th {
		background: #EEE;
		border-bottom: 1px solid #DDD;
		font-weight: bold;
	}
	tr.dadosTurma td {
		background: #EEE;
		border-bottom: 1px solid #DDD;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurma.descricaoTipo}  &gt; Comprovante de Transferência</h2>
	<h:form id="alunos">

	<table class="formulario" width="80%">
		<caption class="formulario">Dados da Transferência</caption>
		<tr>
			<td colspan="8" class="subFormulario">
				Turma de Origem
			</td>
		</tr>
		<tr>
			<td colspan="7">
				${turmaOrigem.disciplina.descricaoResumida } - Turma ${turmaOrigem.codigo} <br/>
				<b>Docente(s):</b> ${turmaOrigem.docentesNomes}
			</td>
			<td><b>Período: </b>${transferenciaTurma.turmaOrigem.ano}.${transferenciaTurma.turmaOrigem.periodo} </td>
		</tr>
		<tr class="dadosTurma">
			<th>Horário:</th>
			<td>${turmaOrigem.descricaoHorario}</td>
			<th>Matriculados:</th>
			<td>${turmaOrigem.qtdMatriculados}</td>
			<th>Solicitações:</th>
			<td>${turmaOrigem.qtdEspera}</td>
			<th>Capacidade:</th>
			<td>${turmaOrigem.capacidadeAluno}</td>
		</tr>
		
		<tr>
			<td colspan="8" class="subFormulario">
				Turma de Destino
			</td>
		</tr>
		<tr>
			<td colspan="7">
				${turmaDestino.disciplina.descricaoResumida } - Turma ${turmaDestino.codigo} <br/>
				<b>Docente(s): </b>${turmaDestino.docentesNomes}
			</td>
			<td><b>Período: </b>${transferenciaTurma.turmaDestino.ano}.${transferenciaTurma.turmaDestino.periodo} </td>
		</tr>
		<tr class="dadosTurma">
			<th>Horário:</th>
			<td>${turmaDestino.descricaoHorario}</td>
			<th>Matriculados:</th>
			<td>${turmaDestino.qtdMatriculados}	</td>
			<th>Solicitações:</th>
			<td>${turmaDestino.qtdEspera}</td>
			<th>Capacidade:</th>
			<td>${turmaDestino.capacidadeAluno}	</td>
		</tr>
		
		<tr>
			<td colspan="8" class="subFormulario">
				Alunos transferidos
			</td>
		</tr>
		<c:forEach var="d" items="${discentes}" varStatus="linha">
		<tr class="${ linha.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td colspan="8"> ${d.matricula} - ${d.nome} </td>
		</tr>
		</c:forEach>
	</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>