<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2 class="tituloPagina"><ufrn:subSistema /> > Calcular quantidade de bolsas por Projeto de Ensino</h2>

<h:form id="escolher">

<h:messages/>

<table class="formulario" width="40%">
<caption>Editais</caption>

<tbody>
<tr>
	<th><h:outputLabel for="turma">Edital:</h:outputLabel></th>
	<td>    	
		<h:selectOneMenu id="turma" value="#{ calcularBolsas.edital.id }">
			<f:selectItems value="#{ calcularBolsas.editais }"/>
		</h:selectOneMenu>
	</td>
</tr>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
	<h:commandButton action="#{ calcularBolsas.cancelar }" value="Cancelar"/>
 	<h:commandButton action="#{ calcularBolsas.calcularBolsas }" value="Calcular Número de Bolsas >>"/>
	</td>
</tr>
</tfoot>

</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>