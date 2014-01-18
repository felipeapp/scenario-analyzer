<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="br.ufrn.arq.caixa_postal.Mensagem"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<br>
<br>
<br>
<br>
<br>
<br>


<table width="80%" align="center" bgcolor="#000080">

	<tr>
		<td class="erro" align="center"><br>
		<br>
		<img src="/shared/img/erro_autorizacao.gif" /><br>
		<font color="white"> Acesso Negado<br>
		Usuário Não Autorizado<br><br></font></td>
	</tr>
	<tr>
		<td><c:if test="${not empty papeis}">
			<table width="80%" align="center">
				<tr>
					<td colspan="2"><b><font color="white"> O Usuário não possui
					nenhum dos papéis listados abaixo para a unidade ${ usuario.unidade.codigoNome }:<br>
					<br><br>
					</font></b></td>
				</tr>
				<tr>
					<td><b><font color="white">Papel</font></b></td>
					<td><b><font color="white">Descrição</font></b></td>
				</tr>
				<c:forEach items="${papeis}" var="papel">
					<tr>
						<td><b><font color="white">${papel.nome}</font></b><br></td>
						<td><b><font color="white">${papel.descricao}</font></b><br></td>
					</tr>
				</c:forEach>
			</table>
		</c:if></td>
	</tr>
	<tr>
		<td><br>
		<table width="80%" align="center">
			<tr>
				<td>
					<b><font color="white"> Caso seja necessária a utilização desta funcionalidade, 
				
					<%
					Integer sistema = (Integer) request.getAttribute("sistema");
					if (sistema == null || sistema != Sistema.SIGADMIN) {
					%>
						<a href="javascript://nop/" accesskey="a" onclick="mensagem.show(<%="" + Mensagem.CHAMADO_SIPAC%>);">
							<font color="white">Clique Aqui</font>
						</a> 
					<% } else { %>
						<c:if test="${configSistema['caminhoAberturaChamado']==null}">
							<a href="#" onclick="window.open('/admin/novoChamadoAdmin.jsf?sistema=8', 'chamado', 'scrollbars=1,width=700,height=600')"><font color="white">Clique aqui</font></a>
						</c:if>
						
						<c:if test="${configSistema['caminhoAberturaChamado']!=null}">
							<a href="#" onclick="window.open('${configSistema['caminhoAberturaChamado']}', 'chamado', 'scrollbars=1,width=700,height=400')"><font color="white">Clique aqui</font></a>
						</c:if> 
					<% } %> 
					para liberação de sua permissão.</font></b>
				</td>
			</tr>
		</table>


		<br>
		<br>
		<td>
	</tr>
</table>


<br>
<br>
<center><a href="javascript:history.go(-1)"> Voltar </a></center>
<br>
<br>



