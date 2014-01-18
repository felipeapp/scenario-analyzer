<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<div id="parametrosRelatorio">
		<table >
			<tr><th>Processo Seletivo:</th><td>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</td></tr>
			<tr><th>Polo / Grupo:</th><td>${cadastramentoDiscenteTecnico.descricaoOpcao}</td></tr>
			<tr><th>Status:</th><td>${cadastramentoDiscenteTecnico.descricaoStatusDiscente}</td></tr>
			<tr><th>Grupo de vagas:</th><td>${cadastramentoDiscenteTecnico.descricaoGrupo}</td></tr>
		</table>
	</div>
	<br />
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>Relatório de Cadastramento</caption>
		<tbody>
			<c:set var="opcao" value="" />
			<c:set var="conv" value="" />
			
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
			
				<c:if test="${ item[0] != conv }">
					<c:set var="conv" value="#{ item[0] }" />
					<c:set var="opcao" value="" />
					<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">${ conv }</td></tr>
					
				</c:if>
			
				<c:if test="${ item[4] != opcao }">
					<c:set var="opcao" value="#{ item[4] }" />
					<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="5">${ opcao }</td></tr>
					
					<tr style="border: 1px solid #000;"> 
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;">CPF</th>
						<th style="text-align: center;">Status</th>
						<th style="text-align: center;">Grupo</th>
					</tr>	
				</c:if>
				<tr style="border: 1px solid #000;">
					<td align="left" style="width: 50%;"><h:outputText value="#{item[1] }"/></td>
					<td align="left" style="width: 15%;"><ufrn:format type="cpf" valor="${item[2] }" /></td>
					<td align="center" style="width: 20%;"><h:outputText value="#{item[5] }"/></td>
					<td align="center" style="width: 15%;"><h:outputText value="#{item[6] }"/></td>
				</tr>
				
			</c:forEach>
			<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">Total Geral: <strong>${cadastramentoDiscenteTecnico.totalGeralRelatorioCadastramento}</strong></td></tr>
																									
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>