<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Cargo Administrativo</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Cargo Administrativo</caption>
			<h:inputHidden value="#{cargoAdministrativo.confirmButton}" />
			<h:inputHidden value="#{cargoAdministrativo.obj.id}" />
			<tr>
				<th class="required">Cargo (Fun��o):</th>

				<td><h:selectOneMenu
					value="#{cargoAdministrativo.obj.designacaoCargo.id}" style="width: 400px"
					disabled="#{cargoAdministrativo.readOnly}"
					disabledClass="#{cargoAdministrativo.disableClass}"
					id="designacaoCargo">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{designacaoCargo.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Portaria:</th>
				<td><h:inputText value="#{cargoAdministrativo.obj.portaria}"
					size="60" maxlength="255"
					readonly="#{cargoAdministrativo.readOnly}" id="portaria" /></td>
			</tr>
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{cargoAdministrativo.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{cargoAdministrativo.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th  class="required">Data de In�cio:</th>
				<td><t:inputCalendar value="#{cargoAdministrativo.obj.dataInicio}"
					size="10" maxlength="10" readonly="#{cargoAdministrativo.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio"/></td>
			</tr>
			<tr>
				<th>Data do Fim:</th>
				<td><t:inputCalendar value="#{cargoAdministrativo.obj.dataFim}"
					size="10" maxlength="10" readonly="#{cargoAdministrativo.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim"/></td>
			</tr>

			<tr>
				<th  class="required">Data da Portaria:</th>
				<td><t:inputCalendar value="#{cargoAdministrativo.obj.dataPortaria}"
					size="10" maxlength="10" readonly="#{cargoAdministrativo.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataPortaria"/></td>
			</tr>
			<tr>
				<th>Data da Portaria Fim:</th>
				<td><t:inputCalendar value="#{cargoAdministrativo.obj.dataPortariaFim}"
					size="10" maxlength="10" readonly="#{cargoAdministrativo.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataPortariaFim"/></td>
			</tr>
			<tr>
				<th>Observa��o:</th>
				<td><h:inputText value="#{cargoAdministrativo.obj.observacao}"
					size="60" maxlength="255"
					readonly="#{cargoAdministrativo.readOnly}" id="observacao" /></td>
			</tr>
			<tr>
				<th>Substituto:</th>
				<td><h:selectBooleanCheckbox
					value="#{cargoAdministrativo.obj.substituto}" id="substituto"
					disabled="#{cargoAdministrativo.readOnly}" /></td>
			</tr>
			<tr>
				<th  class="required">Portaria Final:</th>
				<td><h:inputText
					value="#{cargoAdministrativo.obj.portariaFinal}" size="60"
					maxlength="255" readonly="#{cargoAdministrativo.readOnly}"
					id="portariaFinal" /></td>
			</tr>


			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{cargoAdministrativo.confirmButton}"
						action="#{cargoAdministrativo.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{cargoAdministrativo.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>