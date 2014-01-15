<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style type="text/css">
	.tabelaRelatorioBorda tr td{
		width: 100px;
	}
	
	.tabelaRelatorioBorda tr th{
		width: 100px;
		text-align: center;
		font-weight: bold;
	}
	
</style>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
		
	
	<c:forEach items="${ _abstractRelatorioBiblioteca.resultados }" var="dadosRelatorio" >
			
		<table class="tabelaRelatorioBorda" style="margin-bottom: 15px; width: 100%;">
			<c:if test="${ ! _abstractRelatorioBiblioteca.agrupamento1.semAgrupamento }">
				<caption>   ${dadosRelatorio.descricaoAgrupamento1}:  ${dadosRelatorio.valorAgrupamento1}</caption> 			
			</c:if>
			
			<thead>
				<tr style="background-color: #C2C2C2;">
					<th  style="text-align: center;" colspan="${dadosRelatorio.qtdColunasTotalMatriz}"> ${dadosRelatorio.descricaoAgrupamento3} </th>
				</tr>
			</thead>
			
			 <c:forEach items="${dadosRelatorio.matrizDeResultados}" var="valorLinha" varStatus="linha">
				<tr>
					<c:forEach items="${valorLinha}" var="valorColuna" varStatus="coluna">
						<c:if test="${linha.index == 0 && coluna.index == 0}">
							<td style="width: 100px; font-weight:bold; text-align: center; background-color: #EEEEEE;"> ${dadosRelatorio.descricaoAgrupamento2} </td> 
						</c:if>
						<c:if test="${linha.index != 0 || coluna.index != 0}">
							<td style="width: 100px;  
								${ ( linha.index+1) == dadosRelatorio.qtdLinhasTotalMatriz || (coluna.index+1) == dadosRelatorio.qtdColunasTotalMatriz ? 'font-weight:bold;' : ''}
								${linha.index == 0 || coluna.index == 0 ? 'font-weight:bold; text-align: center' : 'text-align: right; '}">
								${valorColuna == null ? '0' : valorColuna}
								
							</td>
						</c:if>
					</c:forEach>
				</tr>
			</c:forEach> 
			
		</table>
				
		<br/><br/>
				
	</c:forEach>
	
	<c:if test="${ ! _abstractRelatorioBiblioteca.agrupamento1.semAgrupamento }">
	
		<table class="tabelaRelatorioBorda" style="margin-bottom: 15px; width: 100%;">
			<caption>   Totalização por Agrupamento </caption> 					
			
			<tr>
				<th></th>
				<c:forEach items="${_abstractRelatorioBiblioteca.totalPorAgrupamento}" var="totalizacao" varStatus="linha">
					<th style="background-color: #EEEEEE; text-align: center;">
						<c:out value="${totalizacao.key}"/>  
					</th> 
				</c:forEach>
				<th style="background-color: #EEEEEE; text-align: center;">Total</th> 
			</tr>
		
			
			<tr>
				<td style="background-color: #EEEEEE; font-weight: bold;">Total</td>
				 <c:forEach items="${_abstractRelatorioBiblioteca.totalPorAgrupamento}" var="totalizacao" varStatus="linha">
					<td style="text-align: right; font-weight: bold;">
		 				<c:out value="${totalizacao.value}"/>   
					</td> 
				</c:forEach> 
				<td style="text-align: right; font-weight: bold;">  ${_abstractRelatorioBiblioteca.totalGeral}</td>
			</tr>
			
			<tr  style="height: 30px;">  </tr>
			
		</table>
	
	
		<table class="tabelaRelatorioBorda" style="margin-bottom: 15px; width: 100%;">
			<caption>   Totalização por Classificação </caption> 					
		
			<tr>
				<th></th>
				 <c:forEach items="${_abstractRelatorioBiblioteca.totalPorClasse}" var="totalizacao" varStatus="linha">
					<th style="background-color: #EEEEEE; text-align: center;">
						<c:out value="${totalizacao.key}"/>  
					</th> 
				</c:forEach>
				<th style="background-color: #EEEEEE; text-align: center;">Total</th> 
			</tr>
		
			<tr>
				<td style="background-color: #EEEEEE; font-weight: bold;">Total</td>
				 <c:forEach items="${_abstractRelatorioBiblioteca.totalPorClasse}" var="totalizacao" varStatus="linha">
					<td style="text-align: right;">
		 				<c:out value="${totalizacao.value}"/>   
					</td> 
				</c:forEach> 
				<td style="text-align: right; font-weight: bold; ">${_abstractRelatorioBiblioteca.totalGeral}</td>
			</tr>
			
			<tr  style="height: 30px;">  </tr>
			
		</table>
	
		
	
	</c:if>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>