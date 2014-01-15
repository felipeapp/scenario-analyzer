<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Natureza de Serviço</h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Natureza de Serviço</caption>	<h:inputHidden value="#{naturezaServico.confirmButton}"/> <h:inputHidden value="#{naturezaServico.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td><h:inputText value="#{naturezaServico.obj.descricao}" size="60" maxlength="255" readonly="#{naturezaServico.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{naturezaServico.confirmButton}" action="#{naturezaServico.cadastrar}" /> <h:commandButton value="Cancelar" action="#{naturezaServico.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
