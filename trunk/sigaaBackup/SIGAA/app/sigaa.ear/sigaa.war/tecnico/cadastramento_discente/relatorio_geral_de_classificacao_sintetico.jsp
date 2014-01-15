<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<table>
		<tr><th>Processo Seletivo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</strong></td></tr>
		<tr><th>Polo / Grupo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoOpcao}</strong></td></tr>
	</table>
	
	<table class="listagem" width="100%">
		<caption>Relatório Geral de Classificacao</caption>
		<thead>
			<tr style="border: 1px solid;"> 
				<td style="text-align: left;">Polo / Grupo</td>
				<td style="text-align: right;">Com Reserva de Vagas</td>
				<td style="text-align: right;">Sem Reserva de Vagas</td>
			</tr>		
		</thead>
		<tbody>
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
				<tr style="border: 1px solid;">
					<td align="left"><h:outputText value="#{item[0] }"/> </td>
					<td align="right"><h:outputText value="#{item[1] }"/> </td>
					<td align="right"><h:outputText value="#{item[2] }"/> </td>
				</tr>
			</c:forEach>
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>