<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<table class=formulario>
<h:form> <caption class="listagem">Cadastro de statusAvaliacao</caption>	<h:inputHidden value="#{statusAvaliacao.confirmButton}"/> <h:inputHidden value="#{statusAvaliacao.obj.id}"/>
<tr>
	<th> Descricao:</th>
	<td> <h:inputText value="#{statusAvaliacao.obj.descricao}" size="50" maxlength="50" readonly="#{statusAvaliacao.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{statusAvaliacao.confirmButton}" action="#{statusAvaliacao.cadastrar}" /> <h:commandButton value="Cancelar" action="#{statusAvaliacao.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
