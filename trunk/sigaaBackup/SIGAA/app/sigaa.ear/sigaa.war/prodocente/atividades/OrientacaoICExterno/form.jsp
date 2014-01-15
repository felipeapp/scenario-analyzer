<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> &gt; Orientação IC Externo</h2>

	<h:form id="form">
		<div class="infoAltRem" style="width: 100%"><h:graphicImage
			value="/img/listar.gif" style="overflow: visible;" /> <h:commandLink
			action="#{orientacoesICExternoBean.direcionar}"
			value="Listagem do IC Externo cadastrados." /></div>


		<table class="formulario" width="100%">
			<caption class="listagem">Cadastro IC Externo</caption>
			<h:inputHidden value="#{orientacoesICExternoBean.obj.id}" />
			<h:inputHidden value="#{orientacoesICExternoBean.confirmButton}" />

			<tr>
				<th class="required" width="25%">Nome do Orientando:</th>
				<td><h:inputText
					value="#{orientacoesICExternoBean.obj.nomeOrientando}" size="80"
					maxlength="255" readonly="#{orientacoesICExternoBean.readOnly}"
					id="nomeOrientando" /></td>
			</tr>

			<tr>
				<th class="required">Tipo da Bolsa:</th>
				<td><h:selectOneMenu
					value="#{orientacoesICExternoBean.obj.tipoBolsa}" id="tipoBolsa">
					<f:selectItem itemValue="1" itemLabel="Instituicional" />
					<f:selectItem itemValue="2" itemLabel="Pibic" />
					<f:selectItem itemValue="3" itemLabel="Outra" />
					<a4j:support event="onchange" reRender="form"/>
				</h:selectOneMenu></td>
			</tr>

			<tr id="outra">
				<th <c:if test="${ orientacoesICExternoBean.obj.tipoBolsa == 3 }">class="obrigatorio"</c:if>>Nome da Bolsa:</th>
				<td><h:inputText readonly="#{orientacoesICExternoBean.readOnly}"
					value="#{orientacoesICExternoBean.obj.tipoBolsaOutra}" 
					size="80" maxlength="255" id="tipoBolsaOutra" /> 
					<ufrn:help img="/img/prodocente/help.png">Preenchimento obrigatório caso o Tipo da bolsa, informado seja Outra. </ufrn:help>
				</td>
			</tr>

			<tr>
				<th class="required">Instituição de Ensino:</th>
				<td><h:selectOneMenu
					value="#{orientacoesICExternoBean.obj.instituicao.id}"
					id="tipoInstituicao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{instituicoesEnsino.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Data de Início da Orientação:</th>
				<td>
					<t:inputCalendar
						value="#{orientacoesICExternoBean.obj.dataInicio}" size="10"
						maxlength="10" readonly="#{orientacoesICExternoBean.readOnly}"
						id="dataInicio" renderAsPopup="true" renderPopupButtonAsImage="true" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th class="required">Data do Fim da Orientação:</th>
				<td>
					<t:inputCalendar
						value="#{orientacoesICExternoBean.obj.dataFim}" size="10"
						maxlength="10" readonly="#{orientacoesICExternoBean.readOnly}"
						id="dataFim" renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btConfirmarAcao"
							value="#{orientacoesICExternoBean.confirmButton}"
							action="#{orientacoesICExternoBean.validacao}" /> 
						<h:commandButton 
							value="Cancelar" id="btCancelar" onclick="#{confirm}"
							action="#{orientacoesICExternoBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>