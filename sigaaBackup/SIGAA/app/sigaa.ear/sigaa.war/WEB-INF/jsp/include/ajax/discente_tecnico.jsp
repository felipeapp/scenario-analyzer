
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<html:text property="${nomeAjax}" styleId="modelDiscente" size="60" />

<ajax:autocomplete source="modelDiscente" target="makeDiscente"
	baseUrl="/sigaa/ajaxDiscenteTecnico" className="autocomplete"
	indicator="indicator" minimumCharacters="3" parameters=""
	parser="new ResponseXmlToHtmlListParser()" />

<span id="indicator" style="display:none; "> <img
	src="/sigaa/img/indicator.gif" /> </span>

<html:hidden styleId="makeDiscente" property="${idAjax}" styleClass="contentLink"/>