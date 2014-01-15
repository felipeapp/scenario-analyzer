<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
<table class="formulario">
<caption class="listagem">Cadastro de detalhamentoRecursos</caption>	<h:inputHidden value="#{detalhamentoRecursos.confirmButton}"/> <h:inputHidden value="#{detalhamentoRecursos.obj.id}"/>
<tr>
<th> Elemento:</th>

<td>

<h:selectOneMenu value="#{detalhamentoRecursos.obj.elemento.id}" disabled="#{detalhamentoRecursos.readOnly}" disabledClass="#{detalhamentoRecursos.disableClass}" id="elemento">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{elemento.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tr>
	<th> Faex:</th>
	<td> <h:inputText value="#{detalhamentoRecursos.obj.faex}" readonly="#{detalhamentoRecursos.readOnly}" id="faex"/></td>
</tr>
<tr>
	<th> Funpec:</th>
	<td> <h:inputText value="#{detalhamentoRecursos.obj.funpec}" readonly="#{detalhamentoRecursos.readOnly}" id="funpec"/></td>
</tr>
<tr>
	<th> Outros:</th>
	<td> <h:inputText value="#{detalhamentoRecursos.obj.outros}" readonly="#{detalhamentoRecursos.readOnly}" id="outros"/></td>
</tr>
<tr>
<th> RelatorioCursosEventos:</th>

<td>

<h:selectOneMenu value="#{detalhamentoRecursos.obj.relatorioCursosEventos.id}" disabled="#{detalhamentoRecursos.readOnly}" disabledClass="#{detalhamentoRecursos.disableClass}" id="relatorioCursosEventos">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{relatorioCursosEventos.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{detalhamentoRecursos.confirmButton}" action="#{detalhamentoRecursos.cadastrar}" /> <h:commandButton value="Cancelar" action="#{detalhamentoRecursos.cancelar}" /></td>
</tr></tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
