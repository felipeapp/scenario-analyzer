<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Listagem de Usuários por Papéis</h2>
	<br>
	
	<h:form>
	<table class="formulario" width="30%">
	<caption class="listagem"> Selecionar Permissão </caption>

	<tr>
		<th> Permissão: </th>
		<td>
			<h:selectOneMenu value="#{ listagemPapeis.papel }">
				<f:selectItems value="#{ listagemPapeis.papeis }"/>
			</h:selectOneMenu> 
		</td>
	</tr>	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Listar Usuários" action="#{listagemPapeis.listar}"/>
				<h:commandButton value="Cancelar" action="#{listagemPapeis.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</table>
	</h:form>

	<c:if test="${ not empty listagemPapeis.usuarios }">
		<p>&nbsp;</p>
		<table class="listagem">
		<thead><tr><th>Nome</th><th>Unidade</th><th>Login</th><th>E-mail</th></tr></thead>
		<c:forEach items="${ listagemPapeis.usuarios }" var="usr" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }"><td>${ usr.pessoa.nome }</td><td>${ usr.unidade.sigla }</td><td>${ usr.login }</td><td>${ usr.email }</td></tr>
		</c:forEach>
		</table>
	</c:if>
	<c:if test="${ empty listagemPapeis.usuarios and listagemPapeis.buscou }">
		Nenhum usuário encontrado.
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
