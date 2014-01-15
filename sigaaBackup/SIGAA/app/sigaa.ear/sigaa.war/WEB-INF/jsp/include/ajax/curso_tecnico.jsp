
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<html:text property="${nomeAjax}" styleId="sourceAutoCursoTecnico" size="60" onfocus="cursoOnFocus(e)"/>

<ajax:autocomplete source="sourceAutoCursoTecnico" target="targetAutoCursoTecnico"
	baseUrl="/sigaa/ajaxCursoTecnico" className="autocomplete"
	indicator="indicator" minimumCharacters="3" parameters="tipoCurso=${tipoCurso}"
	parser="new ResponseXmlToHtmlListParser()" />

<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>

<html:hidden styleId="targetAutoCursoTecnico" property="${idAjax}" styleClass="contentLink"/>

<script type="text/javascript">
		// Quem quiser usar, deve re-escrever no final da sua jsp
		function cursoOnFocus(e) {
		}
	</script>