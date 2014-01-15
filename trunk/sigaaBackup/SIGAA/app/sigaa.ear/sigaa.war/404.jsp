
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="br.ufrn.comum.dominio.Sistema"%>

<c:set var="sistema" scope="request" value="<%= Sistema.SIGAA %>"/>
<c:import url="/public/404.jsp" context="/shared"/>
