<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema/> &gt; Alterar Dados do Usuário do Discente </h2>

<f:view>
	<h:form id="form_dadosUsuario">
		<table class="formulario" width="70%">
			<caption>Dados do Usuário</caption>
			<tr><td>Discente:</td><td>${ alterarDadosUsuarioAluno.obj.matriculaNome }</td></tr>
			<tr><td>Login:</td><td>${ alterarDadosUsuarioAluno.usuarioSelecionado.login }</td></tr>
			<tr><td>E-Mail: </td><td><h:inputText value="#{ alterarDadosUsuarioAluno.usuarioSelecionado.email }" id="email" /></td></tr>
			<tr><td>Senha: </td><td><h:inputSecret value="#{ alterarDadosUsuarioAluno.usuarioSelecionado.senha }" id="passW" size="10"/></td></tr>
			<tr><td>Repetir Senha: </td><td><h:inputSecret value="#{ alterarDadosUsuarioAluno.reNovaSenha }" id="rePassW" size="10"/></td></tr>
		<tfoot>
			<tr><td colspan="2">
			<h:commandButton value="Alterar Dados" id="btnAlterar" action="#{ alterarDadosUsuarioAluno.alterarDados }"/>
			<h:commandButton value="Cancelar" id="btnCancelar" action="#{ alterarDadosUsuarioAluno.cancelar }" onclick="return confirm('Deseja realmente cancelar a operação?');"/>
			</td></tr>
		</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>