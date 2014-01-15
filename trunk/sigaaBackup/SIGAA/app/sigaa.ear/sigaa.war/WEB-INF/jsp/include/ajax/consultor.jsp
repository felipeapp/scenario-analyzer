<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<input type="hidden" id="buscaAjaxConsultor" >
<html:hidden styleId="idConsultor" property="${idAjax}" styleClass="contentLink"/>

<table class="buscaAjax" >
	<tr class="titulo">
		<td width="3%">
			<input type="radio" name="tipoAjaxConsultor" onclick="buscarConsultorPor('buscaAjaxConsultorTodos')" value="todos" id="buscaAjaxConsultorTodos" class="noborder">
		</td>
		<td width="10%" align="left">
			<label onclick="buscarConsultorPor('buscaAjaxConsultorTodos')">Todos</label>
		</td>
		<td width="3%">
			<input type="radio" name="tipoAjaxConsultor" onclick="buscarConsultorPor('buscaAjaxConsultorInternos')" value="internos" id="buscaAjaxConsultorInternos" class="noborder">
		</td>
		<td width="18%" align="left">
			<label onclick="buscarConsultorPor('buscaAjaxConsultorInternos')">Somente internos</label>
		</td>
		<td width="3%">
			<input type="radio" name="tipoAjaxConsultor" onclick="buscarConsultorPor('buscaAjaxConsultorExternos')" value="externos" id="buscaAjaxConsultorExternos" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarConsultorPor('buscaAjaxConsultorExternos')">Somente externos</label>
		</td>
	</tr>
	<tr>
		<td colspan="6" style="height: 25px">
			<html:text property="${nomeAjax}" styleId="paramAjaxConsultor" size="80" onfocus="consultorOnFocus()" onchange="consultorOnChange()" />
			<c:if test="${obrigatorio}">
				<span class="required">&nbsp;</span>
			</c:if>
			<span id="indicatorConsultor" style="display:none;"><img src="/sigaa/img/indicator.gif" /></span>
		</td>
	</tr>
</table>

<ajax:autocomplete source="paramAjaxConsultor" target="idConsultor"
	baseUrl="/sigaa/ajaxConsultor" className="autocomplete"
	indicator="indicatorConsultor" minimumCharacters="3" parameters="tipo={buscaAjaxConsultor}"
	parser="new ResponseXmlToHtmlListParser()" />

<span id="indicatorConsultor" style="display:none; "> <img
	src="/sigaa/img/indicator.gif" /> </span>

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function consultorOnFocus() {
	}
	function consultorOnChange() {
	}

	function buscarConsultorPor(radio) {
		$('buscaAjaxConsultor').value = $(radio).value;
		marcaCheckBox(radio);
		$('paramAjaxConsultor').focus();
	}

	buscarConsultorPor('buscaAjaxConsultorTodos');
</script>
