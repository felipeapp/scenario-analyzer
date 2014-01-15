<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Parceria de Atividade de Extensão</h2>

	<h:messages showDetail="true"></h:messages>

	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Parceria de Atividade
			de Extensão</caption>
			<h:inputHidden value="#{parceriaAtividadeExtensao.confirmButton}" />
			<h:inputHidden value="#{parceriaAtividadeExtensao.obj.id}" />
			<tr>
				<th class="required">Convênio:</th>
				<td><h:inputText
					value="#{parceriaAtividadeExtensao.obj.convenio}" size="60"
					maxlength="255" readonly="#{parceriaAtividadeExtensao.readOnly}"
					id="convenio" /></td>
			</tr>
			<tr>
				<th class="required">Valor do Financiamento:</th>
				<td><h:inputText
					value="#{parceriaAtividadeExtensao.obj.valorFinanciamento}"
					size="9" maxlength="255"
					readonly="#{parceriaAtividadeExtensao.readOnly}"
					id="valorFinanciamento" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText
					value="#{parceriaAtividadeExtensao.obj.instituicao}" size="60"
					maxlength="255" readonly="#{parceriaAtividadeExtensao.readOnly}"
					id="instituicao" /></td>
			</tr>

			<tr>
				<th class="required">Atividade de Extensão:</th>

				<td><h:selectOneMenu
					value="#{parceriaAtividadeExtensao.obj.atividadeExtensao.id}"
					disabled="#{parceriaAtividadeExtensao.readOnly}"
					disabledClass="#{parceriaAtividadeExtensao.disableClass}"
					id="atividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{atividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{parceriaAtividadeExtensao.confirmButton}"
						action="#{parceriaAtividadeExtensao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{parceriaAtividadeExtensao.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>