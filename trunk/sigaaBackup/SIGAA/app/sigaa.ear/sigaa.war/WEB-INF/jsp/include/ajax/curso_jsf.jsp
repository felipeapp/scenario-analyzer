<input type="hidden" id="nivelAjaxCurso">

<table class="buscaAjax" >
	<tr class="titulo">
		<td width="3%">
			<input type="radio" name="nivelAjaxCurso" onclick="buscarCursoPor('buscaAjaxCursoMedio')" value="M" id="buscaAjaxCursoMedio" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarCursoPor('buscaAjaxCursoMedio')">Médio</label>
		</td>

		<td width="3%">
			<input type="radio" name="nivelAjaxCurso" onclick="buscarCursoPor('buscaAjaxCursoTecnico')" value="T" id="buscaAjaxCursoTecnico" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarCursoPor('buscaAjaxCursoTecnico')">Técnico</label>
		</td>

		<td width="3%">
			<input type="radio" name="nivelAjaxCurso" onclick="buscarCursoPor('buscaAjaxCursoGraduacao')" value="G" id="buscaAjaxCursoGraduacao" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarCursoPor('buscaAjaxCursoGraduacao')">Graduação</label>
		</td>

		<td width="3%">
			<input type="radio" name="nivelAjaxCurso" onclick="buscarCursoPor('buscaAjaxCursoLato')" value="L" id="buscaAjaxCursoLato" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarCursoPor('buscaAjaxCursoLato')">Lato</label>
		</td>

		<td width="3%">
			<input type="radio" name="nivelAjaxCurso" onclick="buscarCursoPor('buscaAjaxCursoStricto')" value="S" id="buscaAjaxCursoStricto" class="noborder">
		</td>
		<td align="left" width="30%">
			<label onclick="buscarCursoPor('buscaAjaxCursoStricto')">Stricto</label>
		</td>

	</tr>
	<tr>
		<td colspan="10" style="height: 25px">
			<input type="text" name="${nomeAjax}" onfocus="cursoOnFocus()" id="paramAjaxCurso" style="width:90%" value="${nomeCurso}"/>

			<c:if test="${obrigatorio}">
				<span class="required">&nbsp;</span>
			</c:if>
			<span id="indicatorCurso_${idAjax}" style="display:none;">
			<img src="/sigaa/img/indicator.gif" /></span>
		</td>
	</tr>
</table>

<ajax:autocomplete source="paramAjaxCurso" target="${idAjax}"
	baseUrl="/sigaa/ajaxCurso" className="autocomplete"
	indicator="indicatorCurso_${idAjax}" minimumCharacters="3" parameters="nivel={nivelAjaxCurso}"
	parser="new ResponseXmlToHtmlListParser()" />

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function cursoOnFocus() {
	}

	function buscarCursoPor(radio) {
		$('nivelAjaxCurso').value = $(radio).value;
		marcaCheckBox(radio);
		$('paramAjaxCurso').focus();
	}
	
	buscarCursoPor('buscaAjaxCursoMedio');
</script>
