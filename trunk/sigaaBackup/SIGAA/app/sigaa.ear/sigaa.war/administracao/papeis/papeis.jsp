<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/administracao/menu_administracao.jsp" %>
<h2>Atribuir permissões ao usuário ${ atribuirPapeis.usuario.pessoa.nome }</h2>

<h:form>

<table class="formulario" width="70%">
<caption>Papéis</caption>
<tr>
	<td>Nome: </td>
	<td>${ atribuirPapeis.usuario.pessoa.nome }</td>
</tr>
<tr>
	<td>Login: </td>
	<td>${ atribuirPapeis.usuario.login }</td>
</tr>
<tr>
	<td>Unidades: </td>
	<td>${ atribuirPapeis.usuario.unidade.nome }</td>
</tr>
<tr>
	<td>Cargo: </td>
	<td>${ atribuirPapeis.usuario.servidor.cargo.denominacao }</td>
</tr>
<tr><td>SubSistema:</td>
<td>
<h:selectOneMenu value="#{ atribuirPapeis.subSistema.id }" onchange="submit()" immediate="true" id="subsistema">
	<f:selectItems value="#{ atribuirPapeis.subSistemas }"/>
</h:selectOneMenu>

</td></tr>
<tr><td>Papel:</td>
<td>
<h:selectOneMenu value="#{ atribuirPapeis.papel.id }" id="papel" immediate="true">
	<f:selectItems value="#{ atribuirPapeis.papeis }"/>
</h:selectOneMenu>

</td></tr>
<tr>
<td>Papel Temporário?</td>
<td>
	<h:selectBooleanCheckbox value="#{ atribuirPapeis.papelTemporario }" onclick="submit()"/>
	<h:outputText value="Data de Expiração: " rendered="#{ atribuirPapeis.papelTemporario }"/>
	<h:inputText value="#{ atribuirPapeis.dataExpiracao }" rendered="#{ atribuirPapeis.papelTemporario }" size="10">
		<f:convertDateTime pattern="dd/MM/yyyy"/>
	</h:inputText>
</td>
</tr>
<tfoot>
<tr><td colspan="2" align="center">

<h:commandButton value="Adicionar" action="#{ atribuirPapeis.adicionarPapel }"/>
<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ atribuirPapeis.cancelar }"/>

</td></tr>
</tfoot>
</table>

</h:form>

<c:if test="${ not empty atribuirPapeis.usuario.permissoes }">
<br/>&nbsp;

<table class="formulario" width="70%">
<caption>Papéis do Usuário</caption>


<c:forEach var="permissao" items="${ atribuirPapeis.usuario.permissoes }" varStatus="status">
<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${ permissao.papel.descricao }</td><td width="10%" align="right">

<h:form>
<input type="hidden" name="id" value="${ permissao.id }"/>
<h:commandButton action="#{ atribuirPapeis.removerPapel }" value="Remover"/>
</h:form>

</td></tr>
</c:forEach>


</table>
</c:if>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
