<%@page import="br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais"%>
<%@page import="br.ufrn.comum.dominio.UsuarioGeral"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="br.ufrn.arq.web.jsf.AbstractController"%>
<%
	String expirou = request.getParameter("expirou");
	UsuarioGeral usuario = (UsuarioGeral) session.getAttribute("usuario");
	
	AbstractController cont = new AbstractController();
	cont.logarSistema(RepositorioDadosInstitucionais.getLinkSigadmin() +"/admin/logar.jsf?passaporte=true&login=" + usuario.getLogin() + "&sistema=" + Sistema.SIGAA + "&url=/admin/public/usuario/alterar_dados.jsf",
			Sistema.SIGAA, Sistema.SIGADMIN,
			usuario, request,response);

	if (expirou != null) {
		request.getSession().invalidate();
	}
%>