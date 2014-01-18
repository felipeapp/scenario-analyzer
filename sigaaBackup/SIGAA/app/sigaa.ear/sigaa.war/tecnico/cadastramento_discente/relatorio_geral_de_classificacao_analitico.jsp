<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<table>
		<tr><th>Processo Seletivo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</strong></td></tr>
		<tr><th>Polo / Grupo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoOpcao}</strong></td></tr>
		<tr><th>Grupo de vagas:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoGrupo}</strong></td></tr>
	</table>
	<br />
		
	<table class="listagem" width="100%">
		<caption>Relatório Geral de Classificacao</caption>
		<tbody>
			<c:set var="opcao" value="" />
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
				<c:if test="${ item[4] != opcao }">
					<c:set var="opcao" value="#{ item[4] }" />
					<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="4">${ opcao }</td></tr>
					
					<tr style="border: 1px solid;"> 
						<th style="text-align: right;">Class.</th>
						<th style="text-align: right;">Arg.</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: center;">Grupo de Vagas</th>
					</tr>	
				</c:if>
				<tr style="border: 1px solid;">
					<td align="right"><h:outputText value="#{item[0] }"/> </td>
					<td align="right"><h:outputText value="#{item[1] }"/> </td>
					<td align="left"><h:outputText value="#{item[2] }"/> </td>
					<td align="center"><h:outputText value="#{item[5]}"/> </td>
				</tr>
			</c:forEach>
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>