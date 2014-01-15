<%@page import="br.ufrn.arq.web.struts.ConstantesActionGeral,br.ufrn.sigaa.form.UsuarioForm"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-logic" prefix="logic"%>

<SCRIPT LANGUAGE="JavaScript" src="/shared/javascript/prototype-1.4.0.js"> </SCRIPT>

<h2>Lista de Usuários</h2>

<html:form action="buscarDestinatario" focus="login">
<input type="hidden" name="ajaxRequest" value="true"/>
<table class="formulario" align="center" width="460">
  <caption class="listagem">Busca de Usuários</caption>
  <tr><td><table width="95%">
  <tr>
    <td>
      <html:radio property="tipoBusca" value="<%= String.valueOf( UsuarioForm.FIND_BY_LOGIN )%>" styleClass="noborder" styleId="radioLogin"/>
    </td>
    <td> Login: </td>
    <td style="text-align: left"> <html:text size="14" property="usuario.login" onfocus="document.getElementById('radioLogin').checked = true;" maxlength="20"/></td>
  </tr>

  <tr>
    <td>
      <html:radio property="tipoBusca" value="<%= String.valueOf( UsuarioForm.FIND_BY_NOME )%>" styleClass="noborder" styleId="radioNome"/>
    </td>
    <td> Nome: </td>
    <td style="text-align: left">
      <html:text property="nomeBusca" style="width: 260" onfocus="document.getElementById('radioNome').checked = true;" maxlength="60"/>
    </td>
  </tr>

  <tr><td colspan="3" align="center"><html:submit value="Buscar"/></td></tr>

  </table></td></tr>
</table>

<c:if test="${encaminhar != null }">
<input type="hidden" name="encaminhar" value="true"/>
<input type="hidden" name="idMensagem" value="${ param.idMensagem }"/>
</c:if>

<input type="hidden" name="acao" value="<%= ConstantesActionGeral.BUSCAR %>">
</html:form>

<script type="text/javascript" language="JavaScript">
  // Focus padrão
  var focusControl = document.forms[0].elements["denominacaoBusca"];

  <logic:equal name="usuarioForm" property="tipoBusca" value="<%= String.valueOf(UsuarioForm.FIND_BY_LOGIN) %>">
    var focusControl = document.forms[0].elements["login"];
  </logic:equal>

  <logic:equal name="usuarioForm" property="tipoBusca" value="<%= String.valueOf(UsuarioForm.FIND_BY_NOME) %>">
    var focusControl = document.forms[0].elements["nomeBusca"];
  </logic:equal>

  if (focusControl.type != "hidden")
     focusControl.focus();
</script>

<script type="text/javascript">
function setUsuario(login) {
 <%-- Função chamada se encontra na view que chamou esta jsp --%>
 	// var destinatarios = window.opener.$F('destinatarios').trim();
 	var destinatarios = window.opener.document.getElementById('destinatarios').value.trim();
	if (destinatarios.length != 0 && destinatarios.charAt(destinatarios.length - 1) != ',')
		destinatarios += ', ';
	destinatarios += login;
	window.opener.document.getElementById('destinatarios').value = destinatarios;

	window.close();
 }
</script>


<logic:notEmpty name="usuarios">
<br/><br/>
<table width="100%" align="center" class="listagem">
  <caption class="listagem">Usuários Encontrados</caption>
  <thead class="listagem">
  <tr>
    <th width="325"> Nome </th>
    <th width="80"> Login </th>
    <th width="225"> Unidade</th>
    <th width="50"colspan="5">&nbsp;</th>
  </tr>

  <tbody class="listagem">
<logic:iterate name="usuarios" id="usuario" indexId="id">
<tr height="25" valign="middle" class="${id % 2 == 0?"linhaPar":"linhaImpar" }">
   <td>${usuario.pessoa.nome }</td>
   <td>${usuario.login }</td>
   <td>
      <logic:present name="usuario" property="unidade">
      ${usuario.unidade.sigla }
      </logic:present>
      <logic:notPresent name="usuario" property="unidade">
      	-
      </logic:notPresent>
   </td>

   <td>
    <a href="javascript: setUsuario('${ usuario.login }')"> <img src="img/addUnd.gif" alt="Selecionar Usuário" border="0"> </a>
   </td>

</tr>
</logic:iterate>
</table>

<br>
<ufrn:paginacao action="listarUsuariosPopup" denominacao="Usuários">
  <html:hidden property="tipoBusca"/>
  <html:hidden property="login"/>
  <html:hidden property="nomeBusca"/>
  <html:hidden property="unidade.id"/>
  <html:hidden property="acao"/>
</ufrn:paginacao>

</logic:notEmpty>
<br><br>
<%@include file="/WEB-INF/jsp/include/rodape_dialog.jsp"%>