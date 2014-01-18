<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

    <h2>Relatório Geral Sintético de Vagas e Quantidade de Convocados</h2>	
	
	<table class="tabelaRelatorioBorda" width="100%">

		<thead>
			<tr style="border: 1px solid;"> 
				<td style="text-align: left;" width="50%">Pólo / Grupo</td>
				<td style="text-align: left;" width="20%">Grupo</td>
				<td style="text-align: right;" width="12%">Vagas</td>
				<td style="text-align: right;" width="18%">Qtd. Convocada</td>
				
			</tr>		
		</thead>
		<tbody>
		
			<c:set var="idPoloAntigo" value="0"/>
			<c:set var="totalVagas" value="0"/>
			<c:set var="totalConvocados" value="0"/>
			
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
			
				<c:if test="${(item[4]) != (idPoloAntigo)}">
					<c:if test="${(totalVagas) > 0}">
								<tr style="border: 1px solid;">
									<td align="left" colspan="2"><b>TOTAL</b></td>
									<td align="right"><b>${totalVagas} </b></td>
									<td align="right"><b>${totalConvocados} </b></td>
								</tr>
							</tbody>
		
						</table>
						
						<br />
						
						<table class="tabelaRelatorioBorda" width="100%">
							<thead>
								<tr style="border: 1px solid;"> 
									<td style="text-align: left;" width="50%">Pólo / Grupo</td>
									<td style="text-align: left;" width="20%">Grupo</td>
									<td style="text-align: right;" width="12%">Vagas</td>
									<td style="text-align: right;" width="18%">Qtd. Convocada</td>
									
								</tr>		
							</thead>
							<tbody>
						
					</c:if>
					<c:set var="totalVagas"  value="0"/>
					<c:set var="totalConvocados"  value="0"/>
							
				</c:if>
				
				<tr style="border: 1px solid;">
					<td align="left"><h:outputText value="#{item[0] }"/> </td>
					<td align="left"><h:outputText value="#{item[1] }"/> </td>
					<td align="right"><h:outputText value="#{item[3] }"/> </td>
					<td align="right"><h:outputText value="#{item[2] }"/> </td>
				</tr>
				
				<c:set var="idPoloAntigo"  value="${item[4]}"/>
				<c:set var="totalVagas"  value="${totalVagas + item[3]}"/>
				<c:set var="totalConvocados"  value="${totalConvocados + item[2]}"/>
			</c:forEach>
			
			
		<c:if test="${(totalVagas) > 0}">
					<tr style="border: 1px solid;">
						<td align="left" colspan="2"><b>TOTAL</b></td>
						<td align="right"><b>${totalVagas} </b></td>
						<td align="right"><b>${totalConvocados} </b></td>
					</tr>
				</tbody>
		
			</table>
		</c:if>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>