<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/javascript/highchart/tags_highchart.jsp"%>
<a4j:keepAlive beanName="relatoriosDocenciaAssistidaMBean" />
<style>
	table.tabelaRelatorio tbody td {
		border-bottom: 1px solid black;
	}
</style>

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
	<c:forEach var="atividade" items="${ relatoriosDocenciaAssistidaMBean.listagem }" varStatus="loop">
		<c:set var="series" value="${series} 
			{
			 name: '${ ( empty atividade['0'] ? '* Outras' : atividade['0'])}',
			 data: [${atividade.total}] ${aux}			        			 
		   }
		"/>
		<c:if test="${loop.index < fn:length(relatoriosDocenciaAssistidaMBean.listagem) -1}">
		    <c:set var="series" value="${series},"/>
		</c:if>							
	</c:forEach>

	<br />
	
	<h2 style="width: 100%">Outras Atividades</h2>
	<table class="tabelaRelatorio" width="80%" align="center">
		<thead>
			<tr>
				<th align="left">Descrição</th>
				<th style="text-align: right">Quantidade</th>

			</tr>
		</thead>
		<tbody>
			<c:set var="total" value="0"/>
			<c:forEach var="atividade"
				items="${ relatoriosDocenciaAssistidaMBean.detalhe }">
				<tr>
					<td align="left">${ atividade['0']}</td>					
					<td style="text-align: right">${atividade.total}</td>
				</tr>
				<c:set var="total" value="${total + atividade.total}"/>
			</c:forEach>
			<tr class="linhaCinza">
				<td style="text-align: right">TOTAL:</td>					
				<td style="text-align: right">${total}</td>
			</tr>				
		</tbody>
	</table>
	
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
         text: 'Quantitativo de Atividades'
      },      
      xAxis: {
          categories: [
             'Atividades'             
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