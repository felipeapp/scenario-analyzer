<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h:form>

<div class="descricaoOperacao">
<p>Foram encontrados mais de um usuário para o discente selecionado. Por favor, selecione entre os usuários
abaixo listados aquele para o qual você deseja recuperar a senha.</p> 
</div>

<table class="listagem">
<caption>Usuários encontrados</caption>
<thead>
<tr><td>Login</td><td></td></tr>
</thead>
<tbody>
<c:forEach var="usuario" items="#{ recuperarSenhaDiscenteMBean.usuarios }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
	<td>${ usuario.login }</td>
	<td width="15">
		<h:commandLink action="#{ recuperarSenhaDiscenteMBean.selecionaUsuario }">
			<h:graphicImage value="/img/seta.gif"/>
			<f:param name="id" value="#{ usuario.id }"/>
		</h:commandLink>
	</td>
</tr>
</c:forEach>
</tbody>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>