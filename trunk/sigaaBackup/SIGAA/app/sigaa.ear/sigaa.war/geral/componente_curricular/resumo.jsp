<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>

<f:view>
<c:if test="${acesso.discente && componenteCurricular.portalDiscente}">
	<%@include file="/portais/discente/menu_discente.jsp" %>
</c:if>
<c:if test="${acesso.docente && componenteCurricular.portalDocente}">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</c:if>
<c:if test="${acesso.coordenadorCursoStricto && componenteCurricular.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp" %>
</c:if>	
	<%@include file="/geral/componente_curricular/include/resumo.jsp" %>
	<br>
	<center>
	<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
