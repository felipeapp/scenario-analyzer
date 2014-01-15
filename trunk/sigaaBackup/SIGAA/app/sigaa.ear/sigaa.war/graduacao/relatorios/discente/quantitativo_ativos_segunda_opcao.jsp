<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<h2>Relatório Quantitativo de Alunos que Entraram por Segunda Opção no Vestibular</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Processo Seletivo: </th>
			<td> ${relatorioDiscente.descricaoProcessoSeletivo}</td>
		</tr>
	</table>
</div>

	<table class="tabelaRelatorioBorda">
		<thead>
			<tr>
				<th rowspan="2" width="100">Município</th>
				<th rowspan="2">Curso</th>
				<th rowspan="2">Grau Acadêmico</th>
				<th rowspan="2">Turno</th>
				<th rowspan="2" style="text-align: right;">Vagas Ofertadas</th>
				<th colspan="2" style="text-align: center">Quantidade de Aprovados</th>
			</tr>
			<tr>
				<th width="60" style="text-align: right;">1º Opção</th>
				<th width="60" style="text-align: right;">2º Opção</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha" varStatus="indice">
				<tr>
					<td>${linha.municipio}</td>
					<td>${linha.curso}</td>
					<td>${linha.grau_academico}</td>
					<td>${linha.turno}</td>
					<td style="text-align: right;">${linha.total_vagas}</td>
					<td style="text-align: right;">${linha.aprovados_opcao_1}</td>
					<td style="text-align: right;">${linha.aprovados_opcao_2}</td>
					<c:set var="total_opcao_1" value="${total_opcao_1 + linha.aprovados_opcao_1}" />
					<c:set var="total_opcao_2" value="${total_opcao_2 + linha.aprovados_opcao_2}" />
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" style="text-align: right;">Total:</td>
				<td style="text-align: right;">${total_opcao_1}</td>
				<td style="text-align: right;">${total_opcao_2}</td>
			</tr> 
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>