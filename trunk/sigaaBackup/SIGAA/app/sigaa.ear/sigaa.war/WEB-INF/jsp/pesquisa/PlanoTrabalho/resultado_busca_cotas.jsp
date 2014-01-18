<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>

table.tabelaRelatorio tbody td {
	border-bottom: 1px solid black;
}

</style>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Busca de Cotas &gt; Resultado da Distribuição de Cotas </h2>

<div id="cotasDocente" align="center">
	<c:if test="${not empty cotasDocente}">

		<h4 style="width: 70%; text-align: left">Docente: ${nomeDocente}</h4>
		<br />
		
		<table class="tabelaRelatorio" width="70%">
			<thead>
				<tr>
					<th style="text-align: left">Sigla</th>
					<th style="text-align: left">Índice</th>
					<th style="text-align: right">Valor</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="cotaDocente" items="${ cotasDocente }">
					<h4 style="width: 70%; text-align: left">Cota: ${cotaDocente.edital.cota } </h4>
					
					<br />
					
					<tr>
						<td align="left">IPI</td>
						<td align="left">Índice de Produtividade Individual</td>
						<td style="text-align: right">${cotaDocente.emissaoRelatorio.ipi}</td>
					</tr>
					<tr>
						<td align="left">IPI médio do centro</td>
						<td align="left">-</td>
						<td style="text-align: right">${mediasDocente[cotaDocente.id]}</td>
					</tr>
					<tr>
						<td align="left">Média dos projetos</td>
						<td align="left">-</td>
						<td style="text-align: right"><ufrn:format type="valor"
								name="cotaDocente" property="mediaProjetos" /></td>
					</tr>
					<tr>
						<td align="left">FPPI</td>
						<td align="left">Fator de Produtividade em Pesquisa
							Individual</td>
						<td style="text-align: right"><ufrn:format type="valor"
								name="cotaDocente" property="fppi" /></td>
					</tr>
					<tr>
						<td align="left"><strong>IFC</strong></td>
						<td align="left"><strong>Índice Final Classificatório</strong></td>
						<td style="text-align: right"><strong><ufrn:format type="valor"
								name="cotaDocente" property="ifc" /></strong></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<br />
		<c:forEach var="cotaDocente" items="${ cotasDocente }">
			<table class="tabelaRelatorio" width="70%">
				<thead>
					<tr>
						<th style="text-align: left">Bolsas concedidas</th>
						<th style="text-align: right">Quantidade</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="cota" items="${cotaDocente.cotas}">
						<tr>
							<td align="left">${cota.tipoBolsa.descricaoResumida}</td>
							<td style="text-align: right"><b>${cota.quantidade}</b></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:forEach>

	</c:if>

	<c:if test="${ empty cotasDocente }">
		<p> O discente não possui cotas contempladas. </p>
	</c:if>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>