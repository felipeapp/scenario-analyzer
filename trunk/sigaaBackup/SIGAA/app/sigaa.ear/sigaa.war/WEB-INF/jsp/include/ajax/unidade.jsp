
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<html:text property="${nomeAjax}" styleId="nomeUnidade" onfocus="unidadeOnFocus()" onchange="unidadeOnChange()" onclick="unidadeOnClick()" style="width:80%" />

<ajax:autocomplete source="nomeUnidade" target="idUnidade"
	baseUrl="/sigaa/ajaxUnidade" className="autocomplete"
	indicator="indicatorUnidade" minimumCharacters="3" parameters="tipo=${tipo}"
	parser="new ResponseXmlToHtmlListParser()" />

<span id="indicatorUnidade" style="display:none; "> <img
	src="/sigaa/img/indicator.gif" /> </span>

<html:hidden styleId="idUnidade" property="${idAjax}" styleClass="contentLink"/>


	<script type="text/javascript">
		// Quem quiser usar, deve re-escrever no final da sua jsp
		function unidadeOnFocus() {
		}
		function unidadeOnChange() {
		}
		function unidadeOnClick() {
		}
	</script>

