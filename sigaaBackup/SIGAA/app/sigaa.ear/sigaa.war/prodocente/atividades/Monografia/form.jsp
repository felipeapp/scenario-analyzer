<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Monografia</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{monografia.listar}" value="Listar Monografias Cadastradas"/>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>

	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Monografia</caption>
			<h:inputHidden value="#{monografia.confirmButton}" />
			<h:inputHidden value="#{monografia.obj.id}" />


			<tr>
					<th>Discente Externo:</th>
					<td>
					 <h:selectBooleanCheckbox
							value="#{monografia.obj.discenteExterno}" id="discenteExterno"
							readonly="#{monografia.readOnly}" onchange="submit()" />
					 <ufrn:help img="/img/prodocente/help.png">Discente cuja graduação não foi cursada na ${ configSistema['siglaInstituicao'] }</ufrn:help>
					</td>
			</tr>

			<tr>
				<th class="required">Orientando:</th>
				<td>
					<c:if test="${ not monografia.obj.discenteExterno}">
						<h:inputHidden id="idDiscente" value="#{ monografia.obj.aluno.id }"/>
			 			<h:inputText id="nomeDiscente"
						value="#{monografia.obj.aluno.pessoa.nome}" size="60" />

						<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G,status=todos"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
					
					</c:if>
					<c:if test="${monografia.obj.discenteExterno}">
						<h:inputText id="orientando" maxlength="100"
						value="#{monografia.obj.orientando}" size="60" readonly="#{monografia.readOnly}" />
					</c:if>
				</td>
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
				<th>Informações complementares:</th>
				<td><h:inputText value="#{monografia.obj.informacao}" size="60"
					maxlength="255" readonly="#{monografia.readOnly}" id="informacao" /></td>
			</tr>

			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{monografia.obj.periodoInicio}" onkeypress="return(formataData(this, event))"
					size="10" maxlength="10" readonly="#{monografia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoInicio" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{monografia.obj.periodoFim}" onkeypress="return(formataData(this, event))"
					size="10" maxlength="10" readonly="#{monografia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoFim" /></td>
			</tr>

			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu style="width: 380px;" value="#{monografia.obj.area.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="area"
					valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu style="width: 380px;" value="#{monografia.obj.subArea.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="subArea">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{producao.subArea}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu style="width: 380px;" value="#{monografia.obj.departamento.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="departamento">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Monografia:</th>
				<td><h:selectOneMenu style="width: 380px;"
					value="#{monografia.obj.tipoOrientacao.id}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="monografia">
					<f:selectItem itemValue="1" itemLabel="Graduação" />

				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo de Orientação:</th>
				<td><h:selectOneMenu style="width: 380px;"
					value="#{monografia.obj.orientacao}"
					disabled="#{monografia.readOnly}"
					disabledClass="#{monografia.disableClass}" id="tipoOrientacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItem itemValue="C" itemLabel="Co-Orientador" />
					<f:selectItem itemValue="O" itemLabel="Orientador" />
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
