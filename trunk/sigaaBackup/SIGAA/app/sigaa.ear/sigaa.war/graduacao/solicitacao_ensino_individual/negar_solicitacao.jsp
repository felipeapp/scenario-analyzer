<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/graduacao/menu_coordenador.jsp" %>
<h:messages showDetail="true" />
<h:outputText value="#{solicitacaoEnsinoIndividual.create}" />

<h2><ufrn:subSistema /> &gt; Solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} &gt; Negar Solicita��o</h2>

<div class="descricaoOperacao">
	<p>Caro Coordenador,</p><br>

	<p> No campo de texto abaixo voc� deve informar o motivo de estar negando este requerimento de turma 
	de ${solicitacaoEnsinoIndividual.tipoSolicitacao} ao discente. Os discentes que tiveram seus requerimentos de turma 
	de ${solicitacaoEnsinoIndividual.tipoSolicitacao} negados n�o poder�o ser matriculados nas turmas criadas. 
	Por�m, a qualquer momento durante o per�do de solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} 
	voc� poder� revogar os requerimentos de turmas que foram negados fazendo-os retornar para a situa��o <b>pendente</b>.</p>

</div>

<h:form id="formNegarSolicitacao">
	<h:inputHidden value="#{solicitacaoEnsinoIndividual.ferias}"/>
	<h:inputHidden value="#{solicitacaoEnsinoIndividual.obj.id}"/>
	
	<table class="formulario" width="100%">
		<caption>Negar Solicita��o de Turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao} (Dados da solicita��o)</caption>
		
		<tr>
			<th>Componente Curricular:</th>
			<td>${solicitacaoEnsinoIndividual.obj.componente}</td>
		</tr>
		<tr>
			<th>Tipo de Componente:</th>
			<td>${solicitacaoEnsinoIndividual.obj.componente.tipoComponente}</td>
		</tr>
		
		<c:if test="${empty solicitacaoEnsinoIndividual.solicitacoes}">
		<tr>
			<th>Matr�cula:</th>
			<td>${solicitacaoEnsinoIndividual.obj.discente.matricula}</td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td>${solicitacaoEnsinoIndividual.obj.discente.nome}</td>
		</tr>
		<tr>
			<th>Data da solicita��o:</th>
			<td><ufrn:format type="dataHora" valor="${solicitacaoEnsinoIndividual.obj.dataSolicitacao}"/></td>
		</tr>
		</c:if>
		
		<c:if test="${not empty solicitacaoEnsinoIndividual.solicitacoes}">
			<tr>
			<td colspan="2">
				<table class="subformulario" width="100%">
					<caption>Discentes que ter�o a solicita��o negada</caption>
					<thead>
					<tr>
						<th>Matr�cula</th>
						<th>Discente</th>
						<th>Data da solicita��o</th>
					</tr>
					</thead>
					
					<c:forEach var="solicitacao" items="${solicitacaoEnsinoIndividual.solicitacoes}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${solicitacao.discente.matricula}</td>
						<td>${solicitacao.discente.nome}</td>
						<td><ufrn:format type="dataHora" valor="${solicitacao.dataSolicitacao}"/></td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</c:if>
		
		<tr>
		 <td colspan="2">
			<table class="subformulario" width="100%">
			<caption>Justificativa da nega��o</caption>
				<tr>
					<th></th>
					<td align="center"> <h:inputTextarea value="#{solicitacaoEnsinoIndividual.obj.justificativaNegacao}" rows="3" cols="80" id="justificativaNegacao"/> </td>
				</tr>
			</table>
		 </td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{solicitacaoEnsinoIndividual.cancelarSolicitacao}" value="Negar Solicita��o" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoEnsinoIndividual.formListaSolicitacoes}" />
				</td>
			</tr>
		</tfoot>		
	</table>
	
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>