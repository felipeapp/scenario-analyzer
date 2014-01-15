<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Tutoria PET</h2>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Tutoria PET</caption>
			<h:inputHidden value="#{tutoriaPet.confirmButton}" />
			<h:inputHidden value="#{tutoriaPet.obj.id}" />
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{tutoriaPet.obj.titulo}" size="60"
					maxlength="255" readonly="#{tutoriaPet.readOnly}" id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{tutoriaPet.obj.instituicao}" size="60"
					maxlength="255" readonly="#{tutoriaPet.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu value="#{tutoriaPet.obj.area.id}"
					disabled="#{tutoriaPet.readOnly}" disabledClass="#{tutoriaPet.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{tutoriaPet.obj.subArea.id}"
					disabled="#{tutoriaPet.readOnly}" disabledClass="#{tutoriaPet.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE A ÁREA <--" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Classificação:</th>
				<td><h:selectOneMenu value="#{tutoriaPet.obj.classificacaoPet.id}"
					disabled="#{tutoriaPet.readOnly}" disabledClass="#{tutoriaPet.disableClass}"
					id="classificacao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{classificacaoPet.allCombo}" />
					</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu value="#{tutoriaPet.obj.departamento.id}" style="width: 400px"
					disabled="#{tutoriaPet.readOnly}" disabledClass="#{tutoriaPet.disableClass}"
					id="departamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{tutoriaPet.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{tutoriaPet.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio"/></td>
			</tr>
			<tr>
				<th class="required">Período Fim:</th>
				<td><t:inputCalendar value="#{tutoriaPet.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{tutoriaPet.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim"/></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{tutoriaPet.obj.informacao}" size="60"
					maxlength="255" readonly="#{tutoriaPet.readOnly}" id="informacao" /></td>
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
					<td colspan=2><h:commandButton value="#{tutoriaPet.confirmButton}"
						action="#{tutoriaPet.cadastrar}" /> <h:commandButton value="Cancelar"
						action="#{tutoriaPet.cancelar}" /></td>
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
