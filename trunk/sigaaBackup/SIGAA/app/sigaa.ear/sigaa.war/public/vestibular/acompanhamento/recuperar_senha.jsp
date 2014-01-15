<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp" %>
<f:view>
<h2>Vestibular > Recuperar Senha</h2>

<div class="descricaoOperacao">

	Para recuperar sua senha, digite seu CPF e o E-Mail cadastrado no sistema.
	Um e-mail será enviado solicitando a confirmação da recuperação da senha.

</div>

<h:form id="form">
	<table class="formulario" style="width:460px;">
		<caption> Dados para Recuperação </caption>
		<tr>
			<th> CPF: </th>
			<td> <h:inputText value="#{acompanhamentoVestibular.obj.cpf_cnpj}" size="16" maxlength="14"
					onkeypress="return formataCPF(this, event, null)" id="txtCPF">
					<f:converter converterId="convertCpf" />
					<f:param name="type" value="cpf"/>
				</h:inputText>
			</td>
		</tr>
		<tr>
			<th nowrap="nowrap"> E-Mail Cadastrado: </th>
			<td> <h:inputText value="#{acompanhamentoVestibular.obj.email}" size="40" maxlength="40" id="eMail"/></td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Verificação de Segurança</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center">
				<img src="/sigaa/captcha.jpg">
			</td>
		</tr>
		<tr>
			<th>Conteúdo da imagem acima:</th>
			<td>
				<h:inputText value="#{acompanhamentoVestibular.captcha}" size="10" maxlength="10" id="captcha"/>
			</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton value="Recuperar Senha" action="#{acompanhamentoVestibular.recuperarSenha}" id="recuperarSenha"/> &ensp;
				<h:commandButton value="Cancelar" action="#{acompanhamentoVestibular.cancelar}" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
		</tfoot>
	</h:form>

</table>

</f:view>
<%@include file="/public/include/rodape.jsp"%>