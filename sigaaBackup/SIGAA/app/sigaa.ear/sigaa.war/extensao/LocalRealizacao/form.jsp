<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Local de Realização</h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Local de Realizacao</caption>	<h:inputHidden value="#{localRealizacao.confirmButton}"/> <h:inputHidden value="#{localRealizacao.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td><h:inputText value="#{localRealizacao.obj.descricao}" size="60" maxlength="255" readonly="#{localRealizacao.readOnly}"/></td>
</tr>
<tr>
<th> Município:</th>

<td>
	<h:selectOneMenu value="#{localRealizacao.obj.municipio.id}" disabled="#{localRealizacao.readOnly}" disabledClass="#{localRealizacao.disableClass}">
		<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM MUNICÍPIO --"/>
		<f:selectItems value="#{municipio.allAtivosCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{localRealizacao.confirmButton}" action="#{localRealizacao.cadastrar}" /> <h:commandButton value="Cancelar" action="#{localRealizacao.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>