<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="br.ufrn.arq.util.AmbienteUtils"%>

<%
	String uagent = request.getHeader("User-Agent").toLowerCase();
	String modo = request.getParameter("modo");
	String modoSessao = (String) session.getAttribute("modo");
	if (modo == "mobile" || (uagent != null && AmbienteUtils.isMobileUserAgent(uagent)) 
			&& modoSessao != "classico") 
		response.sendRedirect("/sigaa/mobile/touch/public/principal.jsf");
	else {
		response.sendRedirect("/sigaa/public/home.jsf");
	}

%>