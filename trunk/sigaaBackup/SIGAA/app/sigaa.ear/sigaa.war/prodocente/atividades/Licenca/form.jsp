<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Licença/Afastamento</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{licenca.listar}" value="Listar Licenças/Afastamentos Cadastrados"/>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Licença/Afastamento</caption>
			<h:inputHidden value="#{licenca.confirmButton}" />
			<h:inputHidden value="#{licenca.obj.id}" />
			<tr>

					<th class="required">Docente:</th>

					<td>
						<h:inputHidden id="id" value="#{licenca.obj.servidor.id}"/>
						<h:inputText id="nomeServidor"
							value="#{licenca.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> </td>
						<td>
						<span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>

				</tr>
			<tr>
				<th class="required">Afastamento:</th>

				<td><h:selectOneMenu value="#{licenca.obj.afastamento.id}"
					disabled="#{licenca.readOnly}"
					disabledClass="#{licenca.disableClass}" id="afastamento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{afastamentoProdocente.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar
					value="#{licenca.obj.periodoInicio}" size="10"
					maxlength="10" readonly="#{licenca.readOnly}"
					id="periodoInicio" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{licenca.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{licenca.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=3><h:commandButton
						value="#{licenca.confirmButton}" action="#{licenca.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{licenca.cancelar}" onclick="#{confirm}" immediate="true"/></td>
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
