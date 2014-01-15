<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/javascript/highchart/tags_highchart.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<style>
	<!--
	.linkValor a {
	  text-decoration: underline;
	  color: #000000;
	  font-size: 11px;
	  font-weight: normal;
	}
	-->
</style>
<f:view>
	<a4j:keepAlive beanName="relatorioAnaliseDiscentesPorIndiceMBean"/>
	
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano-Período:</th>
				<td>
					${relatorioAnaliseDiscentesPorIndiceMBean.ano}.${relatorioAnaliseDiscentesPorIndiceMBean.periodo}			
				</td>
			</tr>	 
			
			<c:if test="${relatorioAnaliseDiscentesPorIndiceMBean.nivel > 0}">
				<tr>
					<th>Nível: </th>
					<td>
						${relatorioAnaliseDiscentesPorIndiceMBean.nivel}			
					</td>		
				</tr>
			</c:if>			
					
			<c:if test="${relatorioAnaliseDiscentesPorIndiceMBean.curso.id > 0}">
				<tr>
					<th>Curso: </th>
					<td>
						${relatorioAnaliseDiscentesPorIndiceMBean.curso.descricao}			
					</td>		
				</tr>
			</c:if>				  

			<tr>
				<th>Índice Acadêmico: </th>
				<td>
					${relatorioAnaliseDiscentesPorIndiceMBean.indice.nome}			
				</td>		
			</tr>	
	
		</table>
	</div>	
	
	<h:form>
	    <h2 class="tituloTabela"><b>Análise de Discentes por Índice Acadêmico</b></h2>
	   	<br/>
		<table class="tabelaRelatorioBorda" align="center" style="width: 50%">	
			<tr class="linhaCinza">
				<th>Faixas</th>
				<th style="text-align: right;width: 70px;">Total</th>
				<th style="text-align: right;width: 70px;">%</th>											
			</tr>				
			<c:set var="total" value="0"/>
			<tbody>
			
				<c:set var="label" value=""/>
				<c:set var="valor" value=""/>
								
				<c:forEach items="#{relatorioAnaliseDiscentesPorIndiceMBean.valoresFaixa}" varStatus="loop" var="linha" >   

					<c:set var="label" value="${label} '${linha.faixa}'"/>
					<c:set var="valor" value="${valor} ${linha.valor}"/>
					<c:if test="${loop.index < fn:length(relatorioAnaliseDiscentesPorIndiceMBean.valoresFaixa)-1}">
						<c:set var="label" value="${label},"/>
						<c:set var="valor" value="${valor},"/>
					</c:if>
					
					<c:set var="total" value="${total + linha.valor}"/>
				
					<tr>
						<td style="text-align: left;">
							${linha.faixa}
						</td>						

						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.valor > 0 }">
								<h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.detalhar}">
									<ufrn:format type="valorint" valor="${ linha.valor }"/>
									<f:param name="index" value="#{linha.posicaoFaixa}"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.valor == 0 }">
								<h:outputText value="0"/>
							</c:if>
						</td>	
						
						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.valor > 0 }">
								<h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.detalhar}">
									<fmt:formatNumber pattern="#0.00" value="${ linha.percentual }"/>
									<f:param name="index" value="#{linha.posicaoFaixa}"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.valor == 0 }">
								<h:outputText value="0"/>
							</c:if>												
						</td>
					</tr>
				</c:forEach>	  
			</tbody>	
			<tr class="linhaCinza">
				<th style="text-align: right;">TOTAL GERAL:</th>
				<td style="text-align: right;" class="linkValor">
					<c:if test="${total > 0 }">
						<h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.detalhar}">
							<ufrn:format type="valorint" valor="${ total }"/>
							<f:param name="index" value="-1"/>
						</h:commandLink>
					</c:if>
					<c:if test="${total == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</td>
				<td style="text-align: right;" class="linkValor">
					<c:if test="${total > 0 }">
						<h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.detalhar}">
							100%
							<f:param name="index" value="-1"/>
						</h:commandLink>
					</c:if>
					<c:if test="${total == 0 }">
						<h:outputText value="0"/>
					</c:if>									
				</td>			
			</tr>
		</table>	
		<br/>		
		
		<div id="indices" style="width: 600px; height: 400px; margin: 0 auto;"></div>
		
	</h:form>
	
	
<script>

J(document).ready(function() {
	var chart = new Highcharts.Chart({
      chart: {
         renderTo: 'indices',
         defaultSeriesType: 'column'
      },
      title: {
         text: 'Histograma'
      },      
      xAxis: {
         categories: [${label}]
      },
      yAxis: {
         min: 0,
         title: {
            text: 'Total de Discentes'
         }
      },
      tooltip: {
         formatter: function() {
            return ''+this.y+' '+ this.series.name;
         }
      },
      legend: {
    	  enabled: false
      },
      credits: {
         enabled: false
      },
		series: [{
			name: 'Discentes',
			data: [${valor}]
	
		}]
   });   
});

</script>
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>