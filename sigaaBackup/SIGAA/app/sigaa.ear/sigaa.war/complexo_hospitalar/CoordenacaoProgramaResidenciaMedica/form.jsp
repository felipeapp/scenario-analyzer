<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Coordenação de Residência Médica</h2>

 <h:form id="form">
	
	<table class=formulario width="550px;" >
		<caption class="formulario">Cadastro de Coordenação de Residência Médica</caption>
			<h:inputHidden value="#{coordenacaoResidenciaMedica.confirmButton}" />
			<h:inputHidden value="#{coordenacaoResidenciaMedica.obj.id}" />
			<tr>
				<th class="required">Programa:</th>
				<td>
				
					<h:selectOneMenu value="#{coordenacaoResidenciaMedica.obj.programaResidenciaMedica.id}" style="width: 80%"
						disabled="#{coordenacaoResidenciaMedica.readOnly}" disabledClass="#{coordenacaoResidenciaMedica.disableClass}" id="programaResidencia">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE UM PROGRAMA DE RESIDÊNCIA MÉDICA --- " />
						<f:selectItems value="#{programaResidencia.allUnidadeCombo}" />
					</h:selectOneMenu>
				 </td>
			</tr>
			<tr>
				<th class="required">Servidor:</th>
				<td> 
					<h:inputHidden id="idServidor" value="#{coordenacaoResidenciaMedica.obj.servidor.id}" />
					<h:inputText id="nome" value="#{coordenacaoResidenciaMedica.obj.servidor.pessoa.nome}" style="width: 400px;"/>
					<ajax:autocomplete
						source="form:nome" target="form:idServidor"
						baseUrl="/sigaa/ajaxServidor" className="autocomplete" indicator="indicator" minimumCharacters="3" 
						parameters="tipo=todos" parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none; "><img src="/sigaa/img/indicator.gif" /></span>
			     </td>
			</tr>
			<tr>
				<th class="required">Data Inicial:</th>
				<td><t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" id="inicio" 
					value="#{coordenacaoResidenciaMedica.obj.inicio}" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" />
				</td>
			</tr>
			<tr>
				<th class="required">Data Final:</th>
				<td><t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" id="fim" 
					value="#{coordenacaoResidenciaMedica.obj.fim}" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" />
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
  				 <h:commandButton id="btConfirmarAcao"
				   value="#{coordenacaoResidenciaMedica.confirmButton}" action="#{coordenacaoResidenciaMedica.cadastrar}" />
				 <h:commandButton value="Cancelar" id="btCancelar" action="#{coordenacaoResidenciaMedica.cancelar}" onclick="#{confirm}" immediate="true"/>
			</tr>
		</tfoot>
	</table>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>