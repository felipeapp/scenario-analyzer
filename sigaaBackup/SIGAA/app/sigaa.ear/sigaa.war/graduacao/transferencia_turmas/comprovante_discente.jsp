<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.dadosTurma th, tr.dadosTurma td{
		background: #EEE;
		border-bottom: 1px solid #DDD;
	}
	tr.dadosTurma td {
		font-weight: bold;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Transferência por Aluno  &gt; Comprovante de Transferência</h2>
	<h:form id="alunos">

	<table class="visualizacao">
		<caption>Dados do Discente</caption>
	</table>	
	<c:set var="discente" value="#{transferenciaTurma.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<table class="formulario" width="80%">
		<caption class="formulario">Dados da Transferência</caption>
		<thead>
			<td>Turma de Origem</td>
			<td>Turma de Destino</td>
		</thead>
		
		<c:forEach var="t" items="${transferenciaTurma.listTurmaOrigemDestinos}" varStatus="linha">
		<tr class="${ linha.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td> ${t.turma.descricaoCodigo} </td>
			<td style="text-align:${t.turmaDestino.id == 0 ? 'center' : 'left'}"> ${t.turmaDestino.id == 0 ? '-' : t.turmaDestino.descricaoCodigo} </td>
		</tr>
		</c:forEach>
	</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>