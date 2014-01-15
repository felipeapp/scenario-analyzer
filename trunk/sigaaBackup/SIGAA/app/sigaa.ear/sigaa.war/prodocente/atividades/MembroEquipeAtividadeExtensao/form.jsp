<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Membro de Equipe de Atividade de Extensão</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de
			Membro de Equipe de Atividade de Extensão</caption>
			<h:inputHidden value="#{membroEquipeAtividadeExtensao.confirmButton}" />
			<h:inputHidden value="#{membroEquipeAtividadeExtensao.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{membroEquipeAtividadeExtensao.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{membroEquipeAtividadeExtensao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Data de Entrada:</th>
				<td><t:inputCalendar
							value="#{membroEquipeAtividadeExtensao.obj.dataEntrada}" size="10"
							maxlength="10" readonly="#{membroEquipeAtividadeExtensao.readOnly}"
							renderAsPopup="true" renderPopupButtonAsImage="true"
					id="dataEntrada" /></td>
			</tr>
			<tr>
				<th>Data de Saída:</th>
				<td><t:inputCalendar
							value="#{membroEquipeAtividadeExtensao.obj.dataSaida}" size="10"
							maxlength="10" readonly="#{membroEquipeAtividadeExtensao.readOnly}"
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataSaida" /></td>
			</tr>
			<tr>
				<th class="required">Carga horária Semanal:</th>
				<td><h:inputText
					value="#{membroEquipeAtividadeExtensao.obj.chSemanal}" size="6"
					maxlength="255"
					readonly="#{membroEquipeAtividadeExtensao.readOnly}" id="chSemanal" /></td>
			</tr>
			<tr>
				<th class="required">Atividade de Extensão:</th>

				<td><h:selectOneMenu
					value="#{membroEquipeAtividadeExtensao.obj.atividadeExtensao.id}"
					disabled="#{membroEquipeAtividadeExtensao.readOnly}"
					disabledClass="#{membroEquipeAtividadeExtensao.disableClass}"
					id="atividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{atividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Categoria do Membro da Atividade de Extensão:</th>

				<td><h:selectOneMenu
					value="#{membroEquipeAtividadeExtensao.obj.categoriaMembroAtividadeExtensao.id}"
					disabled="#{membroEquipeAtividadeExtensao.readOnly}"
					disabledClass="#{membroEquipeAtividadeExtensao.disableClass}"
					id="categoriaMembroAtividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{funcaoMembroEquipe.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo do Membro da Atividade de Extensão:</th>

				<td><h:selectOneMenu
					value="#{membroEquipeAtividadeExtensao.obj.tipoMembroAtividadeExtensao.id}"
					disabled="#{membroEquipeAtividadeExtensao.readOnly}"
					disabledClass="#{membroEquipeAtividadeExtensao.disableClass}"
					id="tipoMembroAtividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoMembroAtivividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{membroEquipeAtividadeExtensao.confirmButton}"
						action="#{membroEquipeAtividadeExtensao.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{membroEquipeAtividadeExtensao.cancelar}" /></td>
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
