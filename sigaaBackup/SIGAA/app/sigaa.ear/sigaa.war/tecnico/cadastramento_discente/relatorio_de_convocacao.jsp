<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<div id="parametrosRelatorio">
		<table >
			<tr><th>Processo Seletivo:</th><td>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</td></tr>
			<tr><th>Polo / Grupo:</th><td>${cadastramentoDiscenteTecnico.descricaoOpcao}</td></tr>
			<tr><th>Convocação:</th><td>${cadastramentoDiscenteTecnico.descricaoConvocacao}</td></tr>
			<tr><th>Grupo de vagas:</th><td>${cadastramentoDiscenteTecnico.descricaoGrupo}</td></tr>
		</table>
	</div>
	<br />
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>Relatório de Convocacao</caption>
		<tbody>
			<c:set var="opcao" value="" />
			<c:set var="conv" value="" />
			
			<c:set var="totalConv" value="0" />
			
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
			
				<c:if test="${ item[0] != conv }">
					<c:if test="${ conv != '' }">
						<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="5">Total: ${ totalConv }</td></tr>
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
						<th style="text-align: center;">Grupo</th>
					</tr>	
				</c:if>
				<tr style="border: 1px solid;">
					<td align="right"><h:outputText value="#{item[1] }"/></td>
					<td align="right"><h:outputText value="#{item[2] }"/></td>
					<td align="left"><h:outputText value="#{item[3] }"/></td>
					<td align="left"><ufrn:format type="cpf" valor="${item[4] }" /></td>
					<td align="center" ><h:outputText value="#{item[7] }"/></td>
				</tr>
				
				<c:set var="totalConv" value="#{totalConv + 1}" />
			</c:forEach>
			
			<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="5">Total: ${ totalConv }</td></tr>
			<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">Total Geral: ${ fn:length( cadastramentoDiscenteTecnico.relatorio )}</td></tr>
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>