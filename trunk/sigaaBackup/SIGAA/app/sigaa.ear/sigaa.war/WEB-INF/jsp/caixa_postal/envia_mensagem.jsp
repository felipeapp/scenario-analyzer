<%@page isELIgnored ="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>


<%@page import="br.ufrn.arq.web.struts.ConstantesActionGeral"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.dominio.Usuario"%>

<%@page import="br.ufrn.arq.caixa_postal.TipoChamado"%><script type="text/javascript" src="<%= request.getContextPath() %>/javascript/mailbox.js"></script>

<link rel="stylesheet" type="text/css" href="/shared/css/mailbox.css" />

<html:form action="/mensagem/enviaMensagem" focus="mensagem.descricao" styleId="formMensagem" method="post">

<div id ="leituraMensagens">
<table width="100%" class="enviaMsg" align="center" cellpadding="2">
	<html:hidden property="acao" value="<%= String.valueOf(ConstantesActionGeral.INSERIR) %>"/>
	<html:hidden property="mensagem.tipo" styleId="tipoMensagem"/>
	<html:hidden property="mensagem.usuario.id" value="${destinatario.id == null || destinatario.id == '' ? 0 : destinatario.id}"/>
	<html:hidden property="mensagem.replyFrom.id" value="${param['mensagem.replyFrom.id'] == null ? 0 : param['mensagem.replyFrom.id']}"/>

    <tr>
    	<td rowspan="5" class="envio" style="width: 10%">&nbsp;</td>
    	<td colspan="2"> </td>
    </tr>

	<c:if test="${ msgAdministrador == null }">
    <tr>
		<td> <b>Destinatário(s):</b> </td>
		<td style="text-align: left;">
			<c:if test="${ mensagemForm.mensagem.chamado == true }">
				ADMINISTRADORES DO SISTEMA
			</c:if>
			<c:if test="${ mensagemForm.mensagem.chamado != true }">
				<html:text property="destinatarios"  styleId="destinatarios" value="${ user.login }" style="width: 95%"/>
				<a href="javascript://nop/" onclick="javascript:window.open('/sigaa/buscarDestinatario.do?ajaxRequest=true', '', 'width=500, height=450, left=200, scrollbars');">
					<img src="/shared/img/caixa_postal/group.gif" alt="Buscar Usuário" title="Buscar Usuário" border="0"/>
				</a>
			</c:if>
		</td>
	</tr>
	</c:if>

	<c:if test="${ msgAdministrador != null }">
	<tr>
		<td> <b>Escolha o Papel:</b> </td>
		<td>
			<html:select property="mensagem.papel.id">
               <html:options collection="papeis" property="id" labelProperty="descricao"/>
            </html:select>
		</td>
	</tr>
	</c:if>

	<tr>
		<td width="18%"> <b>Assunto:</b> </td>
		<td>
			<html:hidden property="mensagem.descricao" styleId="assunto-mensagem"/>
			<c:if test="${encaminhar != null }">
				<input type="text" id="assunto-facade" value="ENC: ${ encaminhar.titulo }" style="width: 99%"/>
			</c:if>
			<c:if test="${encaminhar == null }">
				<input type="text" id="assunto-facade" style="width: 99%" value="${tituloResposta}"/>
			</c:if>
		</td>
	</tr>

	<c:if test="${ mensagemForm.mensagem.chamado == true }">
		<tr>
			<td width="18%"> <b>Tipo de chamado:</b> </td>
			<td>
				<c:forEach items="${tiposChamados}" var="tipoChamado" >
					<input type="radio" name="mensagem.tipoChamado" id="${tipoChamado[0]}"  value="${tipoChamado[0]}" class="noborder"  onclick="mensagem.dialog.setTipoChamado(true);"/>
           			<label for="${tipoChamado[0]}">${tipoChamado[1]}</label>
				</c:forEach>
			</td>
		</tr>
	 </c:if>
	
	</thead>

	<tbody>
	<tr>
		<td colspan="3" align="center">
		<html:hidden property="mensagem.mensagem" styleId="corpoMensagem"/>
		<c:if test="${ encaminhar != null }">
			<html:textarea property="mensagem.mensagem" styleId="corpo-facade" style="width: 99%" rows="8" value="${encaminhar.mensagem}">
			</html:textarea>
		</c:if>
		<c:if test="${encaminhar == null }">
			<html:textarea property="mensagem.mensagem" styleId="corpo-facade" style="width: 99%" rows="8" value="${original}">
			</html:textarea>
		</c:if>
		</td>
	</tr>
	</tbody>
		<tfoot style="background: #EDF1F8;">
		<tr>
			<td colspan="3">
				<html:checkbox styleClass="noborder" property="mensagem.confLeitura" styleId="confirmarLeitura"/>
				<label for="confirmarLeitura">Desejo receber por e-mail uma confirmação da leitura desta mensagem  </label></td>
		</tr>
		<%--
		Usuario user = (Usuario) session.getAttribute("usuario");
		if ( user.isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA ) ) { %>
		<tr>
			<td colspan="3">
				<html:checkbox styleClass="noborder" property="mensagem.leituraObrigatoria" styleId="obrigarLeitura"/>
				<label for="obrigarLeitura"> Obrigar usuário a ler esta mensagem para entrar no sistema  </label>
			</td>
		</tr>
		<% } --%>
    <tr style="visibility: hidden; background: lime;" id="statusEnvio">
    	<td colspan="3" align="center"> Aguarde, enviando mensagem... </td>
    </tr>
	</tfoot>
</table>
</div>
</html:form>