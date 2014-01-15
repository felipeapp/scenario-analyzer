<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Tipo de Projeto</h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Tipo de Projeto</caption>	<h:inputHidden value="#{tipoProjeto.confirmButton}"/> <h:inputHidden value="#{tipoProjeto.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td><h:inputText value="#{tipoProjeto.obj.descricao}" size="60" maxlength="255" readonly="#{tipoProjeto.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoProjeto.confirmButton}" action="#{tipoProjeto.cadastrar}" /> <h:commandButton value="Cancelar" action="#{tipoProjeto.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
