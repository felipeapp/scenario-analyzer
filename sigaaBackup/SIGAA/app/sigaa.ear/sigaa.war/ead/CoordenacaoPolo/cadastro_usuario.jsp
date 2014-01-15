<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Usuário para Tutor</h2>

	<h:messages showDetail="true"/>
	<br>
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<h:outputText value="#{tutorOrientador.create}" />
			<caption class="listagem">Cadastro de Tutores</caption>
			<tr>
				<th class="rotulo">Coordenador:</th>
				<td><h:outputText value="#{coordenacaoPolo.obj.pessoa.nome }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Pólo:</th>
				<td>${coordenacaoPolo.obj.polo.cidade.nomeUF}</td>
			</tr>
			<tr>
				<th>Login: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{coordenacaoPolo.usuario.login}"/></td>
			</tr>
			<tr>
				<th>E-Mail: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{coordenacaoPolo.usuario.email}"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="4">
					<h:inputHidden value="#{ coordenacaoPolo.obj.id }"/>
					<h:commandButton value="Cadastrar"
						action="#{coordenacaoPolo.cadastrarUsuario}" /> <h:commandButton value="Cancelar"
						onclick="#{confirm}" action="#{coordenacaoPolo.cancelar}" /></td>
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
