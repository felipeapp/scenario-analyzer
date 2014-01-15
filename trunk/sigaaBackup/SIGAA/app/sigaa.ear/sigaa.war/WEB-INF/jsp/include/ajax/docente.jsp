<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<input type="hidden" id="buscaAjaxDocente">
<input type="hidden" id="inativos" value="${buscaInativos}">
<html:hidden styleId="idDocente" property="${idAjax}" styleClass="contentLink" />

<table class="buscaAjax">
	<tr class="titulo">
		<td width="5%">
			<input type="radio" name="tipoAjaxDocente" onclick="buscarDocentePor('buscaAjaxDocenteUFRN')" value="ufrn" 
					id="buscaAjaxDocenteUFRN" class="noborder">
		</td>
		<td width="20%" align="left">
			<label onclick="buscarDocentePor('buscaAjaxDocenteUFRN')">Todos da ${ configSistema['siglaInstituicao'] }</label>
		</td>
		<td width="5%">
			<input type="radio" name="tipoAjaxDocente" onclick="buscarDocentePor('buscaAjaxDocenteUnidade')" value="unidade" 
					id="buscaAjaxDocenteUnidade" class="noborder">
		</td>
		<td width="35%" align="left">
			<label onclick="buscarDocentePor('buscaAjaxDocenteUnidade')">Somente da minha unidade</label>
		</td>
		<c:if test="${ empty somenteInternos }">
			<td width="5%">
				<input type="radio" name="tipoAjaxDocente" onclick="buscarDocentePor('buscaAjaxDocenteExterno')" 
						value="externo${ externoLato ? 'Lato' : '' }" id="buscaAjaxDocenteExterno" class="noborder">
			</td>
			<td align="left">
				<label onclick="buscarDocentePor('buscaAjaxDocenteExterno')">Somente externos</label>
			</td>
		</c:if>
	</tr>
	<tr>
		<td colspan="6" style="height: 25px">
			<html:text property="${nomeAjax}" styleId="paramAjaxDocente" size="70" onfocus="docenteOnFocus()" onchange="docenteOnChange()" onclick="docenteOnClick()"/>
			<c:if test="${obrigatorio}">
				<span class="required">&nbsp;</span>
			</c:if>
			<span id="indicatorDocente" style="display:none;">
			<img src="/sigaa/img/indicator.gif" /></span>
		</td>
	</tr>
</table>

<ajax:autocomplete source="paramAjaxDocente" target="idDocente"	baseUrl="/sigaa/ajaxDocente" className="autocomplete"
		indicator="indicatorDocente" minimumCharacters="3" parameters="tipo={buscaAjaxDocente},inativos={inativos}"
		parser="new ResponseXmlToHtmlListParser()" />

<span id="indicatorDocente" style="display:none; ">
	<img src="/sigaa/img/indicator.gif" />
</span>

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function docenteOnFocus() {
	}
	function docenteOnChange() {
	}
	function docenteOnClick() {
	}
	
	function buscarDocentePor(radio) {
		$('buscaAjaxDocente').value = $(radio).value;
		marcaCheckBox(radio);
		$('paramAjaxDocente').focus();
	}

	window.onload = function() {
		buscarDocentePor('buscaAjaxDocenteUFRN');
	};
</script>
