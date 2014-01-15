<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<h2>Relat�rio de Bolsistas com Percentual de Carga Hor�ria Matriculada</h2>

<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Ano-Per�odo:</th>
		<td>${relatorioAcompanhamentoBolsas.ano}.${relatorioAcompanhamentoBolsas.periodo}</td>
	</tr>
</table>
</div>
<br/>

	<table class="tabelaRelatorioBorda">
		<thead>
			<tr>
				<td style="text-align: right;">Matricula</td>
				<td align="left">Nome</td>
				<td style="text-align: right;">Per�odo Atual</td>
				<td style="text-align: right;">CH no Per�odo</td>
				<td style="text-align: right;">CH Matriculada</td>
				<td style="text-align: right;">% Matriculado</td>
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty relatorioAcompanhamentoBolsas.dadosRelatorio}">
			<c:forEach items="${relatorioAcompanhamentoBolsas.dadosRelatorio}" var="linha" varStatus="indice">
				<tr>
					<td align="right">${linha.matricula }</td>
					<td align="left">${linha.nome }</td>
					<td align="right">${linha.periodo_atual }</td>
					<td align="right">${linha.ch_total == null ? "N/M" : linha.ch_total}</td>
					<td align="right">${linha.ch_matriculada == null ? "N/M" : linha.ch_matriculada}</td>
					<td align="right">${linha.perc_ch_matriculado == null? 0 :linha.perc_ch_matriculado}%</td>
				</tr>
			</c:forEach>
		</c:if>
		</tbody>
	</table>
	<br/>
	<b>Legenda:</b>
	<ul>
		<li><b>N/M</b> Discente n�o matriculado no semestre.</li>
	</ul>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>