<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/javascript/highchart/tags_highchart.jsp"%>
<f:view>
	<h:form>
    <h2 class="tituloTabela"><b>Relatório de Taxa de Conclusão</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano Início:</th>
				<td>
					${relatorioTaxaConclusao.ano} 			
				</td>
			</tr>	   
			<tr>
				<th>Ano Fim:</th>
				<td>
					${relatorioTaxaConclusao.anoFim} 			
				</td>
			</tr>	   
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" align="center">	
		<thead>
			<tr>
				<th style="text-align: center;width: 100px;">Ano/Período Conclusão</th>
				<th style="text-align: right;width: 80px;">Concluintes</th>
				<th style="text-align: center; width: 100px;">Ano/Período Entrada</th>
				<th style="text-align: right;width: 80px;">Ingressantes</th>
				<th style="text-align: right;width: 100px;">Totais (Concluintes &divide; Ingressantes)</th>
				<th style="text-align: right;width: 100px;">Taxa Anual (%)</th>
			</tr>
		</thead>
			
		<tbody>
			<c:set var="anos" value=""/>
			<c:set var="concluintes" value=""/>
			<c:set var="ingressantes" value=""/>
			<c:set var="taxaConclusao" value=""/>
			<c:forEach items="#{relatorioTaxaConclusao.taxaConclusao}" varStatus="loop" var="linha" >
				<c:if test="${linha.linhas > 0}">	
					<tr>
				    	<th colspan="6" style="text-align: center; background-color:#DEDFE3;">${linha.ano}</th>
				    </tr>
				    								    
					<c:set var="anos" value="${anos}${linha.ano}"/>
					<c:set var="concluintes" value="${concluintes} ${linha.concluintesAnual}"/>
					<c:set var="ingressantes" value="${ingressantes} ${linha.ingressantesAnual}"/>
					<c:set var="taxaConclusao" value="${taxaConclusao} ${linha.taxaAnual}"/>
					<c:if test="${loop.index < fn:length(relatorioTaxaConclusao.taxaConclusao) -2}">
					    <c:set var="anos" value="${anos},"/>
					    <c:set var="concluintes" value="${concluintes},"/>
					    <c:set var="ingressantes" value="${ingressantes},"/>
					    <c:set var="taxaConclusao" value="${taxaConclusao},"/>
					</c:if>				    
				</c:if>		     		
				<tr>
					<td style="text-align: center;">
						<b>${linha.ano}.${linha.semestre}</b>
					</td>
					<td style="text-align: right; text-decoration: underline;">
						<h:commandLink action="#{relatorioTaxaConclusao.gerarRelatorioConcluintes}" value="#{linha.concluintes}">
							<f:param name="ano" value="#{linha.ano}" />
						</h:commandLink>
					</td>
					<td style="text-align: center;">
						<b>${linha.anoIngresso}.${linha.semestreIngresso}</b>
					</td>					
					<td style="text-align: right; text-decoration: underline;">
						<h:commandLink action="#{ relatorioTaxaConclusao.listaVagasOfertadas }" value="#{linha.ingressantes}">
							<f:param name="ano" value="#{linha.anoIngresso}" />
						</h:commandLink>
					</td>
					<c:if test="${linha.linhas > 0}">		
						<td style="text-align: right;" rowspan="${linha.linhas}">
							 ${linha.concluintesAnual} &divide; ${linha.ingressantesAnual}
						</td>
					</c:if>														
					<c:if test="${linha.linhas > 0}">		
						<td style="text-align: right; text-decoration: underline;" rowspan="${linha.linhas}">
							<h:commandLink action="#{relatorioTaxaConclusao.detalhamentoTaxaConclusao}">
								<f:param name="ano" value="#{linha.ano}" />
							 	<fmt:formatNumber pattern="#0.00" value="${linha.taxaAnual}"/>%
							</h:commandLink>
						</td>
					</c:if>
				</tr>
			</c:forEach>				
		</tbody>		
	</table>
	
	<br/>
				
	<div id="taxa" style="width: 700px; height: 400px; margin: 0 auto;"></div>
				
	</h:form>
</f:view>
<script type="text/javascript">
	/*	J(document).ready(function() {
			var chart = new Highcharts.Chart({
				chart: {
					renderTo: 'concluintes',
					defaultSeriesType: 'line',
					margin: [50, 150, 60, 80]
				},				
				tooltip: {
					formatter: function() {
			                return '<b>'+ this.series.name +'</b><br/>'+
							this.x +': '+ this.y;
					}
				},
				legend: {
					layout: 'vertical',
					style: {
						left: 'auto',
						bottom: 'auto',
						right: '10px',
						top: '100px'
					}
				},				
				title: {
					text: 'Concluintes x Ingressantes',
					style: {
						margin: '10px 100px 0 0' // center it
					}
				},				
				xAxis: {
					categories: [${anos}],
						title: {
							text: 'Ano'
						}
				},	
				yAxis: {
					title: {
						text: 'Concluintes x Ingressantes'
					},
					plotLines: [{
						value: 0,
						width: 1,
						color: '#808080'
					}]
				},				
				series: [{
					name: 'Concluintes',
					data: [${concluintes}]
			
				}, {
					name: 'Ingressantes',
					data: [${ingressantes}]
				}]
			});
*/
			var chartTaxa = new Highcharts.Chart({
				chart: {
					renderTo: 'taxa',
					defaultSeriesType: 'line',
					margin: [50, 150, 60, 80]
				},				
				tooltip: {
					formatter: function() {
			                return '<b>'+ this.series.name +'</b><br/>'+
							this.x +': '+ this.y+'%';
					}
				},
				legend: {
					layout: 'vertical',
					style: {
						left: 'auto',
						bottom: 'auto',
						right: '10px',
						top: '100px'
					}
				},				
				title: {
					text: 'Taxa de Conclusão',
					style: {
						margin: '10px 100px 0 0' // center it
					}
				},				
				xAxis: {
					categories: [${anos}],
						title: {
							text: 'Ano'
						}
				},	
				yAxis: {
					title: {
						text: 'Taxa de Conclusão (%)'
					},
					plotLines: [{
						value: 0,
						width: 1,
						color: '#808080'
					}]
				},							
				series: [{
					name: 'Taxa de <br/>Conclusão',
					data: [${taxaConclusao}]
			
				}]
			});			
</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	