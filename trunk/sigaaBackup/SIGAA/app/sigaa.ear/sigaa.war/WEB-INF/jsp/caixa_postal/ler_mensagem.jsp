<div id="textoHeader" class="textoHeader">

<table width="100%">
<tr class="headMsg">

	
	<td>
		<strong>Assunto:</strong> <span id="assuntoMsg">Teste</span><br/>
		<strong>De:</strong> <span id="deMsg">david/SINFO</span><br/>
		<strong>Para:</strong> <span id="paraMsg">david/SINFO</span><br/>
	</td>
</tr>
</table>
</div>

<div id="textoBody" class="textoBody">
<table>
<tr>
	<td id="textoMsg">
	Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Vestibulum ultrices magna sed purus. 
	Sed arcu eros, euismod nec, mattis quis, ullamcorper ut, mi. Cum sociis natoque penatibus et 
	magnis dis parturient montes, nascetur ridiculus mus. Sed a dui. In consequat turpis in lorem. 
	Nulla facilisi. Praesent orci est, ornare sed, egestas ac, tempor malesuada, erat. Pellentesque 
	auctor, quam vestibulum pretium sodales, dolor diam rutrum ipsum, at suscipit leo felis at diam. 
	Phasellus vel sem. Nam in dui. Duis consequat nibh vitae odio accumsan viverra. Quisque mollis 
	fringilla pede. Mauris quis orci eget nunc faucibus feugiat. Etiam dictum elit vel lacus. 
	Suspendisse augue eros, venenatis eget, gravida sit amet, nonummy a, magna. Aenean elit.
	</td>
</tr>
</table>
</div>

<div id="textoFooter" class="textoFooter">
<table class="caixaOpcoes">
<tr>
	<form action="enviaMensagem.do" method="post" id="responderForm">
		<input type="hidden" name="idUsuario" value="<%-- ultima.getRemetente().getId() --%>" />
		<input type="hidden" name="acao" value="<%--=ConstantesAction.PRE_INSERIR --%>" />
		<input type="hidden" name="mensagem.descricao" value="RES: <%--= ultima.getDescricao() --%>" />
		<input type="hidden" name="mensagem.replyFrom.id" value="<%--= ultima.getId() --%>" />
	</form>
	<td width="30%" align="right"><img src="img_css/mensagens/email_go.png" border="0"></td>
	<td width="10%">&nbsp;<a href="#" onclick="javascript:document.getElementById('responderForm').submit()">Responder</a></td>

	<form action="enviaMensagem.do" method="post" id="encaminharForm">
		<input type="hidden" name="acao" value="<%--=ConstantesAction.PRE_INSERIR --%>" />
		<input type="hidden" name="mensagem.descricao" value="ENC: <%--= ultima.getDescricao() --%>" />
		<input type="hidden" name="mensagem.mensagem" value="<%--= br.ufrn.arq.util.SIPACUtils.escapeHTML(ultima.getMensagem()) --%>" />
		<input type="hidden" name="mensagem.replyFrom.id" value="<%--= ultima.getId() --%>" />
	</form>
	<td width="2%" align="center"><img src="img_css/back.gif" border="0"></td>
	<td width="10%"><a href="#" onclick="javascript:document.getElementById('encaminharForm').submit()">Encaminhar</a></td>

	<td width="2%"><img src="img_css/delete.gif" border="0"></td>
	<td width="35%">Excluir</td>	
</tr>
</table>
</div>
