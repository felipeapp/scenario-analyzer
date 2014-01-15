<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/javascript/highchart/tags_highchart.jsp"%>
<style>
	<!--
	.linkValor a {
	  text-decoration: underline;
	  color: #000000;
	  font-size: 11px;
	  font-weight: normal;
	}
	
	.totalGeral a {
		font-weight: bold;
	}
	-->
</style>
<f:view>
	<h:form>
	    <h2 class="tituloTabela"><b>Total de Unidades Acadêmicas por Tipo</b></h2>
	   	
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<thead>
				<tr>
					<th>Tipo da Unidade Acadêmica</th>
					<th style="text-align: right;width: 100px;">Total</th>					
				</tr>				
			</thead>
			<c:set var="Total" value="0"/>
			<c:set var="tipos" value=""/>
			<c:set var="totais" value=""/>
			<tbody>
				<c:forEach items="#{relatorioUnidadesAcademicasMBean.listagem}" varStatus="loop" var="linha" >   
				
					<c:set var="Total" value="${Total + linha.total}"/>
							 		
					<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: left;">
							${linha.descricao}
							<c:set var="tipos" value="${tipos}['${linha.descricao}',${linha.total}],"/>
						</td>						
						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.total > 0 }">
								<h:commandLink action="#{relatorioUnidadesAcademicasMBean.detalhar}">
									<ufrn:format type="valorint" valor="${ linha.total}"/>
									<f:param name="id" value="#{linha.id_tipo_unidade}"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.total == 0 }">
								<h:outputText value="0"/>
							</c:if>
						</td>										
					</tr>
				</c:forEach>	  
			</tbody>	
			<tr class="linhaCinza">
				<th style="text-align: right;">TOTAL GERAL</th>
				<th class="linkValor" style="text-align: right;">
					<c:if test="${Total > 0 }">
						<h:commandLink action="#{relatorioUnidadesAcademicasMBean.detalhar}">
							<ufrn:format type="valorint" valor="${ Total }"/>
						</h:commandLink>
					</c:if>
					<c:if test="${Total == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</th>				
			</tr>
		</table>	
		<br/>
		
		<div id="unidades" style="width: 450px; height: 450px; margin: 0 auto;"></div>
	
	</h:form>		
		
			
</f:view>
<script type="text/javascript">
 		J(document).ready(function() {
			var chart = new Highcharts.Chart({
				chart: {
					renderTo: 'unidades',
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false
				},
				title: {
					text: 'Unidades Acadêmicas'
				},
				tooltip: {
					formatter: function() {
						return '<b>'+ this.point.name +'</b>: '+ this.y;
					}
				},
				plotOptions: {
					pie: {
						allowPointSelect: true,
						cursor: 'pointer',
						dataLabels: {
							enabled: true,
							color: '#FFFFFF',
							formatter: function() {
								return this.y;
							}
						}
					}
				},			
				series: [{
					type: 'pie',
					data: [${tipos}]
				}]
			});
			
		});
</script>	

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>