<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<%@taglib prefix="cewolf" uri="/tags/cewolf" %>

<style>
	.tabelaRelatorioBorda tr {
		text-align:center;
	}
	/* aumenta a largura exclusivamente para essa página, pois os dados do relatório contém bastantes colunas */
	#relatorio-paisagem-container {
    margin: 0 auto;
    text-align: left;
    width: 90%;
	}
	
</style>



<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<%-- As tabela com os dados do crescimento de Títulos    --%>
	
	
	<c:set var="totalTitulosClassificacao" value="0" scope="request" />
	
	
	<table class="tabelaRelatorioBorda" style="width:100%;">
		<caption style="font-size: 16px;" >Crescimento de Títulos</caption>
	</table>
	
	<table class="tabelaRelatorioBorda" style="width: 100%;">
		<%-- Imprime o cabeçalho que é a classificação --%>
		
		<tr style="background-color: #EEEEEE; text-align: right;">
			<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTitulosBasico }" var="dadosRelatorio" >
				<td style="font-weight: bold; width: 800px;">${dadosRelatorio[0]}</td>
			</c:forEach>
			<td style="font-weight: bold; width: 800px;">Total</td>
		</tr>
		
		<tr>
			<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTitulosBasico }" var="dadosRelatorio" >
				<td style="text-align: right;">${dadosRelatorio[1]}
				<c:set var="totalTitulosClassificacao" value="${totalTitulosClassificacao+dadosRelatorio[1]}" scope="request" />
				</td>
			</c:forEach>
			<td style="font-weight: bold; text-align: right;">${totalTitulosClassificacao}</td>
		</tr>
		
	</table>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 15px; margin-bottom: 30px;">
		<tr>
			<td colspan="23" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral de Título: ${_abstractRelatorioBiblioteca.totalGeralTitulos} </td>
		</tr>
	</table>
	
	
	
	
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 100px;">
		<caption style="font-size: 16px;">Crescimento de Títulos Agrupados*</caption>
	</table>
	
	<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosTitulosAgrupadoBasico }" var="dadosRelatorio" >
			
		<table class="tabelaRelatorioBorda" style="width: 100%;">
			<caption style="background-color: #C2C2C2;">   ${dadosRelatorio.descricaoAgrupamento1}: ${dadosRelatorio.valorAgrupamento1} </caption> 			
			
			<thead>
				<tr style="background-color: #EEEEEE;">
					<th  style="text-align: center;" colspan="${dadosRelatorio.qtdColunasTotalMatriz}"> ${dadosRelatorio.descricaoAgrupamento3} </th>
				</tr>
			</thead>
			
			 <c:forEach items="${dadosRelatorio.matrizDeResultados}" var="valorLinha" varStatus="linha">
				<tr>
					<c:forEach items="${valorLinha}" var="valorColuna" varStatus="coluna">
						<c:if test="${linha.index == 0 && coluna.index == 0}">
							<td style="width: 800px; font-weight:bold; text-align: center; background-color: #EEEEEE;"> ${dadosRelatorio.descricaoAgrupamento2} </td> 
						</c:if>
						
						<%-- Imprime o cabeçado e valores da colunas da tabela, incluindo os totais --%>
						
						<c:if test="${linha.index != 0 || coluna.index != 0}">
							<td style="width: 800px;  
								${ ( linha.index+1) == dadosRelatorio.qtdLinhasTotalMatriz || (coluna.index+1) == dadosRelatorio.qtdColunasTotalMatriz ? 'font-weight:bold;' : ''}
								${linha.index == 0 || coluna.index == 0 ? 'font-weight:bold;' : ' '}
								${linha.index == 0  ? 'text-align: right' : (coluna.index == 0 ? 'text-align: left;' : 'text-align: right;') }
							">
								${valorColuna == null ? '0' : valorColuna}
							</td> 
						</c:if>
					</c:forEach>
				</tr>
			</c:forEach> 
			
		</table>
				
		<br/><br/>
				
	</c:forEach>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-bottom: 30px;">
		<tr>
			<td colspan="23" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral de Título Agrupados: ${_abstractRelatorioBiblioteca.totalGeralTitulosAgrupados} </td>
		</tr>
	</table>
	
	
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 100px;">
		<caption style="font-size: 16px;">Crescimento de Materiais</caption>
	</table>
		
	<c:forEach items="${ _abstractRelatorioBiblioteca.resultadosMateriaisBasico }" var="dadosRelatorio" >
			
		<table class="tabelaRelatorioBorda" style="width: 100%;">
			<caption style="background-color: #C2C2C2;">   ${dadosRelatorio.descricaoAgrupamento1}: ${dadosRelatorio.valorAgrupamento1} </caption> 			
			
			<thead>
				<tr style="background: none repeat scroll 0 0 #EEEEEE;">
					<th  style="text-align: center;" colspan="${dadosRelatorio.qtdColunasTotalMatriz}"> ${dadosRelatorio.descricaoAgrupamento3} </th>
				</tr>
			</thead>
			
			 <c:forEach items="${dadosRelatorio.matrizDeResultados}" var="valorLinha" varStatus="linha">
				<tr>
					<c:forEach items="${valorLinha}" var="valorColuna" varStatus="coluna">
						<c:if test="${linha.index == 0 && coluna.index == 0}">
							<td style="width: 800px; font-weight:bold; text-align: center; background-color: #EEEEEE;"> ${dadosRelatorio.descricaoAgrupamento2} </td> 
						</c:if>
						
						<%-- Imprime o cabeçado e valores da colunas da tabela, incluindo os totais --%>
						
						<c:if test="${linha.index != 0 || coluna.index != 0}">
							<td style="width: 800px;  
								${ ( linha.index+1) == dadosRelatorio.qtdLinhasTotalMatriz || (coluna.index+1) == dadosRelatorio.qtdColunasTotalMatriz ? 'font-weight:bold;' : ''}
								${linha.index == 0 || coluna.index == 0 ? 'font-weight:bold;' : ' '}
								${linha.index == 0  ? 'text-align: right;' : (coluna.index == 0 ? 'text-align: left;' : 'text-align: right;')}	
							">
								${valorColuna == null ? '0' : valorColuna}
							</td> 
						</c:if>
					</c:forEach>
				</tr>
			</c:forEach> 
			
		</table>
				
		<br/><br/>
				
	</c:forEach>
		
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-bottom: 30px;">
		<tr>
			<td colspan="23" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral de Materiais: ${_abstractRelatorioBiblioteca.totalGeralMateriais} </td>
		</tr>
	</table>
	
	
	
	
	
	<p style="margin-top: 15px; margin-bottom: 15px;">
		<strong>*</strong> &nbsp;&nbsp;&nbsp; A quantidade de Títulos mostrada pode ser um pouco maior que a quantidade real, 
		pois ela está agrupada pelas informações dos seus materiais. Assim, caso um título possua materiais em agrupamentos 
		diferentes, ele será contado em duplicidade.
	</p>





	<%-- Mostra um gráfico simples da variação da quantidade de materiais adicionados a cada mês --%>   

	
		
	<%-- <div style="width: 100%; font-size: 20px; text-align: center; margin-top: 30px;">
		<img src="/sigaa/servlet/DisplayChart?filename=${_abstractRelatorioBiblioteca.gerarGraficoCrescimento}" width="900" height="500" />
	</div> --%>
	
	<div style="text-align: center; margin: 10px;">

		<jsp:useBean id="dadosDoGrafico" scope="page"
				class="br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.GraficoRelatorioCrescimentoPeriodicosPorCNPqPeriodo"  />
		
		<cewolf:chart id="grafico" type="stackedverticalbar3d"
				xaxislabel="Área CNPq" yaxislabel="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares ? 'Exemplares' : 'Fascículos' }" showlegend="true">
			<cewolf:data>
				<cewolf:producer id="dadosDoGrafico">
					<cewolf:param name="areas" value="${_abstractRelatorioBiblioteca.areas}" />
					<cewolf:param name="antes" value="${_abstractRelatorioBiblioteca.antes}" />
					<cewolf:param name="depois" value="${_abstractRelatorioBiblioteca.depois}" />
				</cewolf:producer>
			</cewolf:data>
		</cewolf:chart>
		
		<cewolf:img chartid="grafico" height="300" width="950" renderer="/cewolf" hspace="0" />
	
	</div>
		
	


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>