<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>
<f:view>
	<h2><ufrn:subSistema /> > Relatório Pesquisa</h2>
	
	<h:form>
		<div class="infoAltRem" style="width: 100%">
		<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
		<h:commandLink action="#{relatorio.listar}"
			value="Listar Relatórios Cadastradas"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="95%">
			<caption class="listagem">Cadastro de relatorio</caption>
			<h:inputHidden value="#{relatorio.confirmButton}" />
			<h:inputHidden value="#{relatorio.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{relatorio.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{relatorio.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th>Titulo:</th>
				<td><h:inputText value="#{relatorio.obj.titulo}"
					size="60" maxlength="255" readonly="#{relatorio.readOnly}"
					id="titulo" /></td>
			</tr>

			<tr>
				<th class="required">Agência Financiadora:</th>
				<td><h:selectOneMenu value="#{relatorio.obj.agencia.id}"
					disabled="#{relatorio.readOnly}" disabledClass="#{relatorio.disableClass}"
					id="ies">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Data Aprovação:</th>
				<td><t:inputCalendar value="#{relatorio.obj.dataAprovacao}"
					size="10" maxlength="10" readonly="#{relatorio.readOnly}"
					id="periodoInicio" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{relatorio.obj.informacao}" size="60"
					maxlength="255" readonly="#{relatorio.readOnly}" id="informacao" /></td>
			</tr>

			<tr>
				<th class="required">Tipo de Participação:</th>

				<td><h:selectOneMenu
					value="#{relatorio.obj.participacao.id}"
					disabled="#{relatorio.readOnly}"
					disabledClass="#{relatorio.disableClass}" id="tipoParticipacao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoParticipacao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo de Região:</th>

				<td><h:selectOneMenu
					value="#{relatorio.obj.tipoRegiao.id}"
					disabled="#{relatorio.readOnly}"
					disabledClass="#{relatorio.disableClass}" id="tipoRegiao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Pago: <h:selectBooleanCheckbox value="#{relatorio.obj.pago}"
					disabled="#{relatorio.readOnly}"
					 id="pago"/></th>
				<td>
				Destaque: <h:selectBooleanCheckbox value="#{relatorio.obj.destaque}"
					disabled="#{relatorio.readOnly}"
					 id="destaque"/>
				Relatório Final: <h:selectBooleanCheckbox value="#{relatorio.obj.relatorioFinal}"
					disabled="#{relatorio.readOnly}"
					 id="relatorioFinal"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{relatorio.confirmButton}" action="#{relatorio.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{relatorio.cancelar}" /></td>
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
