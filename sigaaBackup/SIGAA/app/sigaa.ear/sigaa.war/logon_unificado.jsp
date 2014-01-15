<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form action="${ configSistema['linkSigaa'] }/sigaa/logar.do?dispatch=logOn" method="post" name="loginForm">
	<input type="hidden" name="width" id="wid0th" />
	<input type="hidden" name="height" id="height" />
	<input type="hidden" name="unificado" value="true" />

		<script>
		document.getElementById('width').value = screen.width;
		document.getElementById('height').value = screen.height;
	</script>

<table align="center" width="80%" cellspacing="3" cellpadding="3">
<tbody>
	<tr>
		<th>  Usuário: </th>
		<td> <input type="text" name="user.login" size="20" /> </td>
	</tr>
	<tr>
		<th> Senha: </th>
		<td> <input type="password" name="user.senha" size="20" /> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="2"></td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<input type="submit" value="Entrar"/>
		</td>
	</tr>
</tfoot>
</table>
</form>
