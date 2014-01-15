<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/javascript/highchart/tags_highchart.jsp"%>
<a4j:keepAlive beanName="relatoriosDocenciaAssistidaMBean" />

<f:view>

	<h2>${relatoriosDocenciaAssistidaMBean.tituloRelatorio}</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<c:if test="${relatoriosDocenciaAssistidaMBean.unidade != null && relatoriosDocenciaAssistidaMBean.unidade.id > 0}">
				<tr>
					<th>Programa:</th>
					<td>${relatoriosDocenciaAssistidaMBean.unidade.nome}</td>
				</tr>
			</c:if>
			<c:if test="${relatoriosDocenciaAssistidaMBean.modalidadeBolsa != null && relatoriosDocenciaAssistidaMBean.modalidadeBolsa.id > 0}">
				<tr>
					<th>Modalidade da Bolsa:</th>
					<td>${relatoriosDocenciaAssistidaMBean.modalidadeBolsa.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${!empty relatoriosDocenciaAssistidaMBean.descricaoNivel}">
				<tr>
					<th>Nível:</th>
					<td>${relatoriosDocenciaAssistidaMBean.descricaoNivel}</td>
				</tr>
			</c:if>			
			<tr>
				<th>Ano-Período:</th>
				<td><h:outputText value="#{relatoriosDocenciaAssistidaMBean.ano}"/>.
					<h:outputText value="#{relatoriosDocenciaAssistidaMBean.periodo}"/></td>
			</tr>
		</table>
	</div>	
	
	<br />
	
	<div id="grafico" style="width: 650px; height: 500px; margin: 0 auto;"></div>
	
	<br />
	
	
	<c:set var="series" value=""/>
	<c:set var="aux" value=",
	           dataLabels: {
	            enabled: true,
	            rotation: -90,
	            color: '#FFFFFF',
	            align: 'right',
	            x: -2,
	            y: 1,
	            formatter: function() {
	               return this.y;
	            },
	            style: {
	               font: 'normal 12px Verdana, sans-serif'
	            }
	         } "/>
	<c:forEach var="item" items="${ relatoriosDocenciaAssistidaMBean.listagem }" varStatus="loop">
		<c:set var="series" value="${series} 
			{
			 name: '${ item.status }',
			 data: [${item.total}] ${aux}			        			 
		   }
		"/>
		<c:if test="${loop.index < fn:length(relatoriosDocenciaAssistidaMBean.listagem) -1}">
		    <c:set var="series" value="${series},"/>
		</c:if>							
	</c:forEach>
	
</f:view>

<script type="text/javascript">

J(document).ready(function() {
	var chart = new Highcharts.Chart({
      chart: {
         renderTo: 'grafico',
         defaultSeriesType: 'column',
         margin: [ 20, 20, 220, 80]               
      },
      title: {
         text: 'Quantitativo por Status'
      },      
      xAxis: {
          categories: [
             'Status'             
          ]
       },
      yAxis: {
         min: 0,
         title: {
            text: 'Total de Planos'
         }
      },
      tooltip: {
          formatter: function() {
             return '<b>'+ this.series.name +'</b><br/>'+
                 'Total: '+ Highcharts.numberFormat(this.y, 0);
          }
       },
       plotOptions: {
          column: {
             pointPadding: 0.2,
             borderWidth: 0
          }
       },      
      legend: {
           layout: 'vertical',
           align: 'right',
           verticalAlign: 'top',
           x: -100,
           y: 0,
           floating: true,
           borderWidth: 1,
           shadow: true       
       },
      credits: {
         enabled: false
      },
		series: [${series}]
   });   
});
	
</script>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>