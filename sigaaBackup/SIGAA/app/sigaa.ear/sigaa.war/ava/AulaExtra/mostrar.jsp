<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<h:form>
		<fieldset>
			<legend>Visualiza��o de Aula Extra</legend>
			
			<table>
				<tr>
					<th>Data:</th>
					<td><h:outputText value="#{aulaExtra.object.dataAula}" /></td>
				</tr>
				<tr>
					<th>Tipo:</th>
					<td><h:outputText value="#{aulaExtra.object.tipoDesc}" /></td>
				</tr>
				<tr>
					<th>N�mero de Aulas:</th>
					<td><h:outputText value="#{aulaExtra.object.numeroAulas}" /></td>
				</tr>
				<tr>
					<th>Observa��es:</th>
					<td><h:outputText value="#{aulaExtra.object.observacoes}" /></td>
				</tr>
			</table>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{aulaExtra.listar}" value="Voltar" /> 
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>