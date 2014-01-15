<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Formação Acadêmica</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Formação Acadêmica</caption>
			<h:inputHidden value="#{formacaoAcademica.confirmButton}" />
			<h:inputHidden value="#{formacaoAcademica.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{formacaoAcademica.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{formacaoAcademica.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">País:</th>

				<td><h:selectOneMenu value="#{formacaoAcademica.obj.pais.id}"
					disabled="#{formacaoAcademica.readOnly}"
					disabledClass="#{formacaoAcademica.disableClass}" id="pais">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{pais.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Formação:</th>
				<td><h:selectOneMenu value="#{formacaoAcademica.obj.formacao.id}"
					disabled="#{formacaoAcademica.readOnly}"
					disabledClass="#{formacaoAcademica.disableClass}" id="pais">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoQualificacao.allCombo}" />
					</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Grau:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.grau}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="grau" /></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.titulo}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Entidade:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.entidade}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="entidade" /></td>
			</tr>
			<tr>
				<th class="required">Tipo de Orientação:</th>
				<td><h:selectOneMenu value="#{formacaoAcademica.obj.tipoOrientacao.id}"
					disabled="#{formacaoAcademica.readOnly}"
					disabledClass="#{formacaoAcademica.disableClass}" id="pais">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoOrientacao.allCombo}" />
					</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Orientador:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.orientador}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="orientador" /></td>
			</tr>
			<tr>
				<th>Palavra-Chave 1:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.palavraChave1}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="palavraChave1" /></td>
			</tr>
			<tr>
				<th>Palavra-Chave2 :</th>
				<td><h:inputText value="#{formacaoAcademica.obj.palavraChave2}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="palavraChave2" /></td>
			</tr>
			<tr>
				<th>Palavra-Chave 3:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.palavraChave3}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="palavraChave3" /></td>
			</tr>
			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar
							value="#{formacaoAcademica.obj.dataInicio}" size="10"
							maxlength="10" readonly="#{formacaoAcademica.readOnly}"
							id="dataInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Data do Fim:</th>
				<td><t:inputCalendar
				value="#{formacaoAcademica.obj.dataFim}" size="10"
							maxlength="10" readonly="#{formacaoAcademica.readOnly}"
							id="dataFim"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Informações:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.informacoes}"
					size="60" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="informacoes" /></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu value="#{formacaoAcademica.obj.area.id}"
					disabled="#{formacaoAcademica.readOnly}" disabledClass="#{formacaoAcademica.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{formacaoAcademica.obj.subArea.id}"
					disabled="#{formacaoAcademica.readOnly}" disabledClass="#{formacaoAcademica.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE A ÁREA <--" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Especialidade:</th>

				<td><h:selectOneMenu
					value="#{formacaoAcademica.obj.especialidade.id}"
					disabled="#{formacaoAcademica.readOnly}"
					disabledClass="#{formacaoAcademica.disableClass}"
					id="especialidade">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{formacaoAcademica.confirmButton}"
						action="#{formacaoAcademica.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{formacaoAcademica.cancelar}" /></td>
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
