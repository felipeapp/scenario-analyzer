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

<%-- Para poder imprir as áreas do cabeçalhos da tabela em cada coluna. IMPORTANTE, Só funciona porque a consulta traz os resultados ordenados e essa coleção está ordenada --%>
<c:set var="siglasAreasCNPq" value="${_abstractRelatorioBiblioteca.grandesAreasConhecimentoCNPq}" />


<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<%-- Mostra uma tabela com a quantidade anterior de titulos existente no acervo  --%>   

	<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" >
		<table class="tabelaRelatorioBorda" style="width:100%;margin-top: 40px;">
			<caption> Quantidade anterior de Títulos </caption>
		
			<c:set var="_totalAnteriorTitulo" value="0" scope="request" />
		
			<tr>
				<th style="width: 50px;"></th>
				<c:forEach var="totalPorArea" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorTitulo}">
					<th style="text-align: right; width: 800px;"> ${totalPorArea.key}</th>
				</c:forEach>
				<th style="text-align: right; width: 800px;">Total</th>	
			</tr>
			<tr>
				<td></td>
				<c:forEach var="totalPorArea" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorTitulo}">
					<td style="width:  800px; text-align: right;"> ${totalPorArea.value}</td>
					<c:set var="_totalAnteriorTitulo" value="${_totalAnteriorTitulo + totalPorArea.value}" scope="request" />
				</c:forEach>
				<td style="font-weight: bold; text-align: right;"> ${_totalAnteriorTitulo} </td>
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
						<td style="font-weight: bold; width:50px; text-align: center;">${dadosPorAgrupamento.descricaoAgrupamento}</td>
						<td>
							<table style="width: 100%; border: 0px;">
								<tr>
									<!--  Imprime os cabeçalhos -->
									<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}" >
										<th style="width: 800px;text-align: right;">${dadosInternos.descricaoArea}</th>
									</c:forEach>
									<!--  Imprime os cabeçalhos do Total -->
									<td style="width: 800px; font-weight:bold; text-align: right;"> 	Total </td>
								</tr>
								<tr>
									<!--  Imprime os valores  -->
									<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}" >
										<td style="text-align: right;">
											<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
												${dadosInternos.quantidadeAnterior} + 
											</c:if>
											<span style="color:blue;">${dadosInternos.quantidade} </span>  
											<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
												= <span style="font-weight: bold;"> ${dadosInternos.quantidade + dadosInternos.quantidadeAnterior } </span> 	
											</c:if>
										</td>
									</c:forEach>
									<!--  Imprime os  Totais -->
									<td style="font-weight: bold; text-align: right;"> ${dadosPorAgrupamento.totalPorAgrupamento}</td>
								</tr>
							</table>
						</td>
						
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
			<td colspan="12" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral: ${totalGeralTitulos} </td>
		</tr>
	</table>












	
	
	
	<%-- As tabela com os dados do crescimento dos Titulos Agrupados    --%>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 40px;">
		<caption> Crescimento de Títulos Agrupados*  </caption>
		
		<c:set var="totalGeralTitulosAgrupados" value="0" scope="request" />
		
		
		<c:forEach var="dadosDoCrescimentoTitulosAgrupados" items="#{_abstractRelatorioBiblioteca.resultadoCrescimentoTituloAgrupado}" >
			<tr>
				<td colspan="12" style="background:#6A6A6A;font-weight:bold;color: white;"> 	${dadosDoCrescimentoTitulosAgrupados.ano} </td>
			</tr>
			
			
			<c:forEach var="dadosPorMes" items="#{dadosDoCrescimentoTitulosAgrupados.dadosInternos}" >
				<tr>
					<td colspan="12" style="background:#EEEEEE;font-weight:bold;"> 	${ descricaoMeses[dadosPorMes.mes-1] } </td>
				</tr>
				
				<%-- Cabeçalho  --%>
				<tr>
					<th style="width: 800px;"> </th>  <%-- Para colocar no nome do agrupamento utilizado no relatório --%>   
					<c:forEach var="sigla" items="#{siglasAreasCNPq}">                         
						<th style="width: 800px; text-align:right;">${ sigla.sigla }</th>
					</c:forEach>
					<th style="width: 800px; font-weight: bold;text-align: right;">Total</th>   <%-- Total por agrupamento --%>   
				</tr>
				
				<c:forEach var="dadosPorAgrupamento" items="#{dadosPorMes.dadosInternos}" varStatus="loop">
					<tr style="text-align: center;">
						<td style="font-weight: bold; width: 800px; text-align: left;">${dadosPorAgrupamento.descricaoAgrupamento}</td>
						
						
						<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}"  varStatus="loop2">
							<td style="width: 800px; margin: 0px; padding: 0px; text-align: right;">
								<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
									${dadosInternos.quantidadeAnterior} + 
								</c:if>
								<span style="color:blue;">${dadosInternos.quantidade} </span>  
								<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
									= <span style="font-weight: bold;"> ${dadosInternos.quantidade + dadosInternos.quantidadeAnterior } </span> 	
								</c:if>
								
							</td>
						</c:forEach>
									
						
						<td style="font-weight: bold; width: 800px; text-align: right;"> ${dadosPorAgrupamento.totalPorAgrupamento}</td>
					</tr>
					
				</c:forEach>
				
				<%-- Imprime do rodapé um total para cada classificação. --%>   
				
				<tr>
					<td style="font-weight: bold; text-align: left;">	Total</td>
					
					<c:forEach var="totalPorArea" items="#{dadosPorMes.totaisPorArea}">
						<td style="width: 800px; margin: 0px; padding: 0px; font-weight: bold; text-align: right;"> ${totalPorArea.value}</td>
					</c:forEach>
								
					<td style="font-weight: bold; text-align: right;">  ${dadosPorMes.totalPorMes} </td>
				</tr>
				
			</c:forEach>
			
			<td colspan="12" style="font-weight:bold;"> Total por Ano: ${dadosDoCrescimentoTitulosAgrupados.totalPorAno}</td>
			
			<%-- Soma o total de cada ano --%>
			<c:if test="${! _abstractRelatorioBiblioteca.dadoBooleano}" >   
				<c:set var="totalGeralTitulosAgrupados" value="${totalGeralMateriais + dadosDoCrescimentoTitulosAgrupados.totalPorAno}" scope="request" />
			</c:if>
			<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" > 
				<c:set var="totalGeralTitulosAgrupados" value="${dadosDoCrescimentoTitulosAgrupados.totalPorAno}" scope="request" />  
			</c:if>
		</c:forEach>
		
	</table>	

	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 30px;">
		<tr>
			<td colspan="12" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral: ${totalGeralTitulosAgrupados} </td>
		</tr>
	</table>
	
	
	




	<%-- Mostra uma tabela com a quantidade anterior de materiais existente no acervo  --%>   

	<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}" >
		<table class="tabelaRelatorioBorda" style="width:100%;margin-top: 40px;">
			<caption> Quantidade anterior de Materiais </caption>
		
			<c:set var="_totalAnteriorMateriais" value="0" scope="request" />
		
			<tr>
				<th style="width: 800px;"></th>
				<c:forEach var="totalPorArea" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorMateriais}">
					<th style="text-align: right; width: 800px;"> ${totalPorArea.key}</th>
				</c:forEach>
				<th style="text-align: right; width: 800px;">Total</th>	
			</tr>
			<tr>
				<td></td>
				<c:forEach var="totalPorArea" items="#{_abstractRelatorioBiblioteca.quantidadeAnteriorMateriais}">
					<td style="width:  800px; text-align: right;"> ${totalPorArea.value}</td>
					
					<c:set var="_totalAnteriorMateriais" value="${_totalAnteriorMateriais + totalPorArea.value}" scope="request" />
					
				</c:forEach>
				<td style="font-weight: bold; text-align: right;"> ${_totalAnteriorMateriais} </td>
			</tr>
			
		</table>
			
	</c:if>			



	<%-- As tabela com os dados do crescimento de Materiais    --%>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 40px;">
		<caption> Crescimento de Materiais  </caption>
		
		<c:set var="totalGeralMateriais" value="0" scope="request" />
		
		
		<c:forEach var="dadosDoCrescimentoMateriais" items="#{_abstractRelatorioBiblioteca.resultadoCrescimentoMaterial}" >
			<tr>
				<td colspan="12" style="background:#6A6A6A;font-weight:bold;color: white;"> 	${dadosDoCrescimentoMateriais.ano} </td>
			</tr>
			
			
			<c:forEach var="dadosPorMes" items="#{dadosDoCrescimentoMateriais.dadosInternos}" >
				<tr>
					<td colspan="12" style="background:#EEEEEE;font-weight:bold;"> 	${ descricaoMeses[dadosPorMes.mes-1] } </td>
				</tr>
				
				<%-- Cabeçalho  --%>
				<tr>
					<th style="width: 800px;"> </th>  <%-- Para colocar no nome do agrupamento utilizado no relatório --%>   
					<c:forEach var="sigla" items="#{siglasAreasCNPq}">                         
						<th style="width: 800px; text-align:right;">${ sigla.sigla }</th>
					</c:forEach>
					<th style="width: 800px; font-weight: bold;text-align: right;">Total</th>   <%-- Total por agrupamento --%>   
				</tr>
				
				<c:forEach var="dadosPorAgrupamento" items="#{dadosPorMes.dadosInternos}" varStatus="loop">
					<tr style="text-align: center;">
						<td style="font-weight: bold; width: 300px; text-align: left;">${dadosPorAgrupamento.descricaoAgrupamento}</td>
						
						
						<c:forEach var="dadosInternos" items="#{dadosPorAgrupamento.dadosInternos}"  varStatus="loop2">
							<td style="width: 800px; margin: 0px; padding: 0px; text-align: right;">
								<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
									${dadosInternos.quantidadeAnterior} + 
								</c:if>
								<span style="color:blue;">${dadosInternos.quantidade} </span>  
								<c:if test="${_abstractRelatorioBiblioteca.dadoBooleano}">
									= <span style="font-weight: bold;"> ${dadosInternos.quantidade + dadosInternos.quantidadeAnterior } </span> 	
								</c:if>
								
							</td>
						</c:forEach>
									
						
						<td style="font-weight: bold; width: 800px; text-align: right;"> ${dadosPorAgrupamento.totalPorAgrupamento}</td>
					</tr>
					
				</c:forEach>
				
				<%-- Imprime do rodapé um total para cada classificação. --%>   
				<tr>
					<td style="font-weight: bold; text-align: left;">	Total</td>
					<c:forEach var="totalPorArea" items="#{dadosPorMes.totaisPorArea}">
						<td style="font-weight: bold; text-align: right;"> ${totalPorArea.value}</td>
					</c:forEach>
					<td style="font-weight: bold; text-align: right;">  ${dadosPorMes.totalPorMes} </td>
				</tr>	
				
			</c:forEach>
			
			<td colspan="12" style="font-weight:bold;"> Total por Ano: ${dadosDoCrescimentoMateriais.totalPorAno}</td>
			
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
			<td colspan="12" style="font-weight: bold; text-align: center; font-size: 14px;"> Total Geral: ${totalGeralMateriais} </td>
		</tr>
	</table>


	<p style="margin-top: 15px; margin-bottom: 15px;">
		<strong>*</strong> &nbsp;&nbsp;&nbsp; A quantidade de Títulos mostrada pode ser um pouco maior que a quantidade real, pois ela está 
		agrupada pelas informações dos seus materiais. 
		Assim, caso um título possua materiais em agrupamentos diferentes, ele será contado em duplicidade.  
	</p>


	<%-- Mostra um gráfico simples da variação da quantidade de materiais adicionados a cada mês --%>   

	
		
	<%-- <div style="width: 100%; font-size: 20px; text-align: center; margin-top: 30px;">
		<img src="/sigaa/servlet/DisplayChart?filename=${_abstractRelatorioBiblioteca.gerarGraficoCrescimento}" width="900" height="500" />
	</div> --%>
	
	<div style="text-align: center; margin: 10px;">

		<jsp:useBean id="dadosDoGrafico" scope="page"
				class="br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.GraficoRelatorioCrescimentoPeriodicosPorCNPqPeriodo"  />
		
		<cewolf:chart id="grafico" type="stackedverticalbar3d"
				xaxislabel="Área" yaxislabel="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares ? 'Exemplares' : 'Fascículos' }" showlegend="true">
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