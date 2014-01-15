<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2 class="tituloPagina"><ufrn:subSistema /> > Classificar Projeto de Ensino por colocação</h2>

<h:form id="form">

<table class="formulario" width="40%">
<caption>Editais</caption>

<tbody>
<tr>
	<th><h:outputLabel for="edital">Edital:</h:outputLabel></th>
	<td>    	
		<h:selectOneMenu id="edital" value="#{ classificarProjeto.edital.id }">
			<f:selectItems value="#{classificarProjeto.editais}"/>
		</h:selectOneMenu>
	</td>
</tr>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
	<h:commandButton action="#{ classificarProjeto.cancelar }" value="Cancelar"/>
 	<h:commandButton action="#{ classificarProjeto.classificarProjetos }" value="Classificar Projetos >>"/>
	</td>
</tr>
</tfoot>

</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>