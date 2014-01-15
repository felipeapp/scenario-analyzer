<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<input type="hidden" id="buscaAjaxDisciplina" >
<table class="buscaAjax">
	<tr class="titulo">
	<td width="5%">
	<input type="radio" name="tipoAjaxDisciplina" onclick="buscarDisciplinaPor('buscaAjaxNomeDisciplina')" value="nome" id="buscaAjaxNomeDisciplina" class="noborder">
	</td>
	<td width="17%" align="left">
	<label onclick="buscarDisciplinaPor('buscaAjaxNomeDisciplina')">Nome</label>
	</td>
	<td width="5%">
	<input type="radio" name="tipoAjaxDisciplina" onclick="buscarDisciplinaPor('buscaAjaxCodDisciplina')" value="codigo" id="buscaAjaxCodDisciplina" class="noborder">
	</td>
	<td align="left">
	<label onclick="buscarDisciplinaPor('buscaAjaxCodDisciplina')">Código</label>
	</td>
	</tr>

	<tr>
	<td colspan="4">
	<html:text property="${nomeAjax}" styleId="paramAjaxDisciplina" size="70" onfocus="disciplinaOnFocus()"  />
	<c:if test="${obrigatorio}">
	<span class="required">&nbsp;</span>
	</c:if>
	<span id="indicatorDisciplina" style="display:none; ">
	<img src="/sigaa/img/indicator.gif" /></span>
	</td>
	</tr>
</table>

<ajax:autocomplete source="paramAjaxDisciplina" target="idDisciplina"
	baseUrl="/sigaa/ajaxDisciplina" className="autocomplete" parameters="tipo={buscaAjaxDisciplina},nivel=${nivelEnsino}"
	indicator="indicatorDisciplina" minimumCharacters="3"
	parser="new ResponseXmlToHtmlListParser()" />

<html:hidden styleId="idDisciplina" property="${idAjax}" styleClass="contentLink" />

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function disciplinaOnFocus() {
	}

	function buscarDisciplinaPor(radio) {
		$('buscaAjaxDisciplina').value = $(radio).value;
		marcaCheckBox(radio);
		$('paramAjaxDisciplina').focus();
	}
	buscarDisciplinaPor('buscaAjaxNomeDisciplina');
</script>
