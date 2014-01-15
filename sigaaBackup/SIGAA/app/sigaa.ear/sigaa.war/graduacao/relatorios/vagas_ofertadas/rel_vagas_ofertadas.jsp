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
			<c:if test="${ relatorioVagasOfertadas.oferta.ano > 0 }">
			<tr>
				<th>Ano:</th>
				<td> <h:outputText value="#{relatorioVagasOfertadas.oferta.ano}" /></td>
			</tr>
			</c:if>
			<c:if test="${ not empty relatorioVagasOfertadas.formaIngresso.descricao }">
			<tr>
				<th>Forma de Ingresso:</th>
				<td> 
					<h:outputText value="#{relatorioVagasOfertadas.formaIngresso.descricao}" />
				</td>
			</tr>
			</c:if>
				 
		</table>
	</div>
	
	<br />
	
    <c:set var="_ano" />
    <c:set var="_total" value="0"/>
    <c:set var="total1" value="0"/>
    <c:set var="total2" value="0"/>
    <c:set var="total3" value="0"/>
    <c:set var="total4" value="0"/>
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="8"></td>
		</tr>
		<c:forEach items="#{relatorioVagasOfertadas.lista}" var="linha" varStatus="indice">
			<c:set var="anoAtual" value="${linha.ano}"/>
		 	<c:if test="${_ano != anoAtual}">
				<tr class="header">
					<td colspan="8" style="font-size: 11px;"><b>Ano: ${linha.ano}</b></td>
				</tr>
				<tr class="header" >
			
					<td align="left" rowspan="2"> Nome do Curso</td>
					<td align="left" rowspan="2"> Departamento</td>
					<td align="center" rowspan="2"> Turno</td>
					<td align="left" rowspan="2"> Forma de Ingresso</td>
					<td colspan="2" align="center" > Vagas </td>
					<td colspan="2" align="center" > Vagas Ociosas </td>
					<tr class="header"> 
						<td align="right" nowrap="nowrap"> ${linha.ano}.1</td>
						<td align="right" nowrap="nowrap"> ${linha.ano}.2</td>
						<td align="right" nowrap="nowrap">  ${linha.ano}.1</td>
						<td align="right" nowrap="nowrap">  ${linha.ano}.2</td>
					</tr>	
				</tr>
				<c:set var="_ano" value="${anoAtual}"/>
			</c:if>
		
			<tr class="componentes">
				
				<td align="left">  ${linha.nome}</td>
				<td align="left">  ${linha.unidade_sigla}</td>
				<td align="center">  ${linha.turno_sigla}</td>
				<td align="left"> ${linha.forma_descricao}</td>
				<td align="right"> ${linha.vagas1}</td>
				<td align="right">  ${linha.vagas2}</td>
				<td align="right"> ${linha.vagas_ociosas1}</td>
				<td align="right"> ${linha.vagas_ociosas2}</td>	
				
			    <c:set var="total1" value="${total1 + linha.vagas1}"/>
			    <c:set var="total2" value="${total2 + linha.vagas2}"/>
			    <c:set var="total3" value="${total3 + linha.vagas_ociosas1}"/>
			    <c:set var="total4" value="${total4 + linha.vagas_ociosas2}"/>
			</tr>
			
			<c:set var="proximo" value="${relatorioVagasOfertadas.lista[indice.index+1].ano}" ></c:set>
					<c:if test="${anoAtual != proximo}">
					    <tr class="componentes">
					    	<td colspan="4" style="text-align: right;"><b>TOTAIS</b></td>
					    	<td style="text-align: right;"><b>${total1}</b></td>
					    	<td style="text-align: right;"><b>${total2}</b></td>
					    	<td style="text-align: right;"><b>${total3}</b></td>
					    	<td style="text-align: right;"><b>${total4}</b></td>
					    </tr>
						<tr class="espaco">
							<td colspan="8">&nbsp;</td>
						</tr>
					
			</c:if>
						
	    </c:forEach>
	    
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>