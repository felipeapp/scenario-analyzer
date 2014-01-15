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



<%-- Para imprimir o nome em vez do valor do mês --%>
<c:set var="descricaoMeses" value="${fn:split('Janeiro Fevereiro Março Abril Maio Junho Julho Agosto Setembro Outubro Novembro Dezembro',' ')}"  scope="request"/>


<%-- Para poder imprir as classificações do cabeçalhos da tabela em cada coluna, só funciona porque essa lista está ordenada e os resultados também são ordenados por classificação --%>
<c:set var="classes" value="${_abstractRelatorioBiblioteca.classesPrincipaisClassificacaoEscolhida}" />


<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<%-- Mostra uma tabela com a quantidade anterior de titulos existente no acervo  --%>   

	<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" >
		<table class="tabelaRelatorioBorda" style="width:100%;margin-top: 40px;">
			<caption> Quantidade anterior de Títulos </caption>
		
			<c:set var="_totalAnteriorTitulo" value="0" scope="request" />
		
			<tr>
				<th style="width: 50px;"></th>
				<c:forEach var="totalPorClasse" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorTitulo}">
					<th style="text-align: center; width: 100px;"> ${totalPorClasse.key}</th>
				</c:forEach>
				<th style="text-align: center; width: 100px;">Total</th>	
			</tr>
			<tr>
				<td></td>
				<c:forEach var="totalPorClasse" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorTitulo}">
					<td style="width:  100px;"> ${totalPorClasse.value}</td>
					<c:set var="_totalAnteriorTitulo" value="${_totalAnteriorTitulo + totalPorClasse.value}" scope="request" />
				</c:forEach>
				<td style="font-weight: bold"> ${_totalAnteriorTitulo} </td>
			</tr>
			
		</table>
			
	</c:if>			



	<%-- As tabela com os dados do crescimento de Títulos    --%>
	
	<table class="tabelaRelatorioBorda" style="width:100%;  margin-top: 40px;">
		<caption> Crescimento de Títulos </caption>
		
		<c:set var="totalGeralTitulos" value="0" scope="request" />
		
		
		<c:forEach var="dadosDoCrescimentoTitulo" items="#{_abstractRelatorioBiblioteca.resultadoCrescimentoTitulo}" >
			<tr>
				<td colspan="3" style="background:#6A6A6A;font-weight:bold;color: white;"> 	${dadosDoCrescimentoTitulo.ano} </td>
			</tr>
			
			
			<c:forEach var="dadosPorMes" items="#{dadosDoCrescimentoTitulo.dadosInternos}" >
				<tr>
					<td colspan="3" style="background:#EEEEEE;font-weight:bold;"> 	${ descricaoMeses[dadosPorMes.mes-1] } </td>
				</tr>
				
				<tr>
					<c:forEach var="dadosPorAgrupamento" items="#{dadosPorMes.dadosInternos}" >
						<td style="font-weight: bold; width: 50px; text-align: center;">${dadosPorAgrupamento.descricaoAgrupamento}</td>
						<td>
							<table style="width: 100%; border: 0px;">
								<tr>
									<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}" >
										<th style="width: 50px;text-align: center;">${dadosInternos.classificacao}</th>
									</c:forEach>
								</tr>
								<tr>
									<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}" >
										<td>
											<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
												${dadosInternos.quantidadeAnterior} + 
											</c:if>
											<span style="color:blue;">${dadosInternos.quantidade} </span>  
											<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
												= <span style="font-weight: bold;"> ${dadosInternos.quantidade + dadosInternos.quantidadeAnterior } </span> 	
											</c:if>
										</td>
									</c:forEach>
								</tr>
							</table>
						</td>
						<td style="font-weight: bold;"> Total: ${dadosPorAgrupamento.totalPorAgrupamento}</td>
					</c:forEach>
					
				</tr>
				
				<%--  Não é necessário na de títulos. 
				<tr>
					<td colspan="3" style="font-weight: bold;"> Total por Mês: ${dadosPorMes.totalPorMes}</td>
				</tr>--%>
				
				
			</c:forEach>
			
			<td colspan="3" style="font-weight:bold;"> Total por Ano: ${dadosDoCrescimentoTitulo.totalPorAno}</td>
			
			<c:if test="${!_abstractRelatorioBiblioteca.dadoBooleano}" > 
				<c:set var="totalGeralTitulos" value="${totalGeralTitulos + dadosDoCrescimentoTitulo.totalPorAno}" scope="request" />
			</c:if>
			<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" > 
				<c:set var="totalGeralTitulos" value="${dadosDoCrescimentoTitulo.totalPorAno}" scope="request" />  
			</c:if>
			
		</c:forEach>
		
		
	</table>	


	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 30px;">
		<tr>
			<td colspan="23" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral: ${totalGeralTitulos} </td>
		</tr>
	</table>






	<%-- Mostra uma tabela com a quantidade anterior de fascículos existente no acervo  --%>   

	<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" >
		<table class="tabelaRelatorioBorda" style="width:100%;margin-top: 40px;">
			<caption> Quantidade anterior de Fascículos </caption>
		
			<c:set var="_totalAnteriorMateriais" value="0" scope="request" />
		
			<tr>
				<th style="width: 50px;"></th>
				<c:forEach var="totalPorClasse" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorMateriais}">
					<th style="text-align: center; width: 100px;"> ${totalPorClasse.key}</th>
				</c:forEach>
				<th style="text-align: center; width: 100px;">Total</th>	
			</tr>
			<tr>
				<td></td>
				<c:forEach var="totalPorClasse" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorMateriais}">
					<td style="width:  100px;"> ${totalPorClasse.value}</td>
					
					<c:set var="_totalAnteriorMateriais" value="${_totalAnteriorMateriais + totalPorClasse.value}" scope="request" />
					
				</c:forEach>
				<td style="font-weight: bold"> ${_totalAnteriorMateriais} </td>
			</tr>
			
		</table>
			
	</c:if>			



	<%-- As tabela com os dados do crescimento de fascículos    --%>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 40px;">
		<caption> Crescimento de Fascículos  </caption>
		
		<c:set var="totalGeralMateriais" value="0" scope="request" />
		
		
		<c:forEach var="dadosDoCrescimentoMateriais" items="#{_abstractRelatorioBiblioteca.resultadoCrescimentoMaterial}" >
			<tr>
				<td colspan="23" style="background:#6A6A6A;font-weight:bold;color: white;"> 	${dadosDoCrescimentoMateriais.ano} </td>
			</tr>
			
			
			<c:forEach var="dadosPorMes" items="#{dadosDoCrescimentoMateriais.dadosInternos}" >
				<tr>
					<td colspan="23" style="background:#EEEEEE;font-weight:bold;"> 	${ descricaoMeses[dadosPorMes.mes-1] } </td>
				</tr>
				
				<%-- Cabeçalho  --%>
				<tr>
					<th style="width: 80px;"> </th>  <%-- Para colocar no nome do agrupamento utilizado no relatório --%>   
					<th colspan="21" style="padding: 0px; margin: 0px;">
						<table style="width: 100%;">
							<tbody>
								<tr>
									<c:forEach var="classe" items="#{classes}">                         
										<th style="width: 40px; max-width: 40px; min-width: 40px; text-align:center;">${ classe }</th>
									</c:forEach>
								</tr>
							</tbody>
						</table>
					</th>
					<th style="width: 80px; max-width: 80px; min-width: 80px; font-weight: bold;text-align: center;">Total</th>   <%-- Total por agrupamento --%>   
				</tr>
				
				<c:forEach var="dadosPorAgrupamento" items="#{dadosPorMes.dadosInternos}" varStatus="loop">
					<tr style="text-align: center;">
						<td style="font-weight: bold; width: 300px; text-align: center;">${dadosPorAgrupamento.descricaoAgrupamento}</td>
						
						<td colspan="21" style="padding: 0px; margin: 0px;">
							<table style="width: 100%;">
								<tbody>
									<tr>
										<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}"  varStatus="loop2">
											<td style="width: 40px; max-width: 40px; min-width: 40px; margin: 0px; padding: 0px;">
												<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
													${dadosInternos.quantidadeAnterior} + 
												</c:if>
												<span style="color:blue;">${dadosInternos.quantidade} </span>  
												<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
													= <span style="font-weight: bold;"> ${dadosInternos.quantidade + dadosInternos.quantidadeAnterior } </span> 	
												</c:if>
												
											</td>
										</c:forEach>
									</tr>
								</tbody>
							</table>
						</td>
						
						<td style="font-weight: bold; width: 50px; text-align: center;"> ${dadosPorAgrupamento.totalPorAgrupamento}</td>
					</tr>
					
				</c:forEach>
				
				<%-- Imprime do rodapé um total para cada classificação. --%>   
				<tr>
					<td style="font-weight: bold; text-align: center;">	Total</td>
					<td colspan="21" style="padding: 0px; margin: 0px;">
						<table style="width: 100%;">
							<tbody>
								<tr>
									<c:forEach var="totalPorClasse" items="#{dadosPorMes.totaisPorClasificacao}">
										<td style="width: 40px; max-width: 40px; min-width: 40px; margin: 0px; padding: 0px; font-weight: bold;"> ${totalPorClasse.value}</td>
									</c:forEach>
								</tr>
							</tbody>
						</table>
					</td>
					<td style="font-weight: bold; text-align: center;">  ${dadosPorMes.totalPorMes} </td>
				</tr>	
				
			</c:forEach>
			
			<td colspan="23" style="font-weight:bold;"> Total por Ano: ${dadosDoCrescimentoMateriais.totalPorAno}</td>
			
			<%-- Soma o total de cada ano --%>
			<c:if test="${! _abstractRelatorioBiblioteca.dadoBooleano}" >   
				<c:set var="totalGeralMateriais" value="${totalGeralMateriais + dadosDoCrescimentoMateriais.totalPorAno}" scope="request" />
			</c:if>
			<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" > 
				<c:set var="totalGeralMateriais" value="${dadosDoCrescimentoMateriais.totalPorAno}" scope="request" />  
			</c:if>
		</c:forEach>
		
	</table>	

	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 30px;">
		<tr>
			<td colspan="23" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral: ${totalGeralMateriais} </td>
		</tr>
	</table>





	<%-- Mostra um gráfico simples da variação da quantidade de fascículos adicionados a cada mês --%>   

	
		
	<%-- <div style="width: 100%; font-size: 20px; text-align: center; margin-top: 30px;">
		<img src="/sigaa/servlet/DisplayChart?filename=${_abstractRelatorioBiblioteca.gerarGraficoCrescimento}" width="900" height="500" />
	</div> --%>
	
	<div style="text-align: center; margin: 10px;">

		<jsp:useBean id="dadosDoGrafico" scope="page"
				class="br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.GraficoRelatorioCrescimentoPeriodicosPorClassePeriodo"  />
		
		<cewolf:chart id="grafico" type="stackedverticalbar3d"
				xaxislabel="Classe" yaxislabel="Fascículos" showlegend="true">
			<cewolf:data>
				<cewolf:producer id="dadosDoGrafico">
					<cewolf:param name="classes" value="${_abstractRelatorioBiblioteca.classes}" />
					<cewolf:param name="antes" value="${_abstractRelatorioBiblioteca.antes}" />
					<cewolf:param name="depois" value="${_abstractRelatorioBiblioteca.depois}" />
				</cewolf:producer>
			</cewolf:data>
		</cewolf:chart>
		
		<cewolf:img chartid="grafico" height="300" width="950" renderer="/cewolf" hspace="0" />
	
	</div>
		
	


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>