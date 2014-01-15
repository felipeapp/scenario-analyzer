<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Finalizar Planos de Trabalho</h2>
	<h:outputText value="#{finalizarPlanoTrabalho.create}" />
	
	<h:form id="form">
		<table class="formulario" style="width: 50%">
			<caption>Selecione o Tipo da Bolsa</caption>
			<tr>
	            <th class="required">Tipo da Bolsa:</th>
	            <td>
	                <h:selectOneMenu id="titulacao" value="#{finalizarPlanoTrabalho.obj.tipoBolsa.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{finalizarPlanoTrabalho.tiposBolsa}"/>
					</h:selectOneMenu>
	            </td>
	        </tr>
			<tfoot align="center">
				<tr>
					<td colspan="5">
						<h:commandButton value="Buscar" action="#{finalizarPlanoTrabalho.buscarPlanos}" /> 
						<h:commandButton value="Cancelar" action="#{finalizarPlanoTrabalho.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>