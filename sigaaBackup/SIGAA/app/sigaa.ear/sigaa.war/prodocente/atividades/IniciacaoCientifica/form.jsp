<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Inicia��o Cient�fica</h2>
	
	<h:form>
		<div class="infoAltRem">
		<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />
		<h:commandLink action="#{iniciacaoCientifica.listar}"
			value="Listar Inicia��es Cient�ficas Cadastradas"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="95%">
			<caption class="listagem">Cadastro de Inicia��o Cient�fica</caption>
			<h:inputHidden value="#{iniciacaoCientifica.confirmButton}" />
			<h:inputHidden value="#{iniciacaoCientifica.obj.id}" />
			<tr>
				<th class="required">Nome do Projeto:</th>
				<td><h:inputText value="#{iniciacaoCientifica.obj.nomeProjeto}"
					size="60" maxlength="255"
					readonly="#{iniciacaoCientifica.readOnly}" id="nomeProjeto" /></td>
			</tr>
			<tr>
				<th class="required">Institui��o:</th>
				<td><h:inputText value="#{iniciacaoCientifica.obj.instituicao}"
					size="60" maxlength="255"
					readonly="#{iniciacaoCientifica.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">�rea:</th>

				<td><h:selectOneMenu value="#{iniciacaoCientifica.obj.area.id}"
					disabled="#{iniciacaoCientifica.readOnly}" disabledClass="#{iniciacaoCientifica.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-�rea:</th>

				<td><h:selectOneMenu value="#{iniciacaoCientifica.obj.subArea.id}"
					disabled="#{iniciacaoCientifica.readOnly}" disabledClass="#{iniciacaoCientifica.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE A �REA <--" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu
					value="#{iniciacaoCientifica.obj.departamento.id}"  style="width: 400px"
					disabled="#{iniciacaoCientifica.readOnly}"
					disabledClass="#{iniciacaoCientifica.disableClass}"
					id="departamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Orientando:</th>
					<td>
					<h:inputHidden id="idDiscente" value="#{ iniciacaoCientifica.obj.orientando }"/>
			 		<h:inputText id="nomeDiscente"
					value="#{iniciacaoCientifica.obj.orientando}" size="60" />
		
					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters=""
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
					</td>	
			</tr>
			<tr>
				<th class="required">Per�odo In�cio:</th>
				<td><t:inputCalendar value="#{iniciacaoCientifica.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{iniciacaoCientifica.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio"/></td>
			</tr>
			<tr>
				<th>Per�odo Fim:</th>
				<td><t:inputCalendar value="#{iniciacaoCientifica.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{iniciacaoCientifica.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim"/></td>
			</tr>
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{iniciacaoCientifica.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{iniciacaoCientifica.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th>Informa��o:</th>
				<td><h:inputText value="#{iniciacaoCientifica.obj.informacao}"
					size="60" maxlength="255"
					readonly="#{iniciacaoCientifica.readOnly}" id="informacao" /></td>
			</tr>
			<tr>
				<th class="required">Ag�ncia Financiadora:</th>

				<td><h:selectOneMenu
					value="#{iniciacaoCientifica.obj.entidadeFinanciadora.id}"
					disabled="#{iniciacaoCientifica.readOnly}"
					disabledClass="#{iniciacaoCientifica.disableClass}"
					id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{iniciacaoCientifica.confirmButton}"
						action="#{iniciacaoCientifica.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{iniciacaoCientifica.cancelar}" /></td>
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
