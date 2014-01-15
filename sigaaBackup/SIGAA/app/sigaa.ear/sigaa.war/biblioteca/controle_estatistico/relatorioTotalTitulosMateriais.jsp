<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style type="text/css">
	#parametrosRelatorio table tr th {
		white-space: nowrap;
	}
	table.tabelaRelatorioBorda th.rightAlign {
		text-align: right;
	}
</style>

<f:view>

	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	<%-- quantidade real de Títulos no acervo com a classificação bibliografica --%>
	
	<c:set var="totalTitulos" value="0" scope="request"/>
	
	<c:if test="${ _abstractRelatorioBiblioteca.agrupamento1.agrupamentoClassificacaoBibliografica  &&  ! _abstractRelatorioBiblioteca.agrupamento2.semAgrupamento }">
		<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
			<caption>Quantidade de Títulos por Classificação</caption>
			<tr>
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTitulos }" var="linha">
					<th style="width: 8%;text-align: center;">${linha[0]}</th>
				</c:forEach>
				<th style="width: 10;text-align: right;">Total</th>
			</tr>
			
			<tr style="background-color: #DEDFE3;">
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTitulos }" var="linha">
					<td style="text-align: right;">${linha[1]}</td>
					<c:set var="totalTitulos" value="${totalTitulos + linha[1] }" scope="request"/>
				</c:forEach>
				<td style="font-weight: bold;text-align: right;">${totalTitulos}</td>
			</tr>
			
		</table>
	</c:if>
	
	
	
	
	
	<%-- 1 agrupamento --%>
	<c:if test="${ _abstractRelatorioBiblioteca.agrupamento2.semAgrupamento }">
		
		<table class="tabelaRelatorioBorda" style="width: 100%;">
		
			<thead>
				<tr style="background-color: #C2C2C2;">
					<th>${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</th>
					<th class="rightAlign">Títulos dos Materiais</th>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatFasciculos }">
						<th class="rightAlign">Exemplares</th>
					</c:if>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatExemplares }">
						<th class="rightAlign">Fascículos</th>
					</c:if>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${ _abstractRelatorioBiblioteca.resultados1 }" var="linha">
					<tr>
						<th>${ linha.key }</th>
						<td class="rightAlign" style="width: 20%;">${ linha.value[0] }</td>
						
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<td class="rightAlign" style="width: 20%;">${ linha.value[1] }</td>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<td class="rightAlign" style="width: 20%;">${ linha.value[2] }</td>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos }">
							<td class="rightAlign" style="width: 20%;">${ linha.value[1] }</td>
							<td class="rightAlign" style="width: 20%;">${ linha.value[2] }</td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
			
			<tfoot>
				<tr style="background-color: #DEDFE3;">
					<td>Total</td>
					<td class="rightAlign">
						${ _abstractRelatorioBiblioteca.total1[0] }
						
						<c:if test="${  _abstractRelatorioBiblioteca.agrupamento1.agrupamentoColecao || _abstractRelatorioBiblioteca.agrupamento1.agrupamentoTipoMaterial   
						||    _abstractRelatorioBiblioteca.agrupamento2.agrupamentoColecao || _abstractRelatorioBiblioteca.agrupamento2.agrupamentoTipoMaterial  }">
						*
						</c:if>
						
					</td>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial  == _abstractRelatorioBiblioteca.ctgMatExemplares }">
						<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total1[1] }</td>
					</c:if>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
						<td class="rightAlign" style="width: 20%;">${  _abstractRelatorioBiblioteca.total1[2] }</td>
					</c:if>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos }">
						<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total1[1] }</td>
						<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total1[2] }</td>
					</c:if>
			</tfoot>
		
		</table>
		
	</c:if>
	
	<%-- 2 agrupamentos --%>
	<c:if test="${ ! _abstractRelatorioBiblioteca.agrupamento2.semAgrupamento }">
	
		<c:set var="totalGeralTitulos" value="0" scope="request" />
		<c:set var="totalGeralExemplares" value="0" scope="request" />
		<c:set var="totalGeralFasciculos" value="0" scope="request" />
	
		<c:forEach items="${ _abstractRelatorioBiblioteca.resultados2 }" var="agrup1" >
			
			<table class="tabelaRelatorioBorda" style="margin-bottom: 15px; width: 100%;">
				<caption>${ _abstractRelatorioBiblioteca.descricaoAgrupamento1 }: ${ agrup1.key }</caption>
				
				<thead>
					<tr style="background-color: #C2C2C2;">
						<th>${_abstractRelatorioBiblioteca.descricaoAgrupamento2}</th>
						<th class="rightAlign" style="width: 20%;">Títulos</th>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<th class="rightAlign" style="width: 20%;">Exemplares</th>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<th class="rightAlign" style="width: 20%;">Fascículos</th>
						</c:if>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach items="${ agrup1.value }" var="linha" >
						<tr>
							<th>${ linha.key }</th>
							<td class="rightAlign">${ linha.value[0] }</td>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
								<td class="rightAlign">${ linha.value[1] }</td>
							</c:if>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
								<td class="rightAlign"> ${ linha.value[2] }</td>
							</c:if>
							<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos }">
								<td class="rightAlign">${ linha.value[1] }</td>
								<td class="rightAlign">${ linha.value[2] }</td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>

				<tfoot>
					<tr style="background-color: #DEDFE3;">
						<td>Total</td>
						<c:set var="totalGeralTitulos" value="${totalGeralTitulos+_abstractRelatorioBiblioteca.total2[agrup1.key][0]}" scope="request"/> 
						<td class="rightAlign">${ _abstractRelatorioBiblioteca.total2[agrup1.key][0] } * </td>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total2[agrup1.key][1] }</td>
							<c:set var="totalGeralExemplares" value="${totalGeralExemplares+_abstractRelatorioBiblioteca.total2[agrup1.key][1]}" scope="request"/>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total2[agrup1.key][2] }</td>
							<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos+_abstractRelatorioBiblioteca.total2[agrup1.key][2]}" scope="request"/>
						</c:if>
						
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos }">
							<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total2[agrup1.key][1] }</td>
							<td class="rightAlign" style="width: 20%;">${ _abstractRelatorioBiblioteca.total2[agrup1.key][2] }</td>
							
							<c:set var="totalGeralExemplares" value="${totalGeralExemplares+_abstractRelatorioBiblioteca.total2[agrup1.key][1]}" scope="request"/>
							<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos+_abstractRelatorioBiblioteca.total2[agrup1.key][2]}" scope="request"/>
							
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
							${totalGeralTitulos}  
							<c:if test="${  _abstractRelatorioBiblioteca.agrupamento1.agrupamentoColecao || _abstractRelatorioBiblioteca.agrupamento1.agrupamentoTipoMaterial   
							||    _abstractRelatorioBiblioteca.agrupamento2.agrupamentoColecao || _abstractRelatorioBiblioteca.agrupamento2.agrupamentoTipoMaterial  }">
							*
							</c:if>
						</td>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }">
							<td class="rightAlign" style="width: 20%;">${totalGeralExemplares}</td>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }">
							<td class="rightAlign" style="width: 20%;">${totalGeralFasciculos}</td>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos }">
							<td class="rightAlign" style="width: 20%;">${totalGeralExemplares}</td>
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