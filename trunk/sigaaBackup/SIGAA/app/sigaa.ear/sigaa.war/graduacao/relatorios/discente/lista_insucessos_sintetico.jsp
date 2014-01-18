<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	<div id="parametrosRelatorio">
		<table width="100%">
			<c:if test="${ not empty relatorioDiscente.curso.descricao }">
				<tr>
					<th>Curso:</th>
					<td colspan="3" width="80%">
						<b>${ relatorioDiscente.curso.descricao }</b>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td colspan="3" width="80%">
					<b>${relatorioDiscente.ano}.${relatorioDiscente.periodo}</b>
				</td>
			</tr>
		</table>
	</div>
	
	<c:set var="totalGeral" value="0"/>
	<table class="tabelaRelatorioBorda">
		<caption>Relatório Sintético de Insucessos de Alunos</caption>
		<thead>
			<tr>
				<th rowspan="3">Código</th>
				<th rowspan="3">Nome</th>
				<th rowspan="3">Pólo</th>
				<th colspan="7" style="text-align: center">Quantidade</th>
				<th rowspan="3" style="text-align: right;">Total<br/>Insucesso</th>
			</tr>
			<tr>
				<th rowspan="2" style="text-align: right;">Turmas</th>
				<th rowspan="2" style="text-align: right;">Discentes</th>
				<th rowspan="2" style="text-align: right;">Cancelamentos</th>
				<th colspan="3" style="text-align: center">Reprovações</th>
				<th rowspan="2" style="text-align: right;">Trancamentos</th>
			</tr>
			<tr>
				<th style="text-align: right;">Média</th>
				<th style="text-align: right;">Falta</th>
				<th style="text-align: right;" nowrap="nowrap">Média<br/>e Falta</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<tr>
				<td>${ linha.codigo_disciplina }</td>
				<td>${ linha.nome_disciplina }</td>
				<td>${ linha.municipio_polo }</td>
				<td style="text-align: right;">${ linha.qtd_turmas }</td>
				<td style="text-align: right;">${ linha.qtd_discentes }</td>
				<td style="text-align: right;">${ linha.qtd_cancelado }</td>
				<td style="text-align: right;">${ linha.qtd_reprovado }</td>
				<td style="text-align: right;">${ linha.qtd_reprovado_falta }</td>
				<td style="text-align: right;">${ linha.qtd_reprovado_media_falta }</td>
				<td style="text-align: right;">${ linha.qtd_trancamento }</td>
				<td style="text-align: right;">${ linha.total_insucesso }</td>
				<c:set var="total_qtd_turmas" value="${ total_qtd_turmas + linha.qtd_turmas }" />
				<c:set var="total_qtd_discentes" value="${ total_qtd_discentes + linha.qtd_discentes }" />
				<c:set var="total_qtd_cancelado" value="${ total_qtd_cancelado + linha.qtd_cancelado }" />
				<c:set var="total_qtd_reprovado" value="${ total_qtd_reprovado + linha.qtd_reprovado }" />
				<c:set var="total_qtd_reprovado_falta" value="${ total_qtd_reprovado_falta + linha.qtd_reprovado_falta }" />
				<c:set var="total_qtd_reprovado_media_falta" value="${ total_qtd_reprovado_media_falta + linha.qtd_reprovado_media_falta }" />
				<c:set var="total_qtd_trancamento" value="${ total_qtd_trancamento + linha.qtd_trancamento }" />
				<c:set var="total_insucesso" value="${ total_insucesso + linha.total_insucesso }" />
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="3" style="text-align: right;">Total:</td>
			<td style="text-align: right;">${ total_qtd_turmas }</td>
			<td style="text-align: right;">${ total_qtd_discentes }</td>
			<td style="text-align: right;">${ total_qtd_cancelado }</td>
			<td style="text-align: right;">${ total_qtd_reprovado }</td>
			<td style="text-align: right;">${ total_qtd_reprovado_falta }</td>
			<td style="text-align: right;">${ total_qtd_reprovado_media_falta }</td>
			<td style="text-align: right;">${ total_qtd_trancamento }</td>
			<td style="text-align: right;">${ total_insucesso }</td>
		</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
