<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h2>Vestibular > Solicitação de Recuperação de Senha</h2>

	<div class="descricaoOperacao">
		<b>Atenção:</b> memorize a senha que você informar. A senha é
	indispensável para ter acesso à sua área pessoal, onde poderá realizar
	inscrições, verificar a validação de sua inscrição, acessar extratos de
	desempenhos, e outras opções.</div>

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
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br>
	<br>
</f:view>
<%@include file="/public/include/rodape.jsp"%>