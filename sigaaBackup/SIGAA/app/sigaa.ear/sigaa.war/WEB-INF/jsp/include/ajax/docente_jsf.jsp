<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<c:if test="${empty contAjaxDocente }">
	<c:set var="contAjaxDocente" value="0" />
</c:if>
<c:set var="contAjaxDocente" value="${contAjaxDocente + 1}" />

<input type="hidden" id="buscaAjaxDocente_${contAjaxDocente}">
<table class="buscaAjax" style="border: solid 1px #CCCCCC;">
	<tr class="titulo">
		<td width="5%">
			<input type="radio" name="tipoAjaxDocente_${contAjaxDocente}" onclick="buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteUFRN_${contAjaxDocente}');resetNome();"
				value="ufrn" id="buscaAjaxDocenteUFRN_${contAjaxDocente}" class="noborder">
		</td>
		<td width="20%" align="left">
			<label onclick="buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteUFRN_${contAjaxDocente}')">Todos da ${ configSistema['siglaInstituicao'] }</label>
		</td>
		<td width="5%">
			<input type="radio" name="tipoAjaxDocente_${contAjaxDocente}" onclick="buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteUnidade_${contAjaxDocente}');resetNome();"
				value="unidade" id="buscaAjaxDocenteUnidade_${contAjaxDocente}" class="noborder">
		</td>
		<td width="35%" align="left">
			<label onclick="buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteUnidade_${contAjaxDocente}')">Somente da minha unidade</label>
		</td>
		<td width="5%">
			<input type="radio" name="tipoAjaxDocente_${contAjaxDocente}" onclick="buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteExterno_${contAjaxDocente}');resetNome();"
				value="externo" id="buscaAjaxDocenteExterno_${contAjaxDocente}" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteExterno_${contAjaxDocente}')">Somente externos</label>
		</td>
	</tr>
	<tr>
		<td colspan="6" style="height: 25px">
			<input type="text" name="${nomeAjax}" size="70" onfocus="docenteOnFocus()" id="paramAjaxDocente_${contAjaxDocente}" value="${nomeDocente}" onkeyup="CAPS(this)"/>

			<input type="hidden" name="${nomeAjaxDisable}" size="70" onfocus="docenteOnFocus()" id="paramAjaxDocente_${contAjaxDocente}" value="${nomeDocente}"/>

			<c:if test="${obrigatorio}">
				<span class="required">&nbsp;</span>
			</c:if>
			<span id="indicatorDocente_${idAjax}" style="display:none;">
			<img src="/sigaa/img/indicator.gif" /></span>
		</td>
	</tr>
</table>

<ajax:autocomplete source="paramAjaxDocente_${contAjaxDocente}" target="${idAjax}"
	baseUrl="/sigaa/ajaxDocente" className="autocomplete"
	indicator="indicatorDocente_${idAjax}" minimumCharacters="3" parameters="tipo={buscaAjaxDocente_${contAjaxDocente}},inativos=${inativos}, cedidos=${cedidos}"
	parser="new ResponseXmlToHtmlListParser()" />

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function resetNome(){
		$('paramAjaxDocente_${contAjaxDocente}').value = '';
	}
		
	function docenteOnFocus() {
	}

	function buscarDocentePor_${contAjaxDocente}(radio) {
		$('buscaAjaxDocente_${contAjaxDocente}').value = $(radio).value;
		marcaCheckBox(radio);
		$('paramAjaxDocente_${contAjaxDocente}').focus();
	}

	buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteUFRN_${contAjaxDocente}');
</script>
