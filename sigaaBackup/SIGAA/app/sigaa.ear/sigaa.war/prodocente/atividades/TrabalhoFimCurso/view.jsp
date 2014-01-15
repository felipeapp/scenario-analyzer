<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Trabalho de Fim de Curso</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Trabalho de Fim de Curso</caption>
			<h:inputHidden value="#{trabalhoFimCurso.confirmButton}" />
			<h:inputHidden value="#{trabalhoFimCurso.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{trabalhoFimCurso.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{trabalhoFimCurso.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{trabalhoFimCurso.obj.titulo}"
					size="60" maxlength="255" readonly="#{trabalhoFimCurso.readOnly}"
					id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu value="#{trabalhoFimCurso.obj.area.id}"
					disabled="#{trabalhoFimCurso.readOnly}"
					disabledClass="#{trabalhoFimCurso.disableClass}" id="area">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th  class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{trabalhoFimCurso.obj.subArea.id}"
					disabled="#{trabalhoFimCurso.readOnly}"
					disabledClass="#{trabalhoFimCurso.disableClass}" id="subArea">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{subArea.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{trabalhoFimCurso.obj.instituicao}"
					size="60" maxlength="255" readonly="#{trabalhoFimCurso.readOnly}"
					id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Orientando:</th>
				<td><h:inputText value="#{trabalhoFimCurso.obj.orientando}"
					size="60" maxlength="255" readonly="#{trabalhoFimCurso.readOnly}"
					id="orientando" /></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>
				<td><h:selectOneMenu
					value="#{trabalhoFimCurso.obj.departamento.id}" style="width: 400px"
					disabled="#{trabalhoFimCurso.readOnly}"
					disabledClass="#{trabalhoFimCurso.disableClass}" id="departamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Data da Defesa:</th>
				<td><t:inputCalendar value="#{trabalhoFimCurso.obj.dataDefesa}"
					size="10" maxlength="10" readonly="#{trabalhoFimCurso.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataDefesa"/></td>
			</tr>
			<tr>
				<th>Páginas:</th>
				<td><h:inputText value="#{trabalhoFimCurso.obj.paginas}"
					size="60" maxlength="255" readonly="#{trabalhoFimCurso.readOnly}"
					id="paginas" /></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{trabalhoFimCurso.obj.informacao}"
					size="60" maxlength="255" readonly="#{trabalhoFimCurso.readOnly}"
					id="informacao" /></td>
			</tr>

			<tr>
				<th class="required">Data Início:</th>
				<td><t:inputCalendar value="#{trabalhoFimCurso.obj.dataInicio}"
					size="10" maxlength="10" readonly="#{trabalhoFimCurso.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio"/></td>
			</tr>
			<tr>
				<th class="required">Agência Financiadora:</th>

				<td><h:selectOneMenu
					value="#{trabalhoFimCurso.obj.entidadeFinanciadora.id}"
					disabled="#{trabalhoFimCurso.readOnly}"
					disabledClass="#{trabalhoFimCurso.disableClass}"
					id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{trabalhoFimCurso.confirmButton}"
						action="#{trabalhoFimCurso.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{trabalhoFimCurso.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
