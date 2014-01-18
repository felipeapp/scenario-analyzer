
<%@page import="br.ufrn.comum.dao.SistemaDao"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="java.util.Map"%>
<%@page import="br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais" %>

<style type="text/css">
.painel {  background-color: #EFF3FA; padding: 5px; width: 16%; text-align: center; }
.sistemaAtual { background-color: #FFFFE4; } 
</style>
<%

	if (request.getSession().getServletContext().getAttribute("sistemasAtivos") == null) {
		SistemaDao dao = new SistemaDao();
		
		try {
			Sistema.setSistemasAtivos(dao.findMapaSistemasAtivos());
			Sistema.setCaixaPostalAtiva(dao.findMapaCaixaPostalAtiva());
			Sistema.setMemorandosAtivos(dao.findMapaMemorandosAtivos());
			request.getSession().getServletContext().setAttribute("sistemasAtivos", "true");
		} finally {
			dao.close();
		}
	}

	boolean sipacAtivo = Sistema.isSipacAtivo();
	boolean sigaaAtivo = Sistema.isSigaaAtivo();
	boolean sigrhAtivo = Sistema.isSigrhAtivo();
	boolean sigppAtivo = Sistema.isSigppAtivo();
	boolean sigedAtivo = Sistema.isSigedAtivo();
	boolean sigadminAtivo = Sistema.isSigadminAtivo();

	String s = request.getParameter("sistemaAtual");
	if (  s != null ) {
		
		if ( s.equals("sigaa") ) {
			request.setAttribute("sigaa", "sistemaAtual");
		} else if ( s.equals("sipac") ) {
			request.setAttribute("sipac", "sistemaAtual");
		} else if ( s.equals("sigrh") ) {
			request.setAttribute("sigrh", "sistemaAtual");
		} else if ( s.equals("sigpp") ) {
			request.setAttribute("sigpp", "sistemaAtual");
		} else if (s.equals("sigadmin") ) {
			request.setAttribute("sigadmin", "sistemaAtual");
		} else if (s.equals("siged") ) {
			request.setAttribute("siged", "sistemaAtual");
		}
	}
	if (request.getSession().getServletContext().getAttribute("configSistema") == null) {
		  Map<String, String> configs = RepositorioDadosInstitucionais.getAll();
		  request.getSession().getServletContext().setAttribute("configSistema", configs);
	}
%>

<% if ( ! "true".equals( request.getParameter("somenteSistemas") ) ) { %>
	<div align="center" style="margin: 0 auto; background: #EFF3FA; padding: 10px;">
		<p style="font-weight: bold; color: #F00;">ATENÇÃO!</p>
		<p style="width: 75%;">	O sistema diferencia letras maiúsculas de minúsculas APENAS na senha,
		portanto ela deve ser digitada da mesma maneira que no cadastro.</p>
	</div>
<% } %>

<div align="center" style="width: 100%; margin: 0 auto; padding: 10px 0;">
<table width="100%" align="center" style="border-collapse: separate; border-spacing: 3px">
	<tr>
		<% if (sigaaAtivo) { %>
		<td class="painel ${sigaa}">
			<a href="${ configSistema['linkSigaa'] }/sigaa/"/>
			${ configSistema['siglaSigaa'] }</a> <br> 
			(Acadêmico)
		</td>
		<% } %>
		<% if (sipacAtivo) { %>
		<td class="painel ${sipac}">
			<a href="${ configSistema['linkSipac'] }/sipac/"/>
			${ configSistema['siglaSipac'] }</a><br>
			(Administrativo)
		</td>
		<% } %>
		<% if (sigrhAtivo) { %>
		<td class="painel ${sigrh}">
			<a href="${ configSistema['linkSigrh'] }/sigrh/"/>
			${ configSistema['siglaSigrh'] }</a><br>
			(Recursos Humanos)
		</td>
		<% } %>
		<% if (sigppAtivo) { %>
		<td class="painel ${sigpp}">
			<a href="${ configSistema['linkSigpp'] }/sigpp/"/>
			${ configSistema['siglaSigpp'] }</a><br>
			(Planejamento e Projetos)
		</td>
		<% } %>
		<% if (sigedAtivo) { %>
		<td class="painel ${siged}">
			<a href="${ configSistema['linkSiged'] }/siged/"/>
			${ configSistema['siglaSiged'] }</a><br>
			(Gestão Eletrônica de Documentos)
		</td>
		<% } %>
		<% if (sigadminAtivo) { %>
		<td class="painel ${sigadmin}">
			<a href="${ configSistema['linkSigadmin'] }/admin/"/>
			${ configSistema['siglaSigadmin'] }</a><br>
			(Administração e Comunicação)
		</td>
		<% } %>
	</tr>
</table>
</div>