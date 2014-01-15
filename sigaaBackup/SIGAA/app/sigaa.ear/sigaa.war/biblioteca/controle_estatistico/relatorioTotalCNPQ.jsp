<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">
	.tabelaRelatorioBorda tr{
		text-align:right;
	}
	#parametrosRelatorio table tr th {
		white-space: nowrap;
	}
	table.tabelaRelatorioBorda th.rightAlign {
		text-align: right;
	}
</style>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<c:if test="${ _abstractRelatorioBiblioteca.agrupamento1.semAgrupamento }">
		
		<c:set var="totalTitulos" value="0" scope="request"/>
		<c:set var="totalMateriaisDigitais" value="0" scope="request"/>		
		
		
		<%-- A tabela da quantidade de Títulos reais  --%>
		<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
			<caption>Quantidade de Títulos por Área CNPq</caption>
			
			<tr>
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTotalTitulos }" var="linha">
					<th style="width: 8%;text-align: center;">${linha.key}</th>
				</c:forEach>
				<th style="width: 10;text-align: right;">Total</th>
			</tr>
			
			<tr style="background-color: #DEDFE3;">
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTotalTitulos }" var="linha">
					<td> ${linha.value[0]}</td>
					<c:set var="totalTitulos" value="${totalTitulos + linha.value[0] }" scope="request"/>
				</c:forEach>
				<td style="font-weight: bold;">${totalTitulos}</td>
			</tr>			
		</table>
		
		<%-- Se for apenas digitais mostra só a lista separada  --%>
		<c:if test="${_abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatDigitais }">
		
			<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
				<caption>Quantidade de Materiais Digitais</caption>
				
				<tr>
					<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosMateriaisDigitais }" var="linha">
						<th style="width: 8%;text-align: center;">${linha[0]}</th>
					</c:forEach>
					<th style="width: 10;text-align: right;">Total</th>
				</tr>
				
				<tr style="background-color: #DEDFE3;">
					<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosMateriaisDigitais }" var="linha">
						<td> ${linha[1]}</td>
						<c:set var="totalMateriaisDigitais" value="${totalMateriaisDigitais + linha[1] }" scope="request"/>
					</c:forEach>
					<td style="font-weight: bold;">${totalMateriaisDigitais}</td>
				</tr>			
			</table>
		
		</c:if>
		
		<%-- Se não for apenas digitais,  mostra a tabela de materiais  --%>
		
		<c:if test="${_abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatDigitais }">
		
			<table class="tabelaRelatorioBorda" style="margin: auto; width: 100%;">
		
				<thead>
					<tr>
						<th style="text-align:left;width:100px;">Área</th>
						<%-- <th style="text-align:right;">Títulos</th> --%>
						<th style="text-align:right;width:100px;">Títulos</th>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<th style="text-align:right;width:100px;">Exemplares</th>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<th style="text-align:right;width:100px;">Fascículos</th>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatDigitais }">
							<th style="text-align:right;width:100px;">Digitais</th>
						</c:if>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="r" items="#{_abstractRelatorioBiblioteca.resultadosNaoAgrupado}">
						<c:if test='${r[0] == "Total"}'>
							${"</tbody><tfoot>"}
						</c:if>
						<tr style="background-color: ${r[0] == 'Total' ? '#DEDFE3' : 'Transparent'};">
							<td style="text-align: ${r[0] == 'Total' ? 'right' : 'left'};">${r[0] != null ? r[0] : "Sem área"}</td>
							<%-- <td>${r[4]}</td> --%>
							<td>${r[1]} ${r[0] == 'Total' ? ' *' : ''}</td>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
								<td>${r[3]}</td>
							</c:if>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
								<td>${r[4]}</td>
							</c:if>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatDigitais }">
								<td>${r[5]}</td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
				
			</table>
		
		</c:if>
		
	</c:if>
	
	
	
	
	
	
	
	
	
	
	<c:if test="${ ! _abstractRelatorioBiblioteca.agrupamento1.semAgrupamento  }">
		<c:set var="totalGeralTitulos" value="0" scope="request" />
		<c:set var="totalGeralExemplares" value="0" scope="request" />
		<c:set var="totalGeralFasciculos" value="0" scope="request" />
		<c:set var="totalGeralDigitais" value="0" scope="request" />
		<c:set var="totalTitulos" value="0" scope="request"/>
		<c:set var="totalMateriaisDigitais" value="0" scope="request"/>		
				
		<%-- A tabela da quantidade de Títulos reais  --%>		
		<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
			<caption>Quantidade de Títulos por Área CNPq</caption>
			
			<tr>
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTotalTitulos }" var="linha">
					<th style="width: 8%;text-align: center;">${linha.key}</th>
				</c:forEach>
				<th style="width: 10;text-align: right;">Total</th>
			</tr>
			
			<tr style="background-color: #DEDFE3;">
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTotalTitulos }" var="linha">
					<td> ${linha.value[0]}</td>
					<c:set var="totalTitulos" value="${totalTitulos + linha.value[0] }" scope="request"/>
				</c:forEach>
				<td style="font-weight: bold;">${totalTitulos}</td>
			</tr>			
		</table>
	
		<%-- A tabela da quantidade de materiais digitais que não podem ser agrupados  --%>
		<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
			<caption>Quantidade de Materiais Digitais</caption>
			
			<tr>
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosMateriaisDigitais }" var="linha">
					<th style="width: 8%;text-align: center;">${linha[0]}</th>
				</c:forEach>
				<th style="width: 10;text-align: right;">Total</th>
			</tr>
			
			<tr style="background-color: #DEDFE3;">
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosMateriaisDigitais }" var="linha">
					<td> ${linha[1]}</td>
					<c:set var="totalMateriaisDigitais" value="${totalMateriaisDigitais + linha[1] }" scope="request"/>
				</c:forEach>
				<td style="font-weight: bold;">${totalMateriaisDigitais}</td>
			</tr>			
		</table>
	
	
	 	<%-- As tabelas com os dados agrupados  --%>
		<c:forEach items="${ _abstractRelatorioBiblioteca.resultados }" var="agrup1" >
			
			<table class="tabelaRelatorioBorda" style="margin-bottom: 15px; width: 100%;">
				<caption>Área: ${ agrup1.key }</caption>
				
				<thead>
					<tr style="background-color: #C2C2C2;">
						<th>${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</th>
						<th class="rightAlign" style="width: 20%;">Títulos</th>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<th class="rightAlign" style="width: 20%;">Exemplares</th>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<th class="rightAlign" style="width: 20%;">Fascículos</th>
						</c:if>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach items="${ agrup1.value }" var="linha" >
						<tr>
							<th>${ linha.key }</th>
							<td class="rightAlign">${ linha.value[0] }</td>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
								<td class="rightAlign">${ linha.value[2] }</td>
							</c:if>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
								<td class="rightAlign"> ${ linha.value[3] }</td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>

				<tfoot>
					<tr style="background-color: #DEDFE3;">
						<td>Total</td>
						<c:set var="totalGeralTitulos" value="${totalGeralTitulos+_abstractRelatorioBiblioteca.total[agrup1.key][0]}" scope="request"/> 
						<td class="rightAlign">${ _abstractRelatorioBiblioteca.total[agrup1.key][0] } * </td>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total[agrup1.key][2] }</td>
							<c:set var="totalGeralExemplares" value="${totalGeralExemplares+_abstractRelatorioBiblioteca.total[agrup1.key][2]}" scope="request"/>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total[agrup1.key][3] }</td>
							<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos+_abstractRelatorioBiblioteca.total[agrup1.key][3]}" scope="request"/>
						</c:if>
					</tr>
				</tfoot>

			</table>
			
		</c:forEach>
		
		<table class="tabelaRelatorioBorda" style="width: 100%;">
			<tfoot>
					<tr style="background-color: #DEDFE3;">
						<td>Total Geral</td>
						<td class="rightAlign" style="width: 20%">
							${totalGeralTitulos} *
						</td>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<td class="rightAlign" style="width: 20%;">${totalGeralExemplares}</td>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos || _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<td class="rightAlign" style="width: 20%;">${totalGeralFasciculos}</td>
						</c:if>
					</tr>
			</tfoot>
		</table>
	</c:if>
	
	
	<p style="margin-top: 15px; margin-bottom: 15px;">
	<strong>*</strong> &nbsp&nbsp&nbsp A totalização de Títulos apresentada nesse campo se refere a quantidade de Títulos dos materiais mostrados no relatório.
	Não representa o número real de Título no acervo e pode estar acima do número real, pois se os materiais ligados a um
	mesmo Título possuírem as informações usadas no agrupamento diferentes, o Título será contado mais de uma vez. <br/> 
	&nbsp&nbsp&nbsp&nbsp Por exemplo, se dois ou mais materiais de um mesmo Título estiverem em coleções diferentes, e o relatório for agrupado por 
	coleção, o mesmo Título será contado em todas as coleções em que seus materiais estiverem.
	</p> 
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>