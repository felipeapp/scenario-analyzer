<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema/> &gt; Alterar Dados do Usuário do Discente </h2>

<f:view>

<h:form>

<table class="formulario" width="70%">
<caption>Dados do Usuário</caption>
<tr><td>Discente:</td><td>${ alterarDadosUsuarioAlunoMBean.obj.matriculaNome }</td></tr>
<tr><td>Login:</td><td>${ alterarDadosUsuarioAlunoMBean.usuarioSelecionado.login }</td></tr>
<tr><td>E-Mail: </td><td><h:inputText value="#{ alterarDadosUsuarioAlunoMBean.usuarioSelecionado.email }" id="email" /></td></tr>
<tr><td>Senha: </td><td><h:inputSecret value="#{ alterarDadosUsuarioAlunoMBean.usuarioSelecionado.senha }" id="passW" size="10"/></td></tr>
<tr><td>Repetir Senha: </td><td><h:inputSecret value="#{ alterarDadosUsuarioAlunoMBean.reNovaSenha }" id="rePassW" size="10"/></td></tr>
<tfoot>
<tr><td colspan="2">
<h:commandButton value="Alterar Dados" action="#{ alterarDadosUsuarioAlunoMBean.alterarDados }"/>
<h:commandButton value="Cancelar" action="#{ alterarDadosUsuarioAlunoMBean.cancelar }" onclick="return confirm('Deseja realmente cancelar a operação?');"/>
</td></tr>
</tfoot>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>