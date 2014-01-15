<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Relatório de Vagas Ofertadas</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Ano:</th>
				<td> <h:outputText value="#{relatorioTaxaConclusao.ano}" /></td>
			</tr>
		</table>
	</div>
	
	<br />
	
    <c:set var="_ano" />
    <c:set var="_total" value="0"/>
    <c:set var="totalParcial1" value="0"/>
    <c:set var="totalParcial2" value="0"/>
    <c:set var="total1" value="0"/>
    <c:set var="total2" value="0"/>
    <c:set var="ingresso" value="0"/>
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="header">		
			<td align="left" rowspan="2"> Nome do Curso</td>
			<td align="left" rowspan="2"> Departamento</td>
			<td align="center" rowspan="2"> Turno</td>
			<td colspan="2" align="center" > Vagas </td>
		</tr>		
		<tr class="header">
			<td align="right" nowrap="nowrap"> ${relatorioTaxaConclusao.ano}.1</td>
			<td align="right" nowrap="nowrap"> ${relatorioTaxaConclusao.ano}.2</td>		
		</tr>
		<c:forEach items="#{relatorioTaxaConclusao.lista}" var="linha" varStatus="indice">
			
			<c:if test="${ingresso != linha.id_forma_ingresso}">
			
				<c:if test="${!indice.first}">				
					
				    <tr class="componentes">
				    	<td colspan="3" style="text-align: right;"><b>Total</b></td>
				    	<td style="text-align: right;"><b>${totalParcial1}</b></td>
				    	<td style="text-align: right;"><b>${totalParcial2}</b></td>
				    	
			    		<c:set var="totalParcial1" value="0"/>
			    		<c:set var="totalParcial2" value="0"/>				    	
				    </tr>			
					<tr class="espaco">
						<td colspan="6">&nbsp;</td>
					</tr>		
							    	
				</c:if>
							    	
				<tr class="header">
					<td colspan="6" style="font-size: 11px; text-align: center; padding: 5px;">${linha.forma_descricao}</td>
				</tr>
				
			</c:if>
			
			<c:set var="ingresso" value="${linha.id_forma_ingresso}"/>			
		
			<tr class="componentes">				
				<td align="left">  ${linha.nome}</td>
				<td align="left">  ${linha.unidade_sigla}</td>
				<td align="center">  ${linha.turno_sigla}</td>
				<td align="right"> ${linha.vagas1}</td>
				<td align="right">  ${linha.vagas2}</td>
				
			    <c:set var="total1" value="${total1 + linha.vagas1}"/>
			    <c:set var="total2" value="${total2 + linha.vagas2}"/>
			    
			    <c:set var="totalParcial1" value="${totalParcial1 + linha.vagas1}"/>
			    <c:set var="totalParcial2" value="${totalParcial2 + linha.vagas2}"/>			    
			</tr>
			
	    </c:forEach>
	    
	    <tr class="componentes">
	    	<td colspan="3" style="text-align: right;"><b>Total</b></td>
	    	<td style="text-align: right;"><b>${totalParcial1}</b></td>
	    	<td style="text-align: right;"><b>${totalParcial2}</b></td>
	    	
    		<c:set var="totalParcial1" value="0"/>
    		<c:set var="totalParcial2" value="0"/>				    	
	    </tr>	
		<tr class="espaco">
			<td colspan="6">&nbsp;</td>
		</tr>		    	    

	    <tr class="componentes">
	    	<td colspan="3" style="text-align: right;"><b>TOTAL GERAL</b></td>
	    	<td style="text-align: right;"><b>${total1}</b></td>
	    	<td style="text-align: right;"><b>${total2}</b></td>
	    </tr>
			
	    
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>