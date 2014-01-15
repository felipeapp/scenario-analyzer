<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="usuario" value="${ sessionScope.usuario }" scope="request"/>
<c:import url="/public/autorizacao.jsp" context="/shared" />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
