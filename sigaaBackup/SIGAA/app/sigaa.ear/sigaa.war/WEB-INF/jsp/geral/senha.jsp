<%@page import="br.ufrn.comum.dominio.UsuarioGeral"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="br.ufrn.arq.web.jsf.AbstractController"%>
<%
	AbstractController cont = new AbstractController();
	cont.logarSistemaComum("/shared/senha.jsf", Sistema.SIGAA,
			(UsuarioGeral) session.getAttribute("usuario"), request,response);
%>