<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<table>
		<tr><th>Processo Seletivo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoProcessoSeletivo}</strong></td></tr>
		<tr><th>Polo / Grupo:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoOpcao}</strong></td></tr>
		<tr><th>Status:</th><td><strong>${cadastramentoDiscenteTecnico.descricaoStatusDiscente}</strong></td></tr>
		<tr><th>Reserva de Vagas:</th><td><strong>${cadastramentoDiscenteTecnico.reservaVagas == null ? "Ambos" : cadastramentoDiscenteTecnico.reservaVagas ? "Sim" : "Não"}</strong></td></tr>
	</table>
	
	<table class="listagem" width="100%">
		<caption>Relatório de Cadastramento</caption>
		<tbody>
			<c:set var="opcao" value="" />
			<c:set var="conv" value="" />
			
			<c:set var="totalGeral" value="0" />
			<c:set var="totalRes" value="0" />
			<c:set var="totalSemRes" value="0" />
			
			<c:forEach var="item" varStatus="status" items="#{cadastramentoDiscenteTecnico.relatorio}">
			
				<c:if test="${ item[0] != conv }">
					<c:set var="conv" value="#{ item[0] }" />
					<c:set var="opcao" value="" />
					<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">${ conv }</td></tr>
					
				</c:if>
			
				<c:if test="${ item[4] != opcao }">
					<c:set var="opcao" value="#{ item[4] }" />
					<tr style="border:1px solid #000;background:#CCC;font-weight:bold;"><td colspan="5">${ opcao }</td></tr>
					
					<tr style="border: 1px solid;"> 
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;">CPF</th>
						<th style="text-align: left;">Status</th>
						<th style="text-align: center;">Res. de Vagas</th>
					</tr>	
				</c:if>
				<tr style="border: 1px solid;">
					<td align="left"><h:outputText value="#{item[1] }"/></td>
					<td align="left"><ufrn:format type="cpf" valor="${item[2] }" /></td>
					<td align="left"><h:outputText value="#{item[5] }"/></td>
					<td align="center"><h:outputText value="#{item[3] == true ? 'Sim' : 'Não' }"/></td>
				</tr>
				
				<c:if test="${ item[3] == true }"><c:set var="totalRes" value="#{totalRes + 1}" /></c:if>
				<c:if test="${ item[3] == false }"><c:set var="totalSemRes" value="#{totalSemRes + 1}" /></c:if>
				<c:set var="totalGeral" value="#{totalGeral + 1}" />
			</c:forEach>
			
			<tr style="border:1px solid #000;background:#CCC;"><td colspan="5">Total com Reserva: <strong>${ totalRes }</strong> Total sem Reserva: <strong>${ totalSemRes }</strong></td></tr>

			<tr style="border:1px solid #000;background:#BBB;font-weight:bold;"><td colspan="5">Total Geral: <strong>${cadastramentoDiscenteTecnico.totalGeralRelatorioCadastramento}</strong></td></tr>
																									
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>