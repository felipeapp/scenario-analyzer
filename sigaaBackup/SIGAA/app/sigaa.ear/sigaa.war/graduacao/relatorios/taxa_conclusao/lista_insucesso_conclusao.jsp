<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@include file="/javascript/highchart/tags_highchart.jsp"%>
<style>
	.sigla { 
		border:1px black solid;
		text-indent: 5px;  
		font-weight: bold;
		background: #dedfe3;
		text-align: right;
	}
	
	.modalidade { 
		border:1px black solid;
		text-indent: 5px;  
		font-weight: bold;
		background: #ccc;	
		text-align: right;	
	}	
		
    .linhaPar td { background-color: #f0f0F0; text-align: right;}
    .linhaImpar td { background-color: #fff; text-align: right;}
    
    .linhaPar th { background-color: #f0f0F0; text-align: right;}
    .linhaImpar th { background-color: #fff; text-align: right;}
    
    .linha td { text-decoration: underline; cursor: pointer;}
    
    .total td { text-decoration: underline; cursor: pointer;}	      	    	
</style>
<f:view>

	<h:form>
    <h2 class="tituloTabela"><b>Relatório Analítico de Acontecimentos por Ano de Entrada</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano de Entrada:</th>
				<td>${relatorioTaxaConclusao.anoBase}</td>
			</tr>
			<tr>
				<th>Ano de Referência para Conclusão:</th>
				<td>
					${relatorioTaxaConclusao.anoBase + relatorioTaxaConclusao.intervaloContablizacao} 			
				</td>
			</tr>	   
		</table>
	</div>

	<div class="naoImprimir" align="center">
		<div class="descricaoOperacao" style="width: 50%">Clique sobre os números para ver o detalhamento.</div>
	</div>	
	<br/>
	<div>
		<p style="font-weight: bold">Legendas:</p>
		<b>ENT:</b> Entrada;
		<b>INT:</b> Integralizados;
		<b>INTAN:</b> Integralizados em Anos Anteriores; 
		<b>TS:</b> Total de Sucesso;
		<b>DES:</b> Desistências;
		<b>AT:</b> Ativos; 
		<b>OF:</b> Outras Formas;
		<b>TSUC:</b> Taxa de Sucesso;
		<b>TINS:</b> Taxa de Insucesso.		 
	</div>
	<br/>
	<c:set var="centros" value=""/>
	<c:set var="sucesso" value=""/>
	<c:set var="insucesso" value=""/>
	
	<c:set var="valorSucesso" value=""/>
	<c:set var="valorInsucesso" value=""/>
	
	<c:set var="idUnidade" value="0"/>	
    <c:set var="sigla" value=""/>
    <c:set var="TES" value="0"/>
    <c:set var="TSS" value="0"/>
    <c:set var="TASS" value="0"/>
    <c:set var="TDS" value="0"/>
    <c:set var="TMS" value="0"/>
    <c:set var="TOS" value="0"/>
    <c:set var="STS" value="0"/>
    
    <c:set var="TGE" value="0"/>
    <c:set var="TGS" value="0"/>
    <c:set var="TGAS" value="0"/>
    <c:set var="TGD" value="0"/>
    <c:set var="TGM" value="0"/>
    <c:set var="TGO" value="0"/>
    <c:set var="STGS" value="0"/>      
	<table class="tabelaRelatorioBorda" align="center">			
			<c:forEach items="#{relatorioTaxaConclusao.listaInsucesso}" varStatus="loop" var="linha">				    
		 		<c:if test="${sigla != linha.sigla}">
		 			<c:if test="${!loop.first}">
						<tr class="sigla total" id="${idUnidade}" style="border-bottom: 2px solid black;">
							<th style="text-align: right;"><b>Total:</b></th>
							<td class="entrada">${TES}</td>
							<td class="saida">${TSS}</td>
							<td class="antesSaida">${TASS}</td>
							<td class="sucesso">${STS}</td>						
							<td class="desistencia">${TDS}</td>
							<td class="matriculados">${TMS}</td>
							<td class="outros">${TOS}</td>
							<td class="percentual">
								<c:if test="${TES > 0}">
									<c:set var="valorSucesso" value="#{((TSS + TASS)/ TES) * 100}"/>
									<fmt:formatNumber pattern="#0.00" value="${valorSucesso}"/>%
								</c:if>
								<c:if test="${TES <= 0}">
									<c:set var="valorSucesso" value="0"/>
									<h:outputText value="0.00%"/>
								</c:if>
							</td>
							<td class="percentual">
								<c:if test="${TES > 0}">
									<c:set var="valorInsucesso" value="#{100 - (((TSS + TASS) / TES) * 100)}"/>
									<fmt:formatNumber pattern="#0.00" value="${valorInsucesso}"/>%
								</c:if>
								<c:if test="${TES <= 0}">
									<c:set var="valorInsucesso" value="0"/>
									<h:outputText value="0.00%"/>
								</c:if>					
							</td>								
						</tr>
 					    <c:set var="sucesso" value="${sucesso}${valorSucesso},"/>
					    <c:set var="insucesso" value="${insucesso}${valorInsucesso},"/>							
						
					    <c:set var="TES" value="0"/>
					    <c:set var="TSS" value="0"/>
					    <c:set var="TASS" value="0"/>					    
					    <c:set var="TDS" value="0"/>
					    <c:set var="TMS" value="0"/>
					    <c:set var="TOS" value="0"/>	
					    <c:set var="STS" value="0"/>					
					</c:if>						
					<thead>
					<tr>
						<th style="text-align: left;width: 450px;">Curso</th>
						<th style="text-align: right;width: 80px;">ENT ${relatorioTaxaConclusao.anoBase}</th>
						<th style="text-align: right;width: 80px;">INT</th>
						<th style="text-align: right;width: 80px;">INTAN</th>
						<th style="text-align: right;width: 80px;">TS</th>
						<th style="text-align: right;width: 80px;">DES</th>
						<th style="text-align: right;width: 80px;">AT</th>
						<th style="text-align: right;width: 80px;">OF</th>
						<th style="text-align: right;width: 80px;">% SUC</th>
						<th style="text-align: right;width: 80px;">% INS</th>				
					</tr>	
					</thead>				
					<tr class="sigla">
						<th style="text-align: center;" colspan="10"><b>${linha.sigla}</b></th>
					</tr>			 		    					
		 			<c:set var="sigla" value="${linha.sigla}"/>		 		
		 			<c:set var="idUnidade" value="${linha.idUnidade}"/>					 			
					<c:set var="centros" value="${centros}'${linha.sigla}',"/>	 			 							
				</c:if>
				
			    <c:set var="TES" value="${TES + linha.entrada}"/>
			    <c:set var="TSS" value="${TSS + linha.saida}"/>
			    <c:set var="TASS" value="${TASS + linha.saidaAntesPrevisto}"/>
			    			    
			    <c:set var="TDS" value="${TDS + linha.desistencia}"/>
			    <c:set var="TMS" value="${TMS + linha.matriculados}"/>
			    <c:set var="TOS" value="${TOS + linha.outros}"/>
			    <c:set var="STS" value="${STS + (linha.saidaAntesPrevisto + linha.saida)}"/>
			     
			    <c:set var="TGE" value="${TGE + linha.entrada}"/>
			    <c:set var="TGS" value="${TGS + linha.saida}"/>
			    <c:set var="TGAS" value="${TGAS + linha.saidaAntesPrevisto}"/>
			    <c:set var="TGD" value="${TGD + linha.desistencia}"/>
			    <c:set var="TGM" value="${TGM + linha.matriculados}"/>
			    <c:set var="TGO" value="${TGO + linha.outros}"/>			    
			    <c:set var="STGS" value="${STGS + (linha.saidaAntesPrevisto + linha.saida)}"/>			    			    				
			
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" } linha" id="${linha.curso.id}">
					<th style="text-align: left; font-weight: normal;">${linha.curso.descricao}</th>
					<td class="entrada" style="text-align: right;">${linha.entrada}</td>
					<td class="saida" style="text-align: right;">${linha.saida}</td>
					<td class="antesSaida" style="text-align: right;">${linha.saidaAntesPrevisto}</td>
					<td class="sucesso" style="text-align: right;">${linha.saida + linha.saidaAntesPrevisto}</td>
					<td class="desistencia" style="text-align: right;">${linha.desistencia}</td>
					<td class="matriculados" style="text-align: right;">${linha.matriculados}</td>
					<td class="outros" style="text-align: right;">${linha.outros}</td>
					<td class="percentual" style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${linha.taxaSucesso}"/>%</td>
					<td class="percentual" style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${linha.taxaInsucesso}"/>%</td>
				</tr>
			</c:forEach>
			
			<tr class="sigla total" id="${idUnidade}" style="border-bottom: 2px solid black;">
				<th style="text-align: right;"><b>Total:</b></th>
				<td class="entrada">${TES}</td>
				<td class="saida" >${TSS}</td>
				<td class="antesSaida">${TASS}</td>
				<td class="sucesso">${STS}</td>
				<td class="desistencia" >${TDS}</td>
				<td class="matriculados" >${TMS}</td>
				<td class="outros" >${TOS}</td>
				<td class="percentual">
					<c:if test="${TES > 0}">
						<c:set var="valorSucesso" value="#{((TSS + TASS) / TES) * 100}"/>
						<fmt:formatNumber pattern="#0.00" value="${valorSucesso}"/>%
					</c:if>
					<c:if test="${TES <= 0}">
						<c:set var="valorSucesso" value="0"/>
						<h:outputText value="0.00%"/>
					</c:if>
				</td>
				<td class="percentual">
					<c:if test="${TES > 0}">
						<c:set var="valorInsucesso" value="#{100 - (((TSS + TASS) / TES) * 100)}"/>
						<fmt:formatNumber pattern="#0.00" value="${valorInsucesso}"/>%
					</c:if>
					<c:if test="${TES <= 0}">
						<c:set var="valorInsucesso" value="0"/>
						<h:outputText value="0.00%"/>
					</c:if>					
				</td>		
			</tr>
			
			<c:set var="centros" value="${centros}'${sigla}'"/>
			<c:set var="sucesso" value="${sucesso}${valorSucesso}"/>
			<c:set var="insucesso" value="${insucesso}${valorInsucesso}"/>				
				
			<tr class="modalidade total" id="0">
				<th style="text-align: right;"><b>Total Geral:</b></th>
				<td class="entrada">${TGE}</td>
				<td class="saida">${TGS}</td>
				<td class="antesSaida">${TGAS}</td>
				<td class="sucesso">${STGS}</td>
				<td class="desistencia">${TGD}</td>
				<td class="matriculados">${TGM}</td>
				<td class="outros">${TGO}</td>
				<td class="percentual">
					<c:if test="${TGE > 0}">
						<c:set var="valorSucesso" value="#{((TGS + TGAS) / TGE) * 100}"/>
						<fmt:formatNumber pattern="#0.00" value="${valorSucesso}"/>%
					</c:if>
					<c:if test="${TGE <= 0}">
						<c:set var="valorSucesso" value="0"/>
						<h:outputText value="0.00%"/>
					</c:if>
				</td>
				<td class="percentual">
					<c:if test="${TGE > 0}">
						<c:set var="valorInsucesso" value="#{100 - (((TGS + TGAS) / TGE) * 100)}"/>
						<fmt:formatNumber pattern="#0.00" value="${valorInsucesso}"/>%
					</c:if>
					<c:if test="${TGE <= 0}">
						<c:set var="valorInsucesso" value="0"/>
						<h:outputText value="0.00%"/>
					</c:if>					
				</td>					
			</tr>							
	</table>
	<br/>
	<div>
		<p style="font-weight: bold">Legendas:</p>
		<b>ENT:</b> Entrada;
		<b>INT:</b> Integralizados;
		<b>INTAN:</b> Integralizados em Anos Anteriores; 
		<b>TS:</b> Total de Sucesso;
		<b>DES:</b> Desistências;
		<b>AT:</b> Ativos; 
		<b>OF:</b> Outras Formas;
		<b>TSUC:</b> Taxa de Sucesso;
		<b>TINS:</b> Taxa de Insucesso.		 
	</div>	
	<br/>
	<div id="container" style="width: 700px; height: 500px; margin: 0 auto;"></div>
	
	</h:form>
	
	<h:form id="auxform">
		<h:inputHidden id="valorLink" value="#{relatorioTaxaConclusao.campoLink}"/>
		<h:inputHidden id="idCurso" value="#{relatorioTaxaConclusao.idCurso}"/>
		<h:inputHidden id="idUnidade" value="#{relatorioTaxaConclusao.idUnidade}"/>
		<h:commandButton action="#{relatorioTaxaConclusao.exibirDetalhesInsucesso}"  style="display:none" id="botaoAcao"/>	
	</h:form>	
</f:view>

<script type="text/javascript">
	J(document).ready(function(){
		/* Criação da Chamada do relatório ao clicar sobre o número */
		function limparLink(obj){
			if (obj.html() == "0" || obj.attr("class") == ("percentual")){				
				obj.css("text-decoration","none");
				obj.css("cursor","default");
			}		
		}

		function exibirRelatorio(obj) {
		   var valor1 = obj.attr("className");
		   var valor2 = obj.parent().attr("id");
		   var tipo = obj.parent().attr("className");

		   document.getElementById("auxform:valorLink").value = valor1;
		   
		   if (tipo.indexOf("total") > 0){	 		
			  document.getElementById("auxform:idUnidade").value = valor2;
			  document.getElementById("auxform:idCurso").value = 0;
		   } else {
		   	  document.getElementById("auxform:idCurso").value = valor2;
  		   	  document.getElementById("auxform:idUnidade").value = 0;
		   }
		       	
		   document.getElementById("auxform:botaoAcao").click();
		}		 

		function clickLink(obj){
			if (obj.html() != "0" && obj.attr("class") != ("percentual")){	
				exibirRelatorio(obj);	 
			}else				
				obj.css("text-decoration","none");
		}
		
		J(".linha > td").each(function(){
			limparLink(J(this));
		});

		J(".total > td").each(function(){
			limparLink(J(this));
		});
				 
		J(".linha > td").click(function(){			
			clickLink(J(this));
		});

		J(".total > td").click(function(){			
			clickLink(J(this));
		});				

		/* Geração do Gráfico */
		var insucesso = '${insucesso}';
		insucesso = insucesso.split(",");
		var valInsucesso = new Array(insucesso.length);
		
		for (var i = 0; i <= insucesso.length -1; i++){
			var num = parseFloat(insucesso[i]);
			valInsucesso[i] = parseFloat(num.toFixed(2));
		}

		var sucesso = '${sucesso}';
		sucesso = sucesso.split(",");	
		var valSucesso = new Array(sucesso.length);
		
		for (var i = 0; i <= sucesso.length -1; i++){
			var num = parseFloat(sucesso[i]);
			valSucesso[i] = parseFloat(num.toFixed(2));
		}

		var chart = new Highcharts.Chart({
			   chart: {
			      renderTo: 'container',
			      defaultSeriesType: 'column'
			   },
			   title: {
			      text: 'Sucesso x Insucesso'
			   },
			   xAxis: {
			      categories: [${centros}]
			   },
			   yAxis: {
			      min: 0,
			      title: {
			         text: 'Percentual (%)'
			      }
			   },
			   legend: {
			      layout: 'vertical',
			      backgroundColor: '#FFFFFF',
			      style: {
			         left: '100px',
			         top: '70px',
			         bottom: 'auto'
			      }
			   },
			   tooltip: {
			      formatter: function() {
			         return '<b>'+ this.series.name +'</b><br/>'+
			            this.x +': '+ this.y +' %';
			      }
			   },
			   plotOptions: {
			      column: {
			         pointPadding: 0.2,
			         borderWidth: 0
			      }
			   },
			        series: [{
			      name: 'Sucesso',
			      data: valSucesso

			   }, {
			      name: 'Insucesso',
			      data: valInsucesso

			   }]
			});
		
	});
</script>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>