<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Forma de Compromisso </h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Forma de Compromisso</caption>	<h:inputHidden value="#{formaCompromisso.confirmButton}"/> <h:inputHidden value="#{formaCompromisso.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td><h:inputText value="#{formaCompromisso.obj.descricao}" size="60" maxlength="255" readonly="#{formaCompromisso.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{formaCompromisso.confirmButton}" action="#{formaCompromisso.cadastrar}" /> <h:commandButton value="Cancelar" action="#{formaCompromisso.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
