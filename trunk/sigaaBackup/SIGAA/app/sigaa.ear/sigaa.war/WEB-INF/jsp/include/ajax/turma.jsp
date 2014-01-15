
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<html:text property="${nomeAjax}" styleId="model" size="60" onfocus="turmaOnFocus()"/>

<ajax:autocomplete source="model" target="make"
	baseUrl="/sigaa/ajaxTurma" className="autocomplete"
	indicator="indicator" minimumCharacters="3" parameters=""
	parser="new ResponseXmlToHtmlListParser()" />

<span id="indicator" style="display:none; "> <img
	src="/sigaa/img/indicator.gif" /> </span>

<html:hidden styleId="make" property="${idAjax}" styleClass="contentLink"/>

<script type="text/javascript">
		// Quem quiser usar, deve re-escrever no final da sua jsp
		function turmaOnFocus() {
		}
	</script>