<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h2>Vestibular > Solicita��o de Recupera��o de Senha</h2>

	<div class="descricaoOperacao">
		<b>Aten��o:</b> memorize a senha que voc� informar. A senha �
	indispens�vel para ter acesso � sua �rea pessoal, onde poder� realizar
	inscri��es, verificar a valida��o de sua inscri��o, acessar extratos de
	desempenhos, e outras op��es.</div>

	<h:form id="form">
		<input type="hidden" name="id" value="${ param['id'] }"/>
		<table class="formulario" style="width: 45%;">
			<caption>Senha de Acesso</caption>
			<tbody>
				<tr>
					<th class="obrigatorio" width="50%">Nova Senha:</th>
					<td>
						<h:inputSecret value="#{acompanhamentoVestibular.novaSenha}" size="16" maxlength="16" id="senha" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio" width="50%">Confirme a Senha:</th>
					<td>
						<h:inputSecret value="#{acompanhamentoVestibular.confirmacaoSenha}" size="16" maxlength="16" id="confirmacaoSenha" />
					</td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Redefinir a Senha" action="#{acompanhamentoVestibular.redefinirSenha}" id="redefinirSenha"/> 
						<h:commandButton value="Cancelar" action="#{acompanhamentoVestibular.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>
	<br>
	<br>
</f:view>
<%@include file="/public/include/rodape.jsp"%>