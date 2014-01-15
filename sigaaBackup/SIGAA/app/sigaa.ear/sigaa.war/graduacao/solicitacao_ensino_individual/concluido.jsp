<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Solicitação de Turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</h2>
	<h:form>	
	<table class="listagem" style="width: 100%">
		<caption>Solicitações de turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</caption>
		<tbody>
			<tr>
				<th class="rotulo">Nº Solicitação:</th>
				<td>${solicitacaoEnsinoIndividual.obj.numeroSolicitacao}</td>
			</tr>
			<tr>
				<th class="rotulo">Componente:</th>
				<td>${solicitacaoEnsinoIndividual.obj.componente}</td>
			</tr>
			<c:if test="${false}">
				<tr>	
				<th class="rotulo">Sugestão de Horário:</th>
				<td>${solicitacaoEnsinoIndividual.obj.sugestaoHorario}</td>
				</tr>
			</c:if>
			<tr>
				<th class="rotulo">Data Solicitação:</th>
				<td><ufrn:format type="data" valor="${solicitacaoEnsinoIndividual.obj.dataSolicitacao}" /></td>
			</tr>
			<tr>
				<th class="rotulo">Situação:</th>
				<td>${solicitacaoEnsinoIndividual.obj.situacaoString}</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center">
					<h:commandButton action="#{solicitacaoEnsinoIndividual.emitirComprovante}" value="Emitir Comprovante" id="comprovante" />
					<h:commandButton action="#{solicitacaoEnsinoIndividual.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
				</td>
		</tfoot>
	</table>
	</h:form>
	<br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>