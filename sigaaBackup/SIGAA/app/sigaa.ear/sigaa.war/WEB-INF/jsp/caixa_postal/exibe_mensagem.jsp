<%@page import="br.ufrn.arq.web.struts.ConstantesActionGeral"%>

<%@page isELIgnored ="false" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="br.ufrn.sigaa.caixa_postal.dominio.Mensagem"%>
<link rel="stylesheet" type="text/css" href="/shared/css/mailbox.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/img_css/mailbox.js" />

<div id="painel-mensagem-leitura"> <div class="bd" style="overflow: auto;"></div></div>
<%	int acao = 0;
	if(request.getParameter("acao") != null)
		acao = Integer.parseInt(request.getParameter("acao"));%>

<c:if test="<%=acao != ConstantesActionGeral.REMOVER%>">
<table class="opcoesMensagem">
	<tr>
		<td>
		<ul>
			<c:if test="${not mensagem.automatica}">
				<li class="responder"> <a href="#" onclick="doAction(false, 'responder');"> Responder </a> </li>
			</c:if>
			<li class="encaminhar"> <a href="#" onclick="doAction(false, 'encaminhar');"> Encaminhar </a> </li>
			<li class="remover"> <a href="#" onclick="doAction(true, 'remover');"> Remover</a> </li>
			<c:if test="${ anterior != null }">
				<li class="anterior">
					<a href="javascript://noop/" onclick="readMsg(${ anterior },'${ param.folder == null ? "inbox" : param.folder }', ${ param.page == null ? 1 : (param.page) });"> Anterior </a>
				</li>
			</c:if>
			<c:if test="${ proxima != null }">
				<li class="proxima">
					<a href="javascript://noop/" onclick="readMsg(${proxima},'${ param.folder == null ? "inbox" : param.folder }', ${ param.page == null ? 1 : (param.page) });"> Próxima </a>
				</li>
			</c:if>
		</ul>
		</td>
	</tr>
</table>
</c:if>

<div class="corpo">

<table class="lerMsg">
	<jsp:useBean id="mensagem" scope="request" type="Mensagem"/>

	<c:if test="<%= mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIPAC %>">
		<caption>
			SIPAC - Chamado Número ${mensagem.numChamado}
		</caption>
	</c:if>

	<c:if test="<%= mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIGAA %>">
		<caption>
			SIGAA - Chamado Número ${mensagem.numChamado}
		</caption>
	</c:if>
	<c:if test="<%= mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM %>">
		<caption>
			Mensagem
		</caption>
	</c:if>

	<thead>
	<tr>
		<td align="left" width="12%">
			<strong>Assunto:</strong>
		</td>
		<td align="left">
			${mensagem.descricao}
		</td>
	</tr>
	<tr valign="top">
		<td>
			<strong>Remetente:</strong>
		</td>
		<td>
			<c:if test="${ !mensagem.automatica }">
			${mensagem.remetente.pessoa.nome} em
			<ufrn:format name="mensagem" property="dataCadastro" type="dataHora" />
			<c:if test="<%= mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIGAA %>">
				(login: ${mensagem.remetente.login}) - Ramal ${mensagem.remetente.ramal}
			</c:if>
			<c:if test="<%= mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIPAC %>">
				(login: ${mensagem.remetente.login}) - Ramal ${mensagem.remetente.ramal}
			</c:if>
			</c:if>
		</td>
	</tr>
	<tr>
		<td>
			<strong>Unidade:</strong>
		</td>
		<td>
			<c:if test="${mensagem.remetente.unidade != null }">
				${mensagem.remetente.unidade.codigoNome}
			</c:if>
		</td>
	</tr>
	</thead>
	<tbody>
	<c:if test="${mensagem.automatica}">
	<tr>
		<td colspan="3">
			<b>ESTA MENSAGEM FOI GERADA AUTOMATICAMENTE PELO SISTEMA. POR FAVOR, NÃO RESPONDÊ-LA.</b>
		</td>
	</tr>
	</c:if>
	<tr>
		<td colspan="3" valign="top">
			<b>Mensagem: </b> <br/>
			<ufrn:format name="mensagem" property="mensagem" type="texto" />
		</td>
	</tr>

	</tbody>
</table>

<c:if test="<%=acao != ConstantesActionGeral.REMOVER%>">
	<c:if test="${respostas != null}">
		<br />
		<center>
			<b> Respostas: </b>
		</center>

		<table class="formulário" width="90%" align="center">
			<c:forEach items="${respostas}" var="resposta" varStatus="status">
				<tr>
					<td>
						<table cellpadding="3" width="95%" class="formulario" align="center">

							<tr>
								<td align="left" width="15%">
									<strong>Assunto:</strong>
								</td>
								<td align="left">
									${resposta.descricao}
								</td>
							</tr>
							<tr valign="top">
								<td>
									<strong>Remetente:</strong>
								</td>
								<td>
									${resposta.remetente.pessoa.nome } em
									<ufrn:format name="mensagem" property="dataCadastro" type="dataHora" />
									(login: ${resposta.remetente.login}) - Ramal ${resposta.remetente.ramal}
								</td>
							</tr>
							<tr>
								<td>
									<strong>Unidade:</strong>
								</td>
								<td>
									${resposta.remetente.unidade.codigoNome}
								</td>
							</tr>
							<tr>
								<td colspan="2" class="linhaAcima" style="padding:10px;">
									<b>Mensagem: </b> <br/>
									<ufrn:format name="resposta" property="mensagem" type="texto"/>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</c:if>

</div>

<c:if test="<%=acao == ConstantesActionGeral.REMOVER%>">
<center>
		<big><strong> <font color="red"> Confirma a remoção desta mensagem? </font> </strong></big>
		<br />
		<br />
		<html:form action="mensagem/enviaMensagem">
			<html:hidden property="mensagem.id" value="${mensagem.id}" />
			<html:hidden property="acao" value="<%= String.valueOf(ConstantesActionGeral.REMOVER) %>" />
			<html:submit value="Confirmar" />&nbsp;
			<ufrn:button action="mensagem/caixaPostal" value="Cancelar" />
		</html:form>
</center>
</c:if>

<form action="/sigaa/mensagem/enviaMensagem.do" id="responder" method="post">
	<input type="hidden" name="idUsuario" value="${mensagem.remetente.id}" />
	<input type="hidden" name="acao" value="<%= String.valueOf(ConstantesActionGeral.PRE_INSERIR) %>" />
	<input type="hidden" name="mensagem.descricao" value="RES: ${mensagem.descricao}" />
	<input type="hidden" name="mensagem.replyFrom.id" value="${mensagem.id}" />
</form>

<form action="/sigaa/mensagem/enviaMensagem.do" id="encaminhar" method="post">
	<input type="hidden" name="acao" value="<%= String.valueOf(ConstantesActionGeral.PRE_INSERIR) %>" />
	<input type="hidden" name="idMensagemEncaminhar" value="${mensagem.id}" />
	<input type="hidden" name="mensagem.descricao" value="ENC: ${mensagem.descricao}" />
	<input type="hidden" name="mensagem.replyFrom.id" value="${mensagem.id}" />
</form>

<html:form action="mensagem/enviaMensagem" styleId="remover" method="post">
	<html:hidden property="acao" value="<%= String.valueOf(ConstantesActionGeral.REMOVER) %>" />
	<html:hidden property="mensagem.id" value="${mensagem.id}" />
</html:form>

<c:if test="${qtdNaoLida == 0 }">
	<span id="todasLidas"> </span>
</c:if>

<!-- Não Exibe Links para entrar no sistema -->
	<c:if test="${qtdNaoLida != 0 }">
		<script type="text/javascript">
			$('msg').show();
			$('link').hide();
			$('modulos').hide();
		</script>
		</c:if>
<!-- Fim: Não Exibe Links para entrar no sistema -->


