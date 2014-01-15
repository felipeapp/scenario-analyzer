<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Área Temática</h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Área Temática</caption>	<h:inputHidden value="#{areaTematica.confirmButton}"/> <h:inputHidden value="#{areaTematica.obj.id}"/>
<tr>
	<th> Descrição:</th>
	<td><span class="required"></span> <h:inputText value="#{areaTematica.obj.descricao}" size="60" maxlength="255" readonly="#{areaTematica.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{areaTematica.confirmButton}" action="#{areaTematica.cadastrar}" /> <h:commandButton value="Cancelar" action="#{areaTematica.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
