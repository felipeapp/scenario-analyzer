<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Beneficiário de Atividade de Extensão</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de
			Beneficiário de Atividade de Extensão</caption>
			<h:inputHidden value="#{beneficiarioAtividadeExtensao.confirmButton}" />
			<h:inputHidden value="#{beneficiarioAtividadeExtensao.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText
					value="#{beneficiarioAtividadeExtensao.obj.descricao}" size="60"
					maxlength="255"
					readonly="#{beneficiarioAtividadeExtensao.readOnly}" id="descricao" /></td>
			</tr>
			<tr>
				<th class="required">Cidade:</th>

				<td><h:selectOneMenu
					value="#{beneficiarioAtividadeExtensao.obj.cidade.id}"
					disabled="#{beneficiarioAtividadeExtensao.readOnly}"
					disabledClass="#{beneficiarioAtividadeExtensao.disableClass}"
					id="cidade">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{municipio.allAtivosCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Quantidade Prevista:</th>
				<td><h:inputText
					value="#{beneficiarioAtividadeExtensao.obj.quantidadePrevista}"
					size="60" maxlength="255"
					readonly="#{beneficiarioAtividadeExtensao.readOnly}"
					id="quantidadePrevista" /></td>
			</tr>
			<tr>
				<th class="required">Quantidade Realizada:</th>
				<td><h:inputText
					value="#{beneficiarioAtividadeExtensao.obj.quantidadeRealizada}"
					size="60" maxlength="255"
					readonly="#{beneficiarioAtividadeExtensao.readOnly}"
					id="quantidadeRealizada" /></td>
			</tr>
			<tr>
				<th class="required">Atividade de Extensão:</th>

				<td><h:selectOneMenu
					value="#{beneficiarioAtividadeExtensao.obj.atividadeExtensao.id}"
					disabled="#{beneficiarioAtividadeExtensao.readOnly}"
					disabledClass="#{beneficiarioAtividadeExtensao.disableClass}"
					id="atividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{atividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{beneficiarioAtividadeExtensao.confirmButton}"
						action="#{beneficiarioAtividadeExtensao.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{beneficiarioAtividadeExtensao.cancelar}" /></td>
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
