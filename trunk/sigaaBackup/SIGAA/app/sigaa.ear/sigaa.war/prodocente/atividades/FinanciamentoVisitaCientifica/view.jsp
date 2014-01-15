<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Financiamento de Visita Científica</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de
			Finanaciamento de Visita Científica</caption>
			<h:inputHidden value="#{financiamentoVisitaCientifica.confirmButton}" />
			<h:inputHidden value="#{financiamentoVisitaCientifica.obj.id}" />
			<tr>
				<th class="required">Entidade:</th>
				<td><h:inputText
					value="#{financiamentoVisitaCientifica.obj.entidade}" size="60"
					maxlength="255"
					readonly="#{financiamentoVisitaCientifica.readOnly}" id="entidade" /></td>
			</tr>
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{estagio.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{estagio.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Valor:</th>
				<td><h:inputText
					value="#{financiamentoVisitaCientifica.obj.valor}" size="8"
					maxlength="255"
					readonly="#{financiamentoVisitaCientifica.readOnly}" id="valor" /></td>
			</tr>
			<tr>
				<th class="required">Visita Científica:</th>

				<td><h:selectOneMenu
					value="#{financiamentoVisitaCientifica.obj.visitaCientifica.id}"
					disabled="#{financiamentoVisitaCientifica.readOnly}"
					disabledClass="#{financiamentoVisitaCientifica.disableClass}"
					id="visitaCientifica">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{visitaCientifica.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{financiamentoVisitaCientifica.confirmButton}"
						action="#{financiamentoVisitaCientifica.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{financiamentoVisitaCientifica.cancelar}" /></td>
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
