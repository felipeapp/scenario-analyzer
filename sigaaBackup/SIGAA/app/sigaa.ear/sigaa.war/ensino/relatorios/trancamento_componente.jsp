<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<h2> Relatório de Trancamento de Matrículas por Motivo</h2>

	<div id="parametrosRelatorio">
	<table >
		<tr>
			<th style="font-weight: bold"> Ano-Período: </th>
			<td> ${relatorioTrancamentoTurma.ano}-${relatorioTrancamentoTurma.periodo} </td>
		</tr>
		<c:if test="${not empty componente}">
			<tr>
				<th style="font-weight: bold"> Componente Curricular: </th>
				<td> ${componente} </td>
			</tr>
		</c:if>
	</table>
	</div>
	<br/>

	<c:if test="${empty dados}">
		Não há turmas cadastradas no ano-período informado.<br />
		<br />
	</c:if>
	<c:if test="${not empty dados}">
		<c:forEach var="linha" items="${dados}">
			<b>COMPONENTE:</b> ${linha.codigo_componente} - ${linha.componente} <br /> 
			<b>TURMA:</b> ${linha.codigo_turma} <br />
			<b>DISCENTES:</b> ${linha.total_discentes}<br />
			
			<table class="tabelaRelatorioBorda" width="100%">
				<thead>
				<tr>
					<th>MOTIVO</th>
					<th width="16%" style="text-align: right">TRANCAMENTOS</th>
					<th width="16%" style="text-align: right">PERCENTUAL</th>
				</tr>
				</thead>
				<tbody>
					<tr><th>${linha.motivo_descricao1}</th><td style="text-align: right">${linha.quant_trancamento1}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento1 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao2}</th><td style="text-align: right">${linha.quant_trancamento2}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento2 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao3}</th><td style="text-align: right">${linha.quant_trancamento3}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento3 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao4}</th><td style="text-align: right">${linha.quant_trancamento4}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento4 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao6}</th><td style="text-align: right">${linha.quant_trancamento6}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento6 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao7}</th><td style="text-align: right">${linha.quant_trancamento7}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento7 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao8}</th><td style="text-align: right">${linha.quant_trancamento8}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento8 * 100 / linha.total_discentes}" />%</td></tr>
					<tr><th>${linha.motivo_descricao9}</th><td style="text-align: right">${linha.quant_trancamento9}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.quant_trancamento9 * 100 / linha.total_discentes}" />%</td></tr>
					<c:set var="naoEspecificado" value="#{linha.trancamentos - (linha.quant_trancamento1 +linha.quant_trancamento2 +linha.quant_trancamento3 +linha.quant_trancamento4 +linha.quant_trancamento6 +linha.quant_trancamento7 +linha.quant_trancamento8 +linha.quant_trancamento9)}"/>
					<tr><th>MOTIVO NÃO ESPECIFICADO</th><td style="text-align: right">${naoEspecificado}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${naoEspecificado * 100 / linha.total_discentes}" />%</td></tr>
				</tbody>
				<tfoot>
					<tr><td style="text-align: right">TOTAL:</td><td style="text-align: right">${linha.trancamentos}</td><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${linha.trancamentos * 100 / linha.total_discentes}" />%</td></tr>
				</tfoot>
				<c:set var="total_motivo_1" value="${total_motivo_1 + linha.quant_trancamento1}"/>
				<c:set var="total_motivo_2" value="${total_motivo_2 + linha.quant_trancamento2}"/>
				<c:set var="total_motivo_3" value="${total_motivo_3 + linha.quant_trancamento3}"/>
				<c:set var="total_motivo_4" value="${total_motivo_4 + linha.quant_trancamento4}"/>
				<c:set var="total_motivo_6" value="${total_motivo_6 + linha.quant_trancamento6}"/>
				<c:set var="total_motivo_7" value="${total_motivo_7 + linha.quant_trancamento7}"/>
				<c:set var="total_motivo_8" value="${total_motivo_8 + linha.quant_trancamento8}"/>
				<c:set var="total_motivo_9" value="${total_motivo_9 + linha.quant_trancamento9}"/>
				<c:set var="total_nao_especificado" value="${total_nao_especificado + linha.trancamentos - (linha.quant_trancamento1 +linha.quant_trancamento2 +linha.quant_trancamento3 +linha.quant_trancamento4 +linha.quant_trancamento6 +linha.quant_trancamento7 +linha.quant_trancamento8 +linha.quant_trancamento9)}"/>
				<c:set var="total_trancamentos" value="${total_trancamentos + linha.trancamentos}"/>
				<c:set var="total_total_discentes" value="${total_total_discentes + linha.total_discentes}"/>
				<c:set var="total_percentual_trancamento" value="${100 * total_trancamentos / total_total_discentes}"/>
			</table>
			<br />
		</c:forEach>
		</c:if>
		<c:if test="${fn:length(dados) > 1}"> 
		
		<b>TOTAL GERAL</b>
		<table class="tabelaRelatorioBorda" width="100%">
			<thead>
				<tr>
					<th>MOTIVO</th>
					<th width="32%" style="text-align: right">TOTAL</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="linha" items="${dados}" begin="1" end="1">
					<tr><th>${linha.motivo_descricao1}</th><td style="text-align: right">${total_motivo_1}</td></tr>
					<tr><th>${linha.motivo_descricao2}</th><td style="text-align: right">${total_motivo_2}</td></tr>
					<tr><th>${linha.motivo_descricao3}</th><td style="text-align: right">${total_motivo_3}</td></tr>
					<tr><th>${linha.motivo_descricao4}</th><td style="text-align: right">${total_motivo_4}</td></tr>
					<tr><th>${linha.motivo_descricao6}</th><td style="text-align: right">${total_motivo_6}</td></tr>
					<tr><th>${linha.motivo_descricao7}</th><td style="text-align: right">${total_motivo_7}</td></tr>
					<tr><th>${linha.motivo_descricao8}</th><td style="text-align: right">${total_motivo_8}</td></tr>
					<tr><th>${linha.motivo_descricao9}</th><td style="text-align: right">${total_motivo_9}</td></tr>
					<tr><th>MOTIVO NÃO ESPECIFICADO</th><td style="text-align: right">${total_nao_especificado}</td></tr>
					<tr><th>TRANCAMENTOS</th><td style="text-align: right">${total_trancamentos}</td></tr>
					<tr><th>MATRICULADOS</th><td style="text-align: right"><fmt:formatNumber pattern="#,###" value="${total_total_discentes}" /></td></tr>
					<tr><th>PERCENTUAL TRANCAMENTOS</th><td style="text-align: right"><fmt:formatNumber pattern="#.##" value="${total_percentual_trancamento}" />%</td></tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
