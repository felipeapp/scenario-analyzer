<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>

<%@ taglib uri="/tags/a4j" prefix="a4j" %>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
<jwr:style src="/ava/css/turma.css" media="all"/>

<c:set var="noticias" value="${ turmaVirtual.noticias }"/>

<f:view>
	<h1>${turmaVirtual.turma.descricaoSemDocente }</h1>
	<h:form id="formAva">
		<%@include file="/ava/aulas.jsp" %>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>
<script>window.print();</script>
${turmaVirtual.concluirImpressao }