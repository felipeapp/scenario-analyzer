<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
	<br/>

	<table class="formulario" width="40%">
	<caption>Resetar Última Atualização de Totais</caption>
	<tr><th>Status: </th><td>
	<h:selectOneMenu value="#{ recalculosMBean.status }">
		<f:selectItem itemLabel="Todos" itemValue="0"/>
		<f:selectItems value="#{ statusDiscente.allCombo }"/>
	</h:selectOneMenu> 
	</td></tr>
	<tr><th>Senha: </th><td><h:inputSecret value="#{ recalculosMBean.senha }" size="10"/> </td></tr>
	<tfoot>
	<tr><td colspan="2">
	<h:commandButton value="Resetar" action="#{ recalculosMBean.resetarDiscentes }"/>
	<h:commandButton value="Cancelar" action="#{ recalculosMBean.cancelar }"/>
	</td></tr>
	</tfoot>
	</table>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
