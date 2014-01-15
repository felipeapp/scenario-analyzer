<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<table>
		<tr><th>Processo Seletivo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</strong></td></tr>
		<tr><th>Polo / Grupo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoOpcao}</strong></td></tr>
		<tr><th>Convocação:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoConvocacao}</strong></td></tr>
	</table>
	
	<table class="listagem" width="100%">
		<caption>Relatório Quantitativo de discentes Convocados X Cadastrados</caption>
		<tbody>
			<c:set var="opcao" value="" />
			<c:set var="conv" value="" />
			
			<c:set var="totalGeral" value="0" />
			<c:set var="totalConv" value="0" />
			
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
			
				<c:if test="${ item[0] != conv }">
					<c:if test="${ conv != '' }">
						<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="4">Total: ${ totalConv }</td></tr>
						<c:set var="totalConv" value="0" />
					</c:if>
				
					<c:set var="conv" value="#{ item[0] }" />
					<c:set var="opcao" value="" />
					<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">${ conv }</td></tr>
					
				</c:if>
			
				<c:if test="${ item[6] != opcao }">
					<c:set var="opcao" value="#{ item[6] }" />
					<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="5">${ opcao }</td></tr>
					
					<tr style="border: 1px solid;"> 
						<th style="text-align: right;">Class.</th>
						<th style="text-align: right;">Arg.</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;">CPF</th>
						<th style="text-align: center;">Res. de Vagas</th>
					</tr>	
				</c:if>
				<tr style="border: 1px solid;">
					<td align="right"><h:outputText value="#{item[1] }"/></td>
					<td align="right"><h:outputText value="#{item[2] }"/></td>
					<td align="left"><h:outputText value="#{item[3] }"/></td>
					<td align="left"><ufrn:format type="cpf" valor="${item[4] }" /></td>
					<td align="center"><h:outputText value="#{item[5] == true ? 'Sim' : 'Não' }"/></td>
				</tr>
				
				<c:set var="totalConv" value="#{totalConv + 1}" />
				<c:set var="totalGeral" value="#{totalGeral + 1}" />
			</c:forEach>
			
			<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="5">Total: ${ totalConv }</td></tr>
			<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">Total Geral: ${ totalGeral }</td></tr>
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>