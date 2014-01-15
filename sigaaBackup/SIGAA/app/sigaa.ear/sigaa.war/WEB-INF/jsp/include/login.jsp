<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
<%
	if (AmbienteUtils.isNotSecure(request)) {
		String url = "https://" + request.getServerName() + request.getContextPath();
		response.sendRedirect(url);
		return;
	}	
%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:if test="${modo != 'classico' }">
	<script type="text/javascript">
		function isMobile(){	 		
			var a = navigator.userAgent||navigator.vendor||window.opera;
			if (/android|blackberry|ip(hone|od|ad)|kindle|maemo|opera m(ob|in)i|palm( os)?|symbian/i.test(a)) {			
				return true;
			} else {
				return false;
			}	
		}

		if (isMobile()) {
			window.location = "<%=request.getContextPath()%>/mobile/touch/login.jsf";
		}
	</script>
</c:if>


<%@page import="br.ufrn.comum.dominio.Sistema"%><c:import context="/shared" url="/sistemas.jsp?sistemaAtual=sigaa" var="sistemas" scope="request"/> 
${sistemas}

<% if (request.getParameter("msgSucesso") != null) { %>
<br>
 <table class="formulario" width="100%" align="center">
  <tr valign="middle">
  <td width="15%" align="center"><img src="img/warning.gif"></td>
  <td valign="middle" align="left" style="color: black;">
   	<li><b>${param['msgSucesso']} </b></li>
  </td></tr>
 </table>
<br>

<%}%>

<c:if test="${ param.discente != null }">
<div id="painel-erros">

	<ul class="info">
		<li> <big><b>Usuário Cadastrado com Sucesso</b></big> </li>
	</ul>
</div>
</c:if>
<br>

<c:choose>
	<c:when test="${param.acessibilidade == 'true'}">
		<div style="margin: 0 auto; width: 645px; ">
			<div style="float:left; width: 73%; margin: 0; padding: 0;">
				Perdeu o e-mail de confirmação de cadastro? <a href="${ configSistema['linkSigadmin'] }/admin/public/recuperar_codigo.jsf">Clique aqui para recuperá-lo.</a> <br/>
				Esqueceu o login? <a href="${ configSistema['linkSigadmin'] }/admin/public/recuperar_login.jsf">Clique aqui para recuperá-lo.</a> <br/>
				Esqueceu a senha? <a href="${ configSistema['linkSigadmin'] }/admin/public/recuperar_senha.jsf">Clique aqui para recuperá-la.</a> <br />
			</div>
	
			<div style="float:left; width: 24%; margin: 0 0 0 2px; padding: 0; background-color: #EFF3FA; padding: 5px; border: 1px solid #C8D5EC;">
				<img src="img/modoAcessibilidade.png" width="40px;" height="40px;" align="left" style="margin-right: 5px;" />
				 Modo Acessibilidade Ativado
			</div>
		</div>		
	</c:when>
	<c:otherwise>
		<center>
		Perdeu o e-mail de confirmação de cadastro? <a href="${ configSistema['linkSigadmin'] }/admin/public/recuperar_codigo.jsf">Clique aqui para recuperá-lo.</a> <br/>
		Esqueceu o login? <a href="${ configSistema['linkSigadmin'] }/admin/public/recuperar_login.jsf">Clique aqui para recuperá-lo.</a> <br/>
		Esqueceu a senha? <a href="${ configSistema['linkSigadmin'] }/admin/public/recuperar_senha.jsf">Clique aqui para recuperá-la.</a>
		</center>
	</c:otherwise>
</c:choose>

<br clear="all">
<c:if test="${ mensagem != '' }">
	<br />
	<center style="color: #922; font-weight: bold;">${ mensagem }</center>
</c:if>
<br>
<div class="logon" style="width:50%; margin: 0 auto;">
<h3> Entrar no Sistema </h3> 
<html:form action="/logar?dispatch=logOn" method="post" focus="user.login">
	<html:hidden property="width" styleId="width" />
	<html:hidden property="height" styleId="height" />
	<input type="hidden" name="urlRedirect" value="${param.urlRedirect}" />
	<input type="hidden" name="subsistemaRedirect" value="${param.subsistemaRedirect}" />
	<input type="hidden" name="acao" value="${ param.acao }" />
	<html:hidden property="acessibilidade" value="${ param.acessibilidade }"/>

	<script>
		document.getElementById('width').value = screen.width;
		document.getElementById('height').value = screen.height;
	</script>

<table align="center" width="100%" cellspacing="0" cellpadding="3">
<tbody>
	<tr>
		<th width="35%">  Usuário: </th>
		<td align="left"> <html:text property="user.login" size="20" value="${ param.login }"/></td>
	</tr>
	<tr>
		<th> Senha: </th>
		<td> <html:password property="user.senha" size="20" /> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="2" align="center">
			<html:submit><fmt:message key="botao.entrar" /></html:submit>
		</td>
	</tr>
</tfoot>
</table>
</html:form>

</div>

<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="5" align="center" width="33%">
			<br><br>
			<b>Professor ou Funcionário,</b> <br>
			caso ainda não possua cadastro no ${ configSistema['siglaSigaa'] },<br>clique no link abaixo.<br>
			<a href="${ configSistema['linkSigadmin'] }/admin/auto_cadastro/form.jsf?origem=<%= Sistema.SIGAA %>">
				<html:img page="/img/novo_usuario.gif" border="0" alt="Novo Usuário" width="16" height="16"/>
				<br>
				Cadastre-se
			</a>
		</td>
		<td colspan="5" align="center" width="33%">
			<br><br>
			<b>Aluno,</b><br>caso ainda não possua cadastro no ${configSistema['siglaSigaa']}, <br>clique no link abaixo.<br>
			<html:link href="/sigaa/public/cadastro/discente.jsf">
				<html:img page="/img/user.png" border="0" alt="Novo Usuário"/>
				<br>
				Cadastre-se
			</html:link>
		</td>
		<% /* 
		<td colspan="5" align="center" width="33%">
			<br><br>
			<b>Familiares,</b><br>caso ainda não possuam cadastro no ${configSistema['siglaSigaa']}, <br>clique no link abaixo.<br>
			<html:link href="/sigaa/public/cadastro/familiares.jsf">
				<html:img page="/img/familiares.png" border="0" alt="Novo Usuário"/>
				<br>
				Cadastre-se
			</html:link>
		</td>	
		 */
		%>
	</tr>
</table>
<br><br><br>
<table align="center">
	<tr>
		<td><html:link href="http://www.sistemas.ufrn.br/download/Firefox.exe" target="_blank"><html:img page="/img/firefox.jpg" border="0" width="20" height="20"/></html:link></td>
		<td><b>Este sistema é melhor visualizado utilizando o
			<html:link href="http://www.sistemas.ufrn.br/download/Firefox.exe" target="_blank">Mozilla Firefox</html:link>,
			para baixá-lo e instalá-lo,
			<html:link href="http://www.sistemas.ufrn.br/download/Firefox.exe" target="_blank">clique aqui</html:link></b>.
		</td>
	</tr>
	<tr>
		<td><html:link href="http://www.sistemas.ufrn.br/download/AdbeRdr910_pt_BR.exe" target="_blank"><html:img page="/img/adobe.jpg" border="0" width="20" height="20"/></html:link></td>
		<td><b>Para visualizar documentos é necessário utilizar o
			<html:link href="http://www.sistemas.ufrn.br/download/AdbeRdr910_pt_BR.exe" target="_blank">Adobe Reader</html:link>,
			para baixá-lo e instalá-lo,
			<html:link href="http://www.sistemas.ufrn.br/download/AdbeRdr910_pt_BR.exe" target="_blank">clique aqui</html:link></b>.
		</td>
	</tr>

</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>