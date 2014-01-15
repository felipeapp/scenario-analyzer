<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Monitoria</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Monitoria</caption>
			<h:inputHidden value="#{monitoria.confirmButton}" />
			<h:inputHidden value="#{monitoria.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{monitoria.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{monitoria.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>
				<td colspan="3"><h:selectOneMenu style="width: 400px"
					value="#{monitoria.obj.departamento.id}"
					disabled="#{monitoria.readOnly}" id="departamento"
					disabledClass="#{monitoria.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu value="#{monitoria.obj.area.id}"
					disabled="#{monitoria.readOnly}" disabledClass="#{monitoria.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{monitoria.obj.subArea.id}"
					disabled="#{monitoria.readOnly}" disabledClass="#{monitoria.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE A ÁREA <--" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Nome da Disciplina:</th>
				<td><h:inputText value="#{monitoria.obj.nomeDisciplina}"
					size="60" maxlength="255" readonly="#{monitoria.readOnly}"
					id="nomeDisciplina" /></td>
			</tr>

			<tr>
				<th class="required">Monitor:</th>
				<td><h:inputText value="#{monitoria.obj.monitor}" size="60"
					maxlength="255" readonly="#{monitoria.readOnly}" id="monitor" /></td>
			</tr>
			<tr>
				<th>Instituição:</th>
				<td><h:inputText value="#{monitoria.obj.instituicao}" size="60"
					maxlength="255" readonly="#{monitoria.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{monitoria.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{monitoria.readOnly}"
					id="periodoInicio" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{monitoria.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{monitoria.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>

			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{monitoria.obj.informacao}" size="60"
					maxlength="255" readonly="#{monitoria.readOnly}" id="informacao" /></td>
			</tr>
			<tr>
				<th class="required">Nome do Monitor:</th>
				<td><h:inputText value="#{monitoria.obj.nomemonitor}" size="60"
					maxlength="255" readonly="#{monitoria.readOnly}" id="nomemonitor" /></td>
			</tr>
			<tr>
				<th class="required">Agência Financiadora:</th>

				<td><h:selectOneMenu
					value="#{monitoria.obj.entidadeFinanciadora.id}"
					disabled="#{monitoria.readOnly}"
					disabledClass="#{monitoria.disableClass}" id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{monitoria.confirmButton}" action="#{monitoria.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{monitoria.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
