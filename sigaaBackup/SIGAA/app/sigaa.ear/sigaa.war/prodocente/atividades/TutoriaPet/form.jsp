<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Tutoria PET</h2>
	<h:messages showDetail="true"></h:messages>

	<h:form>
	 <div class="infoAltRem" style="width:80%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tutoriaPet.listar}" value="Listar Tutorias Cadastradas"/>
	 </div>
    </h:form>
	<h:form id="form">
	<table class="formulario" width="80%">
			<caption class="listagem">Cadastro de Tutorias do PET</caption>
			<h:inputHidden value="#{tutoriaPet.confirmButton}" />
			<h:inputHidden value="#{tutoriaPet.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{tutoriaPet.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{tutoriaPet.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Grupo PET:</th>
				<td>
					<h:selectOneMenu value="#{tutoriaPet.obj.pet.id}"
						disabled="#{tutoriaPet.readOnly}" disabledClass="#{tutoriaPet.disableClass}" id="pet">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE UM GRUPO <--" />
						<f:selectItems value="#{petBean.allAtivosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar value="#{tutoriaPet.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{tutoriaPet.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio"/></td>
			</tr>
			<tr>
				<th class="required">Data de Fim:</th>
				<td><t:inputCalendar value="#{tutoriaPet.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{tutoriaPet.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{tutoriaPet.confirmButton}" action="#{tutoriaPet.cadastrar}" id="btnCadastrar" /> 
						<h:commandButton value="Cancelar" action="#{tutoriaPet.cancelar}" onclick="#{confirm}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
