<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Monografia</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> 
	  <h:commandLink action="#{chefia.listar}" value="Listar Chefias Cadastradas"/>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>

	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Monografia</caption>
			<h:inputHidden value="#{monografia.confirmButton}" />
			<h:inputHidden value="#{monografia.obj.id}" />
			<tr>
				<th class="required">Docente:</th>
				<td><h:inputHidden id="id"
					value="#{monografia.obj.servidor.id}"></h:inputHidden> <h:inputText
					id="nomeServidor" value="#{monografia.obj.servidor.pessoa.nome}"
					size="60" /> <ajax:autocomplete source="form:nomeServidor"
					target="form:id" baseUrl="/sigaa/ajaxDocente"
					className="autocomplete" indicator="indicator"
					minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" />
				</span></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{monografia.obj.titulo}" size="60"
					maxlength="255" readonly="#{monografia.readOnly}" id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{monografia.obj.instituicao}"
					size="60" maxlength="255" readonly="#{monografia.readOnly}"
					id="instituicao" /></td>
			</tr>

			<tr>
				<th class="required">Orientando:</th>
				<td><h:inputText value="#{monografia.obj.orientando}" size="60"
					maxlength="255" readonly="#{monografia.readOnly}" id="orientando" /></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{monografia.obj.informacao}" size="60"
					maxlength="255" readonly="#{monografia.readOnly}" id="informacao" /></td>
			</tr>

			<tr>
				<th>Páginas:</th>
				<td><h:inputText value="#{monografia.obj.paginas}" size="10"
					maxlength="255" readonly="#{monografia.readOnly}" id="paginas" /></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{monografia.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{monografia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoInicio" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{monografia.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{monografia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoFim" /></td>
			</tr>
			<tr>
				<th class="required">Data de Publicação:</th>
				<td><t:inputCalendar value="#{monografia.obj.dataPublicacao}"
					size="10" maxlength="10" readonly="#{monografia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="dataPublicacao" /></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu style="width: 220px;" value="#{monografia.obj.area.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="area"
					valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu style="width: 220px;" value="#{monografia.obj.subArea.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="subArea">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{producao.subArea}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu style="width: 220px;" value="#{monografia.obj.departamento.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="departamento">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo de Orientação:</th>
				<td><h:selectOneMenu style="width: 220px;"
					value="#{monografia.obj.tipoOrientacao.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="tipoOrientacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{tipoOrientacao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{monografia.confirmButton}"
						action="#{monografia.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{monografia.cancelar}" /></td>
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
