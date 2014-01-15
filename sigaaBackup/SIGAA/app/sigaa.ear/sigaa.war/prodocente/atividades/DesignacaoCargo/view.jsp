<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Cargo</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Cargo</caption>
			<h:inputHidden value="#{designacaoCargo.confirmButton}" />
			<h:inputHidden value="#{designacaoCargo.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{designacaoCargo.obj.descricao}"
					size="60" maxlength="255" readonly="#{designacaoCargo.readOnly}"
					id="descricao" /></td>
			</tr>
			<tr>
				<th>Gratificada:</th>
				<td><h:selectBooleanCheckbox
					value="#{designacaoCargo.obj.gratificada}" id="gratificada"
					disabled="#{designacaoCargo.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Tipo:</th>
				<td><h:inputText value="#{designacaoCargo.obj.tipo}" size="60"
					maxlength="255" readonly="#{designacaoCargo.readOnly}" id="tipo" /></td>
			</tr>
			<tr>
				<th class="required">Código Rhnet:</th>
				<td><h:inputText value="#{designacaoCargo.obj.codigoRhnet}"
					size="60" maxlength="255" readonly="#{designacaoCargo.readOnly}"
					id="codigoRhnet" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{designacaoCargo.confirmButton}"
						action="#{designacaoCargo.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{designacaoCargo.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
