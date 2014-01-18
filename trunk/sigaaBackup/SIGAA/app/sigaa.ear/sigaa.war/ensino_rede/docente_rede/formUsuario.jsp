<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Usuário para Docente</h2>

	<h:messages showDetail="true"/>
	<br>
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<caption class="listagem">Cadastro de Usuário para Docente</caption>
			<tr>
				<th>Docente:</th>
				<td><h:outputText value="#{docenteRedeMBean.obj.pessoa.nome }" /></td>
			</tr>
			<tr>
				<th>Tipo:</th>
				<td><h:outputText value="#{docenteRedeMBean.obj.tipo.descricao }" /></td>
			</tr>
			<tr>
				<th>Situacao:</th>
				<td><h:outputText value="#{docenteRedeMBean.obj.situacao.descricao }" /></td>
			</tr>
			<tr>
				<th>Login: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{docenteRedeMBean.usuario.login}" maxlength="20"/></td>
			</tr>
			<tr>
				<th>E-Mail: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{docenteRedeMBean.usuario.email}" maxlength="100"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="4">
					<h:commandButton value="Cadastrar"
						action="#{docenteRedeMBean.submeterUsuario}" /> <h:commandButton value="Cancelar"
						onclick="#{confirm}" action="#{docenteRedeMBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
