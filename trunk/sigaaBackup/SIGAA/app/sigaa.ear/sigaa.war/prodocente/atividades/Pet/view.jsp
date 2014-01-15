<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Pet</h2>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Pet</caption>
			<h:inputHidden value="#{pet.confirmButton}" />
			<h:inputHidden value="#{pet.obj.id}" />
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{pet.obj.titulo}" size="60"
					maxlength="255" readonly="#{pet.readOnly}" id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{pet.obj.instituicao}" size="60"
					maxlength="255" readonly="#{pet.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu value="#{pet.obj.area.id}"
					disabled="#{pet.readOnly}" disabledClass="#{pet.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{pet.obj.subArea.id}"
					disabled="#{pet.readOnly}" disabledClass="#{pet.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE A ÁREA <--" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Classificação:</th>
				<td><h:selectOneMenu value="#{pet.obj.classificacaoPet.id}"
					disabled="#{pet.readOnly}" disabledClass="#{pet.disableClass}"
					id="classificacao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{classificacaoPet.allCombo}" />
					</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu value="#{pet.obj.departamento.id}" style="width: 400px"
					disabled="#{pet.readOnly}" disabledClass="#{pet.disableClass}"
					id="departamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{pet.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{pet.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio"/></td>
			</tr>
			<tr>
				<th class="required">Período Fim:</th>
				<td><t:inputCalendar value="#{pet.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{pet.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim"/></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{pet.obj.informacao}" size="60"
					maxlength="255" readonly="#{pet.readOnly}" id="informacao" /></td>
			</tr>
			<tr>
				<th>Docente:</th>

				<td><h:inputHidden id="id" value="#{coordenacao.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{coordenacao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{pet.confirmButton}"
						action="#{pet.cadastrar}" /> <h:commandButton value="Cancelar"
						action="#{pet.cancelar}" /></td>
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
