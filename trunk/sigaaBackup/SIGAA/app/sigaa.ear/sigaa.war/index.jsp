<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% 
	if (request.getParameter("modo") != null && 
	  request.getParameter("modo").equals("classico"))
		session.setAttribute("modo", "classico"); 
%>

<c:redirect url="verTelaLogin.do" />