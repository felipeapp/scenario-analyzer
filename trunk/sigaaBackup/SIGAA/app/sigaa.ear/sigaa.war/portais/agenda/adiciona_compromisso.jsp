<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
.rich-spinner-input {
	width:18px;

}
.formulario .rich-spinner-c td {
	padding: 0px;
}
 
</style>
 
<h2><ufrn:subSistema /> > Cadastrar Compromisso</h2>
 
<f:view>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js"> </script>
	<h:form>
		<table width="100%" class="formulario">
		<caption> Informe os detalhes do compromisso </caption>
		<tbody>

				<tr>
					<th>Dia:</th>
					<td>
						<rich:calendar datePattern="dd/MM/yyyy" inputSize="10" id="calendarioInicio" value="#{compromisso.obj.inicio}" />
					</td>
				</tr>
				
				<tr>
					<th>Hora início:</th>
					<td>
						<rich:inputNumberSpinner value="#{compromisso.obj.inicio.hours}" maxValue="23" minValue="00" />
					</td>
				</tr>
				
				<tr>
					<th>Minutos:</th>
					<td>
						<rich:inputNumberSpinner value="#{compromisso.obj.inicio.minutes}" maxValue="59" minValue="00" />					
					</td>
				</tr>
				
				<tr>
					<th>Dia:</th>
					<td>
						<rich:calendar datePattern="dd/MM/yyyy" inputSize="10" id="calendarioFim" value="#{compromisso.obj.fim}" />
					</td>
				</tr>
				
				<tr>
					<th>Hora fim:</th>
					<td>
						<rich:inputNumberSpinner value="#{compromisso.obj.fim.hours}" maxValue="23" minValue="00" /> 
				</tr>
				
				<tr>
					<th>Minutos:</th>
					<td>
						<rich:inputNumberSpinner value="#{compromisso.obj.fim.minutes}" maxValue="59" minValue="00" />					
					</td>
				</tr>		
				
			<tr>
				<th><h:outputLabel for="title" value="Título:" />  </th>
				<td>
					<h:inputText id="title" value="#{compromisso.obj.titulo}" required="true" size="60"/>
				</td>
				<td> <h:message for="title" /> </td>
			</tr>
			<tr>
				<th> <h:outputLabel for="location" value="Local:" /> </th>
				<td>
					<h:inputText id="location" value="#{compromisso.obj.local}" size="60"/>
				</td>
				<td> <h:message for="location" /> </td>
			</tr>
			<tr>
				<th><h:outputLabel for="comments" value="Descrição:" />  </th>
				<td>
					<h:inputTextarea id="comments" value="#{compromisso.obj.descricao}" rows="2" style="width: 97%"/>
				</td>
				<td><h:message for="comments" />  </td>
			</tr>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{compromisso.cadastrar}" value="Cadastrar Compromisso" />
					<h:commandButton action="#{compromisso.cancelar}" value="Cancelar" immediate="true"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>