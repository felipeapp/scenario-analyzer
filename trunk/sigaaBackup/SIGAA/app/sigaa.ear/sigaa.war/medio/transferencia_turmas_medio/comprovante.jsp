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
	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurmaMedioMBean.descricaoTipo}  &gt; Comprovante de Transferência</h2>
	<h:form id="alunos">

	<table class="formulario" width="80%">
		<caption class="formulario">Dados da Transferência</caption>
		<tr>
			<td colspan="7" class="subFormulario">
				Turma de Origem
			</td>
		</tr>
		<tr>
			<td colspan="6">
				${transferenciaTurmaMedioMBean.turmaSerieOrigem.descricaoCompleta } <br/>
			</td>
			<td><b>Ano:</b> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.ano} </td>
		</tr>
		<tr class="dadosTurma">
			<th colspan="2">Turno:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieOrigem.turno.descricao}</td>
			<th>Matriculados:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieOrigem.qtdMatriculados}</td>
			<th>Capacidade:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieOrigem.capacidadeAluno}</td>
		</tr>
		
		<tr>
			<td colspan="7" class="subFormulario">
				Turma de Destino
			</td>
		</tr>
		<tr>
			<td colspan="6">
				${transferenciaTurmaMedioMBean.turmaSerieDestino.descricaoCompleta } <br/>
			</td>
			<td><b>Ano:</b> ${transferenciaTurmaMedioMBean.turmaSerieDestino.ano} </td>
		</tr>
		<tr class="dadosTurma">
			<th colspan="2">Turno:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieDestino.turno.descricao}</td>
			<th>Matriculados:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieDestino.qtdMatriculados}	</td>
			<th>Capacidade:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieDestino.capacidadeAluno}	</td>
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
				<tfoot>
			<tr>
				<td colspan="8">
					<h:commandButton value="Finalizar" action="#{transferenciaTurmaMedioMBean.cancelar}" id="btnFinalizar"/>
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>