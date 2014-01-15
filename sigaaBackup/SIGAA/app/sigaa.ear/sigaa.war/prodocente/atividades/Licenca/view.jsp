<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Licença</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Licença</caption>
			<h:inputHidden value="#{licenca.confirmButton}" />
			<h:inputHidden value="#{licenca.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:selectOneMenu value="#{licenca.obj.servidor.id}"
					disabled="#{licenca.readOnly}"
					disabledClass="#{licenca.disableClass}" id="servidor">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{atividadeEnsino.docentesUnidade}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar
					value="#{licenca.obj.periodoInicio}" size="10"
					maxlength="10" readonly="#{licenca.readOnly}"
					id="periodoInicio" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{licenca.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{licenca.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Afastamento:</th>

				<td><h:selectOneMenu value="#{licenca.obj.afastamento.id}"
					disabled="#{licenca.readOnly}"
					disabledClass="#{licenca.disableClass}" id="afastamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{afastamento.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{licenca.confirmButton}" action="#{licenca.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{licenca.cancelar}" /></td>
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
