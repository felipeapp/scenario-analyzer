<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<h:form>
		<fieldset>
			<legend>Visualização de Aula de Ensino Individual</legend>
			
			<table>
				<tr>
					<th>Data:</th>
					<td><h:outputText value="#{aulaEnsinoIndividual.object.dataAula}" /></td>
				</tr>
				<tr>
					<th>Tipo:</th>
					<td><h:outputText value="#{aulaEnsinoIndividual.object.tipoDesc}" /></td>
				</tr>
				<tr>
					<th>Número de Aulas:</th>
					<td><h:outputText value="#{aulaEnsinoIndividual.object.numeroAulas}" /></td>
				</tr>
				<tr>
					<th>Observações:</th>
					<td><h:outputText value="#{aulaEnsinoIndividual.object.observacoes}" /></td>
				</tr>
			</table>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{aulaEnsinoIndividual.listar}" value="Voltar" /> 
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>