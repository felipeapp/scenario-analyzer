<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
<table class="formulario">
<caption class="listagem">Cadastro de avaliacaoOrganizacao</caption>	<h:inputHidden value="#{avaliacaoOrganizacao.confirmButton}"/> <h:inputHidden value="#{avaliacaoOrganizacao.obj.id}"/>
<tr>
<th> Area:</th>

<td>

<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.area.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="area">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{area.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tr>
<th> Subarea:</th>

<td>

<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.subarea.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="subarea">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{subarea.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tr>
	<th> Veiculo:</th>
	<td> <h:inputText value="#{avaliacaoOrganizacao.obj.veiculo}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="veiculo"/></td>
</tr>
<tr>
	<th> Local:</th>
	<td> <h:inputText value="#{avaliacaoOrganizacao.obj.local}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="local"/></td>
</tr>
<tr>
	<th> PeriodoInicio:</th>
	<td> <h:inputText value="#{avaliacaoOrganizacao.obj.periodoInicio}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="periodoInicio"/></td>
</tr>
<tr>
	<th> PeriodoFim:</th>
	<td> <h:inputText value="#{avaliacaoOrganizacao.obj.periodoFim}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="periodoFim"/></td>
</tr>
<tr>
	<th> Informacao:</th>
	<td> <h:inputText value="#{avaliacaoOrganizacao.obj.informacao}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="informacao"/></td>
</tr>
<tr>
<th> Servidor:</th>

<td>

<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.servidor.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="servidor">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{servidor.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tr>
	<th> Validacao:</th>
	<td> <h:inputText value="#{avaliacaoOrganizacao.obj.validacao}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="validacao"/></td>
</tr>
<tr>
<th> TipoAvaliacaoOrganizacao:</th>

<td>

<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.tipoAvaliacaoOrganizacao.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="tipoAvaliacaoOrganizacao">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{tipoAvaliacaoOrganizacao.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tr>
<th> TipoParticipacao:</th>

<td>

<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.tipoParticipacao.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="tipoParticipacao">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{tipoParticipacao.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tr>
<th> TipoRegiao:</th>

<td>

<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.tipoRegiao.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="tipoRegiao">
		<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
		<f:selectItems value="#{tipoRegiao.allCombo}"/>
	</h:selectOneMenu>
</td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{avaliacaoOrganizacao.confirmButton}" action="#{avaliacaoOrganizacao.cadastrar}" /> <h:commandButton value="Cancelar" action="#{avaliacaoOrganizacao.cancelar}" /></td>
</tr></tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
