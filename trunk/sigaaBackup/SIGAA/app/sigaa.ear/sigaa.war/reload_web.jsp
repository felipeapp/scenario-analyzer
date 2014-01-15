<%@page import="br.ufrn.sigaa.arq.struts.*" %>
<%

	SigaaController controller = (SigaaController) application.getAttribute("strutsServlet");
	controller.reload();
	out.println("Struts recarregado");

%>