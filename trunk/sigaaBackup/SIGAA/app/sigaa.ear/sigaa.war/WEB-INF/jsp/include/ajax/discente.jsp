<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript">
<!--
	function depoisDoAjax() {
	<c:if test="${usarFuncao}">
		retrieveInfoQualificacoes();
	</c:if>
	}
//-->
</script>

<input type="hidden" id="nivelAjaxDiscente" value="${ nivel }"/>
<html:hidden styleId="idDiscente" property="${idAjax}" styleClass="contentLink" />

<table class="buscaAjax" >
<c:if test="${ not empty opcoesNivel }">
	<tr class="titulo">
		<c:if test="${ not empty exibeTecnico }">
			<td width="5%">
				<input type="radio" name="nivelAjaxDiscente" onclick="buscarDiscentePor('buscaAjaxTecnico')" value="T" id="buscaAjaxTecnico" class="noborder">
			</td>
			<td width="16%" align="left">
				<label onclick="buscarDiscentePor('buscaAjaxTecnico')">Técnico</label>
			</td>
		</c:if>
		<c:if test="${ empty hideGraduacao }">
			<td width="5%">
				<input type="radio" name="nivelAjaxDiscente" onclick="buscarDiscentePor('buscaAjaxGraduacao')" value="G" id="buscaAjaxGraduacao" class="noborder">
			</td>
			<td width="16%" align="left">
				<label onclick="buscarDiscentePor('buscaAjaxGraduacao')">Graduação</label>
			</td>
		</c:if>
		<c:if test="${ not empty showResidente }">
			<td width="5%">
				<input type="radio" name="nivelAjaxDiscente" onclick="buscarDiscentePor('buscaAjaxResidente')" value="R" id="buscaAjaxResidente" class="noborder">
			</td>
			<td width="16%">
				<label onclick="buscarDiscentePor('buscaAjaxResidente')">Residente</label>
			</td>
		</c:if>
		<td width="5%">
			<input type="radio" name="nivelAjaxDiscente" onclick="buscarDiscentePor('buscaAjaxMestrado')" value="E" id="buscaAjaxMestrado" class="noborder">
		</td>
		<td width="16%" align="left">
			<label onclick="buscarDiscentePor('buscaAjaxMestrado')">Mestrado</label>
		</td>
		<td width="5%">
			<input type="radio" name="nivelAjaxDiscente" onclick="buscarDiscentePor('buscaAjaxDoutorado')" value="D" id="buscaAjaxDoutorado" class="noborder">
		</td>
		<td align="left">
			<label onclick="buscarDiscentePor('buscaAjaxDoutorado')">Doutorado</label>
		</td>
		<td> </td>
	</tr>
</c:if>
<tr>
	<td colspan="${ empty hideGraduacao ? 8 : 6 }">
		<c:if test="${obrigatorio}">
			<span class="required">&nbsp;</span>
		</c:if>
		<html:text property="${nomeAjax}" styleId="paramAjaxDiscente" size="70" onfocus="discenteOnFocus()" onchange="discenteOnChange()" style="width: 90%;"/>
		<span id="indicatorDiscente" style="display:none">
		<img src="/sigaa/img/indicator.gif" /></span>
	</td>
</tr>
</table>

<ajax:autocomplete
	source="paramAjaxDiscente"
	target="idDiscente"
	baseUrl="/sigaa/ajaxDiscente"
	className="autocomplete"
	parameters="nivel={nivelAjaxDiscente},status=${statusDiscente},ignorarUnidade=${ignorarUnidade}" 
	indicator="indicatorDiscente"
	minimumCharacters="3"
	postFunction="depoisDoAjax"
	parser="new ResponseXmlToHtmlListParser()" />

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function discenteOnFocus() {
	}
	function discenteOnChange() {
	}

	<c:if test="${ not empty opcoesNivel }">
		function buscarDiscentePor(radio) {
			$('nivelAjaxDiscente').value = $(radio).value;
			marcaCheckBox(radio);
			$('paramAjaxDiscente').focus();
		}
		<c:if test="${ empty hideGraduacao }">
			window.onload = function() {
				buscarDiscentePor('buscaAjaxGraduacao');
			};
		</c:if>
		<c:if test="${ not empty hideGraduacao }">
			window.onload = function() {
				buscarDiscentePor('buscaAjaxMestrado');
			};
		</c:if>
	</c:if>
</script>
