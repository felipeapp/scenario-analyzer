<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
<table class="formulario">
<caption class="listagem">Cadastro de areaTematica</caption>	<h:inputHidden value="#{areaTematica.confirmButton}"/> <h:inputHidden value="#{areaTematica.obj.id}"/>
<tr>
	<th> Descricao:</th>
	<td> <h:inputText value="#{areaTematica.obj.descricao}" size="60" maxlength="255" readonly="#{areaTematica.readOnly}" id="descricao"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{areaTematica.confirmButton}" action="#{areaTematica.cadastrar}" /> <h:commandButton value="Cancelar" action="#{areaTematica.cancelar}" /></td>
</tr></tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
