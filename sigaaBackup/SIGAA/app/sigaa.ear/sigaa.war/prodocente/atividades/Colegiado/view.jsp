<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Atividade de Ensino - Strictus Sensu</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Colegiado</caption>
			<h:inputHidden value="#{colegiado.confirmButton}" />
			<h:inputHidden value="#{colegiado.obj.id}" />
			<tr>
				<th class="required">Docente:</th>
				<td><h:inputHidden id="id" value="#{colegiado.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{colegiado.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Tipo do Membro do Colegiado:</th>

				<td><h:selectOneMenu
					value="#{colegiado.obj.tipoMembroColegiado.id}"
					disabled="#{colegiado.readOnly}"
					disabledClass="#{colegiado.disableClass}" id="tipoMembroColegiado">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoMembroColegiado.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu value="#{colegiado.obj.departamento.id}"
					disabled="#{colegiado.readOnly}" style="width: 300px"
					disabledClass="#{colegiado.disableClass}" id="departamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{colegiado.obj.instituicao}" size="60"
					maxlength="255" readonly="#{colegiado.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{colegiado.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{colegiado.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoInicio" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{colegiado.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{colegiado.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoFim" /></td>
			</tr>
			<tr>
				<th class="required">Comissão:</th>
				<td><h:inputText value="#{colegiado.obj.comissao}" size="60"
					maxlength="255" readonly="#{colegiado.readOnly}" id="comissao" /></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{colegiado.obj.informacao}" size="60"
					maxlength="255" readonly="#{colegiado.readOnly}" id="informacao" /></td>
			</tr>
			<tr>
				<th>Nato:</th>
				<td><h:selectBooleanCheckbox
							value="#{colegiado.obj.nato}" id="nato"
							disabled="#{colegiado.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Número de Reuniões:</th>
				<td><h:inputText value="#{colegiado.obj.numeroReunioes}"
					size="4" maxlength="255" readonly="#{colegiado.readOnly}"
					id="numeroReunioes" /></td>
			</tr>
			<tr>
				<th class="required">Tipo da Comissão do Colegiado:</th>

				<td><h:selectOneMenu
					value="#{colegiado.obj.tipoComissaoColegiado.id}"
					disabled="#{colegiado.readOnly}"
					disabledClass="#{colegiado.disableClass}"
					id="tipoComissaoColegiado">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoComissaoColegiado.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{colegiado.confirmButton}" action="#{colegiado.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{colegiado.cancelar}" /></td>
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
