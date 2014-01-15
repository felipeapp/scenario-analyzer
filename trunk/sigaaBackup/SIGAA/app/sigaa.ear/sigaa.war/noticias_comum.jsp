<%@page import="br.ufrn.comum.dominio.UsuarioGeral"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="br.ufrn.arq.web.jsf.AbstractController"%>
<%

/*
	Redireciona pra SIGAdmin, pra listar todas as noticias do portal
*/



	String portal = request.getParameter("portal");
	
	String url = "/admin/public/noticia_portal/index.jsf?portal=" + portal;
	
	AbstractController cont = new AbstractController();
	cont.logarSistema(url, Sistema.SIGAA, Sistema.SIGADMIN, 
			(UsuarioGeral) session.getAttribute("usuario"), request, response);

%>