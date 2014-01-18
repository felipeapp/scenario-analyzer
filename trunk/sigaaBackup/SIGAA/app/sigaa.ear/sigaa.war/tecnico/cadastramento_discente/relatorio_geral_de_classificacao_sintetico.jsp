<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

	<div id="parametrosRelatorio">
		<table >
			<tr><th>Processo Seletivo:</th><td>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</td></tr>
			<tr><th>Pólo / Grupo:</th><td>${cadastramentoDiscenteTecnico.descricaoOpcao}</td></tr>
			<tr><th>Grupo de vagas:</th><td>${cadastramentoDiscenteTecnico.descricaoGrupo}</td></tr>
			
		</table>
	</div>
	<br />
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>Relatório Geral de Classificação</caption>
		<thead>
			<tr style="border: 1px solid;"> 
				<td style="text-align: left;">Pólo / Grupo</td>
				<td style="text-align: right;">Grupo</td>
				<td style="text-align: right;">Quantidade</td>
				
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