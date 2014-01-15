<table class="visualizacao" style="width: 85%">
<caption>Dados do edital</caption>
	<tr>
		<th width="30%"> Edital: </th>
		<td> ${ edital.descricao } </td>
	</tr>
	<tr>
		<th> Cota: </th>
		<td> ${ edital.cota } </td>
	</tr>
	<tr>
		<th> FPPI Mínimo: </th>
		<td> <ufrn:format type="valor" name="edital" property="fppiMinimo" /></td>
	</tr>
	<c:if test="${not empty edital.cotas}">
			<tr>
				<td colspan="2" class="subFormulario"> Cotas distribuídas </td>
			</tr>
			<tr><td colspan="2">
				<table class="listagem">
	        		<thead>
	        			<tr>
	        				<th style="text-align: left;">Tipo da bolsa</th>
	        				<th style="text-align: right;">Quantidade</th>
	        				<th style="text-align: right;">Distribuídas</th>
	        				<th style="text-align: right;">Disponíveis</th>
	        			</tr>
	        		</thead>
	        		<c:set var="totalCotas" value="0"/>
	        		<c:set var="totalCotasDist" value="0"/>
	        		<c:set var="totalCotasDisp" value="0"/>
	        		<c:forEach items="${edital.cotas}" var="cota" varStatus="row">
	        			<tr>
	        				<td>${cota.tipoBolsa.descricaoResumida}</td>
	        				<td style="text-align: right;">${cota.quantidade}</td>
	        				<td style="text-align: right;">${edital.totalBolsasDistribuidas[cota.tipoBolsa.id]}</td>
	        				<td style="text-align: right;">${cota.quantidade - edital.totalBolsasDistribuidas[cota.tipoBolsa.id]}</td>
	        				<c:set var="totalCotas" value="${totalCotas + cota.quantidade}"/>
	        				<c:set var="totalCotasDist" value="${totalCotasDist + edital.totalBolsasDistribuidas[cota.tipoBolsa.id]}"/>
	        				<c:set var="totalCotasDisp" value="${totalCotasDisp + (cota.quantidade - edital.totalBolsasDistribuidas[cota.tipoBolsa.id])}"/>
	        			</tr>
	        		</c:forEach>
	        		<tfoot>
	        			<tr>
	        				<th style="text-align: left;">Totais</th>
	        				<th style="text-align: right;">${totalCotas}</th>
	        				<th style="text-align: right;">${totalCotasDist}</th>
	        				<th style="text-align: right;">${totalCotasDisp}</th>
	        			</tr>
	        		</tfoot>
	        	</table>
			</td></tr>
		</c:if>
</table>