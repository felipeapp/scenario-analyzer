<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>
<f:view>
<a4j:keepAlive beanName="relatoriosPlanejamento"/>
<style>
	.siglas { 
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
	
	.geral { 
		text-indent: 5px;  
		font-weight: bold;
		background: #dedfe3;
		text-align: right;		
	}	
	
	.body {
		font-size: 10;
	}	

    .linhaPar td { background-color: #f0f0F0; text-align: right;}
    .linhaImpar td { background-color: #fff; text-align: right;}
    
    .linhaPar th { background-color: #f0f0F0; text-align: right;}
    .linhaImpar th { background-color: #fff; text-align: right;}
    
    .linha td { text-decoration: underline; cursor: pointer;}	    	
</style>

<style media="print">
    .linha td { text-decoration: none !important;}
</style>
	
    <h:form id="form">
	<h2>${relatoriosPlanejamento.tituloRelatorio}</h2>
	
	<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Unidade:</th>
			<c:if test="${empty relatoriosPlanejamento.unidade.nome}">
				<td>TODOS</td>			
			</c:if>
			<c:if test="${!empty relatoriosPlanejamento.unidade.nome}">
				<td><h:outputText value="#{relatoriosPlanejamento.unidade.nome}"/></td>
			</c:if>			
		</tr>
		<tr>
			<th>Ano:</th>
			<td><h:outputText id="ano" value="#{relatoriosPlanejamento.ano}"/></td>
		</tr>
	</table>
	</div>
	</h:form>
	<br/>
	
	<div class="naoImprimir" align="center">
		<div class="descricaoOperacao" style="width: 50%">Clique sobre os números para ver o detalhamento.</div>
	</div>		
	
	<table class="tabelaRelatorioBorda">
		<thead class="body">
		    <tr>
		    	<th rowspan="3" style="text-align: center; width: 300px">UNIDADE/CURSO</th>
		    	<th style="text-align: center;" colspan="9">1º Semestre</th>
		    	<th style="text-align: center;" colspan="9">2º Semestre</th>
		    	<th style="text-align: center;" colspan="2" rowspan="2">Total</th>
		    	<th style="text-align: center;" rowspan="3">Total Geral</th>
		    </tr>
		    <tr>
		    	<c:forEach begin="1" end="2" step="1">
			    	<th style="text-align: center;" colspan="3">Diurno</th>
			    	<th style="text-align: center;" colspan="3">Noturno</th>
			    	<th style="text-align: center;" colspan="2">Total</th>
			    	<th style="text-align: center;" colspan="1" rowspan="2">Total Geral</th>
		    	</c:forEach>
		    </tr>
		    <tr>
		    	<c:forEach begin="1" end="2" step="1">
			    	<th style="text-align: center;">Masc</th>
			    	<th style="text-align: center;">Fem</th>
			    	<th style="text-align: center;">Total</th>
			    	<th style="text-align: center;">Masc</th>
			    	<th style="text-align: center;">Fem</th>
			    	<th style="text-align: center;">Total</th>
			    	<th style="text-align: center;">Masc</th>
			    	<th style="text-align: center;">Fem.</th>	  
		    	</c:forEach>
		    	<th style="text-align: center;">Masc</th>
		    	<th style="text-align: center;">Fem</th>	    		    	  	
		    </tr>	
	    </thead>    	    
	    <c:set var="modalidade" value=""/>
	    <c:set var="idModalidade" value="0"/>
	    <c:set var="sigla" value=""/>
	    <c:set var="idUnidade" value="0"/>	    	    	        	    

	    <tbody class="body">
		 	<c:forEach items="#{relatoriosPlanejamento.listagem}"  var="linha" varStatus="loop">
	 			<c:if test="${loop.first}">
					<tr>
						<th class="modalidade" style="text-align: center;" colspan="22"><b>${linha.modalidade}</b></th>
					</tr>			
					<c:set var="modalidade" value="${linha.modalidade}"/>
					<c:set var="idModalidade" value="${linha.idModalidade}"/> 	
					<c:set var="idUnidade" value="${linha.idUnidade}"/>	    
	 			</c:if>			 				
		 	
		 		<c:if test="${sigla != linha.sigla}">
		 			<c:if test="${!loop.first}">
						<tr class="linha siglas ${idUnidade} modalidade ${idModalidade}" style="border-bottom: 2px solid black;">
							<th><b>Total:</b></th>
							<td class="M_D_1">${TMD1}</td>
							<td class="F_D_1">${TFD1}</td>					
							<td class="G_D_1">${TD1}</td>
							<td class="M_N_1">${TMN1}</td>
							<td class="F_N_1">${TFN1}</td>
							<td class="N_1">${TN1}</td>
							<td class="M_1">${TM1}</td>
							<td class="F_1">${TF1}</td>					
							<td class="G_1">${TG1}</td>
							<td class="M_D_2">${TMD2}</td>
							<td class="F_D_2">${TFD2}</td>					
							<td class="G_D_2">${TD2}</td>
							<td class="M_N_2">${TMN2}</td>
							<td class="F_N_2">${TFN2}</td>
							<td class="N_2">${TN2}</td>
							<td class="M_2">${TM2}</td>
							<td class="F_2">${TF2}</td>
							<td class="G_2">${TG2}</td>
							<td class="M">${TM}</td>
							<td class="F">${TF}</td>
							<td class="G">${TG}</td>
							
						    <c:set var="MTMD1" value="${MTMD1 + TMD1}"/>
						    <c:set var="MTFD1" value="${MTFD1 + TFD1}"/>
						    <c:set var="MTD1" value="${MTD1 +  TD1}"/>
						    <c:set var="MTMN1" value="${MTMN1 + TMN1}"/>
						    <c:set var="MTFN1" value="${MTFN1 + TFN1}"/>
						    <c:set var="MTN1" value="${MTN1 + TN1}"/>
						    <c:set var="MTM1" value="${MTM1 + TM1}"/>
						    <c:set var="MTF1" value="${MTF1 + TF1}"/>
						    <c:set var="MTG1" value="${MTG1 + TG1}"/>
						    <c:set var="MTMD2" value="${MTMD2 + TMD2}"/>
						    <c:set var="MTFD2" value="${MTFD2 + TFD2}"/>
						    <c:set var="MTD2" value="${MTD2 + TD2}"/>
						    <c:set var="MTMN2" value="${MTMN2 + TMN2}"/>
						    <c:set var="MTFN2" value="${MTFN2 + TFN2}"/>
						    <c:set var="MTN2" value="${MTN2 + TN2}"/>
						    <c:set var="MTM2" value="${MTM2 + TM2}"/>
						    <c:set var="MTF2" value="${MTF2 +  TF2}"/>
						    <c:set var="MTG2" value="${MTG2 + TG2}"/>
						    <c:set var="MTM" value="${MTM + TM}"/>
						    <c:set var="MTF" value="${MTF + TF}"/>
						    <c:set var="MTG" value="${MTG + TG}"/>		

						    <c:set var="TMD1" value="0"/>
						    <c:set var="TFD1" value="0"/>
						    <c:set var="TD1" value="0"/>
						    <c:set var="TMN1" value="0"/>
						    <c:set var="TFN1" value="0"/>
						    <c:set var="TN1" value="0"/>
						    <c:set var="TM1" value="0"/>
						    <c:set var="TF1" value="0"/>
						    <c:set var="TG1" value="0"/>
						    <c:set var="TMD2" value="0"/>
						    <c:set var="TFD2" value="0"/>
						    <c:set var="TD2" value="0"/>
						    <c:set var="TMN2" value="0"/>
						    <c:set var="TFN2" value="0"/>
						    <c:set var="TN2" value="0"/>
						    <c:set var="TM2" value="0"/>
						    <c:set var="TF2" value="0"/>
						    <c:set var="TG2" value="0"/>
						    <c:set var="TM" value="0"/>
						    <c:set var="TF" value="0"/>
						    <c:set var="TG" value="0"/>								
						</tr>			 			
		 			</c:if>	 				 					 			
		 			<c:if test="${modalidade != linha.modalidade}">
						<tr class="linha modalidade ${idModalidade}">
							<th><b>Total (${modalidade}):</b></th>
							<td class="M_D_1">${MTMD1}</td>
							<td class="F_D_1">${MTFD1}</td>					
							<td class="G_D_1">${MTD1}</td>							
							<td class="M_N_1">${MTMN1}</td>
							<td class="F_N_1">${MTFN1}</td>
							<td class="N_1">${MTN1}</td>							
							<td class="M_1">${MTM1}</td>
							<td class="F_1">${MTF1}</td>					
							<td class="G_1">${MTG1}</td>							
							<td class="M_D_2">${MTMD2}</td>
							<td class="F_D_2">${MTFD2}</td>					
							<td class="G_D_2">${MTD2}</td>
							<td class="M_N_2">${MTMN2}</td>
							<td class="F_N_2">${MTFN2}</td>
							<td class="N_2">${MTN2}</td>
							<td class="M_2">${MTM2}</td>
							<td class="F_2">${MTF2}</td>
							<td class="G_2">${MTG2}</td>
							<td class="M">${MTM}</td>
							<td class="F">${MTF}</td>
							<td class="G">${MTG}</td>
						</tr>
						
						<tr><td colspan="22"><br/></td></tr>
						
						<tr>
							<th class="modalidade" style="text-align: center;" colspan="22"><b>${linha.modalidade}</b></th>
						</tr>		
						
					    <c:set var="TGMD1" value="${MTMD1 + TGMD1}"/>
					    <c:set var="TGFD1" value="${MTFD1 + TGFD1}"/>
					    <c:set var="TGD1" value="${MTD1 +  TGD1}"/>
					    <c:set var="TGMN1" value="${MTMN1 + TGMN1}"/>
					    <c:set var="TGFN1" value="${MTFN1 + TGFN1}"/>
					    <c:set var="TGN1" value="${MTN1 + TGN1}"/>
					    <c:set var="TGM1" value="${MTM1 + TGM1}"/>
					    <c:set var="TGF1" value="${MTF1 + TGF1}"/>
					    <c:set var="TGG1" value="${MTG1 + TGG1}"/>
					    <c:set var="TGMD2" value="${MTMD2 + TGMD2}"/>
					    <c:set var="TGFD2" value="${MTFD2 + TGFD2}"/>
					    <c:set var="TGD2" value="${MTD2 + TGD2}"/>
					    <c:set var="TGMN2" value="${MTMN2 + TGMN2}"/>
					    <c:set var="TGFN2" value="${MTFN2 + TGFN2}"/>
					    <c:set var="TGN2" value="${MTN2 + TGN2}"/>
					    <c:set var="TGM2" value="${MTM2 + TGM2}"/>
					    <c:set var="TGF2" value="${MTF2 +  TGF2}"/>
					    <c:set var="TGG2" value="${MTG2 + TGG2}"/>
					    <c:set var="TGM" value="${MTM + TGM}"/>
					    <c:set var="TGF" value="${MTF + TGF}"/>
					    <c:set var="TGG" value="${MTG + TGG}"/>								
						
					    <c:set var="MTMD1" value="0"/>
					    <c:set var="MTFD1" value="0"/>
					    <c:set var="MTD1" value="0"/>
					    <c:set var="MTMN1" value="0"/>
					    <c:set var="MTFN1" value="0"/>
					    <c:set var="MTN1" value="0"/>
					    <c:set var="MTM1" value="0"/>
					    <c:set var="MTF1" value="0"/>
					    <c:set var="MTG1" value="0"/>
					    <c:set var="MTMD2" value="0"/>
					    <c:set var="MTFD2" value="0"/>
					    <c:set var="MTD2" value="0"/>
					    <c:set var="MTMN2" value="0"/>
					    <c:set var="MTFN2" value="0"/>
					    <c:set var="MTN2" value="0"/>
					    <c:set var="MTM2" value="0"/>
					    <c:set var="MTF2" value="0"/>
					    <c:set var="MTG2" value="0"/>
					    <c:set var="MTM" value="0"/>
					    <c:set var="MTF" value="0"/>
					    <c:set var="MTG" value="0"/>																 			
		 			</c:if>							 		 				 			
		 			<c:set var="modalidade" value="${linha.modalidade}"/>	
		 			<c:set var="idModalidade" value="${linha.idModalidade}"/>	 					 			
					<tr>
						<th class="siglas" style="text-align: left;" colspan="22"><b>${linha.sigla}</b></th>
					</tr>			 		    					
		 			<c:set var="sigla" value="${linha.sigla}"/>
		 			<c:set var="idUnidade" value="${linha.idUnidade}"/>
		 		</c:if>	 		
	
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" } linha curso ${linha.idCurso} ">
					<th style="text-align: left;">${linha.nome}</th>																							
					<td class="M_D_1">${linha.DM1}</td>
					<td class="F_D_1">${linha.DF1}</td>					
					<td class="G_D_1">${linha.TD1}</td>
					<td class="M_N_1">${linha.NM1}</td>
					<td class="F_N_1">${linha.NF1}</td>
					<td class="N_1">${linha.TN1}</td>
					<td class="M_1">${linha.TM1}</td>
					<td class="F_1">${linha.TF1}</td>					
					<td class="G_1">${linha.TG1}</td>
					<td class="M_D_2">${linha.DM2}</td>
					<td class="F_D_2">${linha.DF2}</td>					
					<td class="G_D_2">${linha.TD2}</td>
					<td class="M_N_2">${linha.NM2}</td>
					<td class="F_N_2">${linha.NF2}</td>
					<td class="N_2">${linha.TN2}</td>
					<td class="M_2">${linha.TM2}</td>
					<td class="F_2">${linha.TF2}</td>
					<td class="G_2">${linha.TG2}</td>
					<td class="M">${linha.TM}</td>
					<td class="F">${linha.TF}</td>
					<td class="G">${linha.TG}</td>
					
				    <c:set var="TMD1" value="${TMD1 + linha.DM1}"/>
				    <c:set var="TFD1" value="${TFD1 + linha.DF1}"/>
				    <c:set var="TD1" value="${TD1 +  linha.TD1}"/>
				    <c:set var="TMN1" value="${TMN1 + linha.NM1}"/>
				    <c:set var="TFN1" value="${TFN1 + linha.NF1}"/>
				    <c:set var="TN1" value="${TN1 + linha.TN1}"/>
				    <c:set var="TM1" value="${TM1 + linha.TM1}"/>
				    <c:set var="TF1" value="${TF1 + linha.TF1}"/>
				    <c:set var="TG1" value="${TG1 + linha.TG1}"/>
				    <c:set var="TMD2" value="${TMD2 + linha.DM2}"/>
				    <c:set var="TFD2" value="${TFD2 + linha.DF2}"/>
				    <c:set var="TD2" value="${TD2 + linha.TD2}"/>
				    <c:set var="TMN2" value="${TMN2 + linha.NM2}"/>
				    <c:set var="TFN2" value="${TFN2 + linha.NF2}"/>
				    <c:set var="TN2" value="${TN2 + linha.TN2}"/>
				    <c:set var="TM2" value="${TM2 + linha.TM2}"/>
				    <c:set var="TF2" value="${TF2 +  linha.TF2}"/>
				    <c:set var="TG2" value="${TG2 + linha.TG2}"/>
				    <c:set var="TM" value="${TM + linha.TM}"/>
				    <c:set var="TF" value="${TF + linha.TF}"/>
				    <c:set var="TG" value="${TG + linha.TG}"/>					
				</tr>
			</c:forEach>  
			<tr class="linha siglas ${idUnidade}" style="border-bottom: 2px solid black;">
				<th><b>Total:</b></th>
				
				<td class="M_D_1">${TMD1}</td>
				<td class="F_D_1">${TFD1}</td>					
				<td class="G_D_1">${TD1}</td>
				<td class="M_N_1">${TMN1}</td>
				<td class="F_N_1">${TFN1}</td>
				<td class="N_1">${TN1}</td>
				<td class="M_1">${TM1}</td>
				<td class="F_1">${TF1}</td>					
				<td class="G_1">${TG1}</td>
				<td class="M_D_2">${TMD2}</td>
				<td class="F_D_2">${TFD2}</td>					
				<td class="G_D_2">${TD2}</td>
				<td class="M_N_2">${TMN2}</td>
				<td class="F_N_2">${TFN2}</td>
				<td class="N_2">${TN2}</td>
				<td class="M_2">${TM2}</td>
				<td class="F_2">${TF2}</td>
				<td class="G_2">${TG2}</td>
				<td class="M">${TM}</td>
				<td class="F">${TF}</td>
				<td class="G">${TG}</td>					
			</tr>
			<tr class="linha modalidade ${idModalidade}">
				<th><b>Total (${modalidade}):</b></th>
			    <c:set var="MTMD1" value="${MTMD1 + TMD1}"/>
			    <c:set var="MTFD1" value="${MTFD1 + TFD1}"/>
			    <c:set var="MTD1" value="${MTD1 +  TD1}"/>
			    <c:set var="MTMN1" value="${MTMN1 + TMN1}"/>
			    <c:set var="MTFN1" value="${MTFN1 + TFN1}"/>
			    <c:set var="MTN1" value="${MTN1 + TN1}"/>
			    <c:set var="MTM1" value="${MTM1 + TM1}"/>
			    <c:set var="MTF1" value="${MTF1 + TF1}"/>
			    <c:set var="MTG1" value="${MTG1 + TG1}"/>
			    <c:set var="MTMD2" value="${MTMD2 + TMD2}"/>
			    <c:set var="MTFD2" value="${MTFD2 + TFD2}"/>
			    <c:set var="MTD2" value="${MTD2 + TD2}"/>
			    <c:set var="MTMN2" value="${MTMN2 + TMN2}"/>
			    <c:set var="MTFN2" value="${MTFN2 + TFN2}"/>
			    <c:set var="MTN2" value="${MTN2 + TN2}"/>
			    <c:set var="MTM2" value="${MTM2 + TM2}"/>
			    <c:set var="MTF2" value="${MTF2 +  TF2}"/>
			    <c:set var="MTG2" value="${MTG2 + TG2}"/>
			    <c:set var="MTM" value="${MTM + TM}"/>
			    <c:set var="MTF" value="${MTF + TF}"/>
			    <c:set var="MTG" value="${MTG + TG}"/>		
			    
			    <c:set var="TGMD1" value="${MTMD1 + TGMD1}"/>
			    <c:set var="TGFD1" value="${MTFD1 + TGFD1}"/>
			    <c:set var="TGD1" value="${MTD1 +  TGD1}"/>
			    <c:set var="TGMN1" value="${MTMN1 + TGMN1}"/>
			    <c:set var="TGFN1" value="${MTFN1 + TGFN1}"/>
			    <c:set var="TGN1" value="${MTN1 + TGN1}"/>
			    <c:set var="TGM1" value="${MTM1 + TGM1}"/>
			    <c:set var="TGF1" value="${MTF1 + TGF1}"/>
			    <c:set var="TGG1" value="${MTG1 + TGG1}"/>
			    <c:set var="TGMD2" value="${MTMD2 + TGMD2}"/>
			    <c:set var="TGFD2" value="${MTFD2 + TGFD2}"/>
			    <c:set var="TGD2" value="${MTD2 + TGD2}"/>
			    <c:set var="TGMN2" value="${MTMN2 + TGMN2}"/>
			    <c:set var="TGFN2" value="${MTFN2 + TGFN2}"/>
			    <c:set var="TGN2" value="${MTN2 + TGN2}"/>
			    <c:set var="TGM2" value="${MTM2 + TGM2}"/>
			    <c:set var="TGF2" value="${MTF2 +  TGF2}"/>
			    <c:set var="TGG2" value="${MTG2 + TGG2}"/>
			    <c:set var="TGM" value="${MTM + TGM}"/>
			    <c:set var="TGF" value="${MTF + TGF}"/>
			    <c:set var="TGG" value="${MTG + TGG}"/>				    			
				
				<td class="M_D_1">${MTMD1}</td>
				<td class="F_D_1">${MTFD1}</td>					
				<td class="G_D_1">${MTD1}</td>							
				<td class="M_N_1">${MTMN1}</td>
				<td class="F_N_1">${MTFN1}</td>
				<td class="N_1">${MTN1}</td>							
				<td class="M_1">${MTM1}</td>
				<td class="F_1">${MTF1}</td>					
				<td class="G_1">${MTG1}</td>							
				<td class="M_D_2">${MTMD2}</td>
				<td class="F_D_2">${MTFD2}</td>					
				<td class="G_D_2">${MTD2}</td>
				<td class="M_N_2">${MTMN2}</td>
				<td class="F_N_2">${MTFN2}</td>
				<td class="N_2">${MTN2}</td>
				<td class="M_2">${MTM2}</td>
				<td class="F_2">${MTF2}</td>
				<td class="G_2">${MTG2}</td>
				<td class="M">${MTM}</td>
				<td class="F">${MTF}</td>
				<td class="G">${MTG}</td>
			</tr>				 		    			
			<tr class="linha geral">
				<th><b>Total da Instituição:</b></th>
				<td class="M_D_1">${TGMD1}</td>
				<td class="F_D_1">${TGFD1}</td>					
				<td class="G_D_1">${TGD1}</td>
				<td class="M_N_1">${TGMN1}</td>
				<td class="F_N_1">${TGFN1}</td>
				<td class="N_1">${TGN1}</td>
				<td class="M_1">${TGM1}</td>
				<td class="F_1">${TGF1}</td>					
				<td class="G_1">${TGG1}</td>
				<td class="M_D_2">${TGMD2}</td>
				<td class="F_D_2">${TGFD2}</td>					
				<td class="G_D_2">${TGD2}</td>
				<td class="M_N_2">${TGMN2}</td>
				<td class="F_N_2">${TGFN2}</td>
				<td class="N_2">${TGN2}</td>
				<td class="M_2">${TGM2}</td>
				<td class="F_2">${TGF2}</td>
				<td class="G_2">${TGG2}</td>
				<td class="M">${TGM}</td>
				<td class="F">${TGF}</td>
				<td class="G">${TGG}</td>
			</tr>				
		</tbody>
	</table>

<h:form id="auxform">
	<h:inputHidden id="valorLink" value="#{relatoriosPlanejamento.campoLink}"/>
	<h:inputHidden id="ano" value="#{relatoriosPlanejamento.ano}"/>
	<h:commandButton action="#{relatoriosPlanejamento.exibirRelatorioConcluintesDetalhes}"  style="display:none" id="botaoAcao"/>	
</h:form>

</f:view>


<script type="text/javascript">
	J(document).ready(function(){
		J(".linha > td").each(function(){
			if (J(this).html() == "0"){				
				J(this).css("text-decoration","none");
				J(this).css("cursor","default");
			}
		});
		 
		J(".linha > td").click(function(){
			if (J(this).html() != "0")
				exibirRelatorio(J(this));	 
			else				
				J(this).css("text-decoration","none");
		});		
	});

	function exibirRelatorio(obj) {
	   var valor1 = obj.attr("className");
	   var valor2 = obj.parent().attr("className");

	   valor2 = valor2.trim();	   
  
	   document.getElementById("auxform:valorLink").value = valor1 + '&' + valor2;	      	
	   document.getElementById("auxform:botaoAcao").click();
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>