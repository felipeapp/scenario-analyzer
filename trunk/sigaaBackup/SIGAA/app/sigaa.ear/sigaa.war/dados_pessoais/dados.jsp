<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Alterar os Dados Pessoais</h2>
	<h:outputText value="#{userBean.dadosUsuario}" />
	<h:form>
		<table class="formulario" cellpadding="4">
			<caption> Dados Pessoais </caption>
			<tr>
				<th>Nome:</th>
				<td>${usuario.pessoa.nome}</td>
			</tr>
			<tr>
				<th>E-mail:</th>
				<td><h:inputText value="#{userBean.obj.email}" size="50"/>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">
					<center>Alteração
				de Senha</center></td>
			</tr>
			<tr>
				<th>Senha Atual:</th>
				<td><h:inputSecret value="#{userBean.senha}" />
			</tr>
			<tr>
				<th>Nova Senha:</th>
				<td><h:inputSecret value="#{userBean.novaSenha}" />
			</tr>
			<tr>
				<th>Repetir Nova Senha:</th>
				<td><h:inputSecret value="#{userBean.reNovaSenha}" />
			</tr>

			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton action="#{userBean.alteraDados}" value="Alterar Dados"/>
					<h:commandButton action="#{userBean.cancelar}" value="Cancelar"/></td>
			</tr>
			</tfoot>

		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
