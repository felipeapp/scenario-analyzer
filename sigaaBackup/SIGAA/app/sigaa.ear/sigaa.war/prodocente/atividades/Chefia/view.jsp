<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Chefia</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> 
	  <h:commandLink action="#{chefia.listar}" value="Listar Chefias Cadastradas"/>
	 </div>
    </h:form>
	
	<h:messages showDetail="true"></h:messages>	
	
	
	<h:form id="form">
	<table class=formulario width="95%">
			<caption class="listagem">Cadastro de Chefia</caption>
			<h:inputHidden value="#{chefia.confirmButton}" />
			<h:inputHidden value="#{chefia.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{chefia.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{chefia.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Publicação:</th>
				<td><h:inputText value="#{chefia.obj.publicacao}" size="60"
					maxlength="255" readonly="#{chefia.readOnly}" id="publicacao" /></td>
			</tr>
			<tr>
				<th>Autoridade:</th>
				<td><h:inputHidden id="id1" value="#{chefia.obj.autoridade.id}"></h:inputHidden>
				<h:inputText id="nomeServidor1"
					value="#{chefia.obj.autoridade.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor1" target="form:id1"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator2" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator2"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Data do Documento:</th>
				<td><t:inputCalendar value="#{chefia.obj.dataDocumento}"
					size="10" maxlength="10" readonly="#{chefia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataDocumento"/></td>
			</tr>

			<tr>
				<th class="required">Data de Publicação:</th>
				<td><t:inputCalendar value="#{chefia.obj.dataPublicacao}"
					size="10" maxlength="10" readonly="#{chefia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataPublicacao"/></td>
			</tr>
			<tr>
				<th>Data Fim da Validade:</th>
				<td><t:inputCalendar value="#{chefia.obj.dataFinal}"
					size="10" maxlength="10" readonly="#{chefia.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFinal"/></td>
			</tr>
			<tr>
				<th>Remunerado</th>
				<td><h:selectBooleanCheckbox
					value="#{chefia.obj.remunerado}"
					id="remunerado" disabled="#{chefia.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Tipo de Chefia:</th>

				<td><h:selectOneMenu value="#{chefia.obj.tipoChefia.id}"
					disabled="#{chefia.readOnly}"
					disabledClass="#{chefia.disableClass}" id="idTipoChefia">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{tipoChefia.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{chefia.confirmButton}"
						action="#{chefia.cadastrar}" /> <h:commandButton value="Cancelar"
						action="#{chefia.cancelar}" /></td>
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
