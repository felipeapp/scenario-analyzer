<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2>Vestibular > Definir Senha</h2>

	<div class="descricaoOperacao">
		<b>Atenção: </b>memorize a senha que você informar. A senha é
	indispensável para ter acesso à sua área pessoal, onde poderá realizar
	inscrições, verificar a validação de sua inscrição, acessar extratos de
	desempenhos, e outras opções.<br/>
	<b>A senha deverá ter pelo menos 4 caracteres.</b></div>

	<h:form id="form">
		<table class="formulario">
			<caption>Senha de Acesso</caption>
			<tbody>
				<c:if test="${acompanhamentoVestibular.obj.id != 0}">
					<tr>
						<th class="obrigatorio" width="50%">Senha Atual:</th>
						<td>
							<h:inputSecret value="#{acompanhamentoVestibular.senhaAtual}" size="16" maxlength="16" id="senhaAtuall"/>
						</td>
					</tr>
					<tr>
					<th class="obrigatorio" width="50%">Nova Senha:</th>
					<td>
						<h:inputSecret value="#{acompanhamentoVestibular.novaSenha}" size="16" maxlength="16" id="novaSenha" />
					</td>
				</tr>
				</c:if>
				<c:if test="${acompanhamentoVestibular.obj.id == 0}">
					<tr>
						<th class="obrigatorio" width="50%">Senha:</th>
						<td>
							<h:inputSecret value="#{acompanhamentoVestibular.novaSenha}" size="16" maxlength="16" id="senha"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio" width="50%">Confirme a Senha:</th>
					<td>
						<h:inputSecret value="#{acompanhamentoVestibular.confirmacaoSenha}" size="16" maxlength="16" id="confirmacaoSenha"/>
					</td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Alterar Senha" action="#{acompanhamentoVestibular.submeterSenha}" rendered="#{acompanhamentoVestibular.alterarSenha}" id="alterarSenha"/>&ensp;
						<h:commandButton value="<< Passo Anterior" action="#{acompanhamentoVestibular.formUploadFoto}" rendered="#{not acompanhamentoVestibular.alterarSenha}" id="passAnterior"/>&ensp; 
						<h:commandButton value="Cancelar" action="#{acompanhamentoVestibular.cancelar}" onclick="#{confirm}" id="cancelar"/>&ensp;
						<h:commandButton value="Próximo Passo >>" action="#{acompanhamentoVestibular.submeterSenha}" rendered="#{not acompanhamentoVestibular.alterarSenha}" id="submeterSenha"/>
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