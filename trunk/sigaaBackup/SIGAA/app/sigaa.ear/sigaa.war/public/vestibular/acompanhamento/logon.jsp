<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp" %>
<f:view>
<h2>Vestibular > Acesso à Área Pessoal</h2>

<h:form id="form">
	<br/>
	<table class="formulario" cellspacing="3" cellpadding="3" width="50%">
	<caption>Caso já tenha cadastro, preencha os campos abaixo</caption>
	<tbody>
		<tr>
			<th width="40%" class="obrigatorio" >CPF:</th>
			<td>
				<h:inputText value="#{acompanhamentoVestibular.obj.cpf_cnpj}" size="16" maxlength="14"
					onkeypress="return formataCPF(this, event, null)" id="txtCPF">
					<f:converter converterId="convertCpf" />
					<f:param name="type" value="cpf"/>
				</h:inputText>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Senha: </th>
			<td>
				<h:inputSecret value="#{acompanhamentoVestibular.obj.senha}" size="16" maxlength="16" id="senha"/> 
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton value="Acessar a Área Pessoal" action="#{acompanhamentoVestibular.logon}" id="logon"/> &ensp;
				<h:commandButton value="Cancelar" action="#{acompanhamentoVestibular.logoff}" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<table  width="100%" align="center">
	<tr>
		<td style="align:center" width="50%" align="center">
			<b><h:commandLink value="Não Possuo Cadastro" action="#{acompanhamentoVestibular.novoCadastro}" id="novoCadastro" /></b><br/>
			<h:commandLink value="(clique aqui para fazer seu cadastro)" action="#{acompanhamentoVestibular.novoCadastro}" id="novoCadastro2" />
		</td>
		<td align="center">
			<b><h:commandLink value="Esqueci Minha Senha" action="#{acompanhamentoVestibular.solicitarSenha}" id="solicitarSenha"/></b><br/>
			<h:commandLink value="(clique aqui para recuperar sua senha)" action="#{acompanhamentoVestibular.solicitarSenha}" id="solicitarSenha2"/>
		</td>
	</tr>
	</table>
</h:form>
<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
</center>
<br>
<br>
</f:view>
<%@include file="/public/include/rodape.jsp"%>