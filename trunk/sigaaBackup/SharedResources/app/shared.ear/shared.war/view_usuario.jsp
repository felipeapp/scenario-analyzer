<%@page import="br.ufrn.comum.dominio.UsuarioGeral"%>
<%@page import="br.ufrn.comum.dao.UsuarioDAO"%>
<%--
JSP que busca o usuário e informa exibe seus dados. 
Usado Scriptlet pois será passado direto para a JSP e se usar JSF para 
isso fica nos métodos Gets.
--%>
<%
	UsuarioDAO dao = new UsuarioDAO();
	UsuarioGeral user = dao.findByLogin(request.getParameter("login"));
	request.setAttribute("usuario", user);
%>
<html>
<head>
<link rel="stylesheet" media="all" type="text/css" href="/shared/css/ufrn.css"/>
</head>
<body>
<table width="300" class="formulario">
	<tr>
		<td align="center">
			<img src="/shared/verFoto?idFoto=${usuario.idFoto}" /> <br>
			Login: ${usuario.login}
		</td>
	</tr>
	<tfoot>
		<tr>
			<td>
				<b>${usuario.pessoa.nome} <br> </b>
				${usuario.unidade.nome } <br>
				E-mail: <i>${usuario.email } </i>, 
				Ramal: <i> ${usuario.ramal } </i> <br>
			</td>
		</tr>
	</tfoot>
</table>
</body>
</html>