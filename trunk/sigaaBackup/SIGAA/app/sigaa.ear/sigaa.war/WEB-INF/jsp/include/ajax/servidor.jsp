
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<html:text property="${nomeAjax}" styleId="nomeServidor" size="60" onfocus="servidorOnFocus()"/>

<ajax:autocomplete source="nomeServidor" target="idServidor"
	baseUrl="/sigaa/ajaxServidor" className="autocomplete"
	indicator="indicatorServidor" minimumCharacters="3" parameters="tipo=${tipo}"
	parser="new ResponseXmlToHtmlListParser()" />

<span id="indicatorServidor" style="display:none; "> <img
	src="/sigaa/img/indicator.gif" /> </span>

<html:hidden styleId="idServidor" property="${idAjax}" styleClass="contentLink"/>


	<script type="text/javascript">
		// Quem quiser usar, deve re-escrever no final da sua jsp
		function servidorOnFocus() {
		}
	</script>

