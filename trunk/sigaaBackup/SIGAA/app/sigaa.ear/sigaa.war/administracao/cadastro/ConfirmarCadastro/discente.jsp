<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>Confirmar Cadastro de Discentes</h2>

<f:view>

<h:outputText value="#{ cadastroDiscente.create }"/>

<table class="listagem">
<caption>Lista de Usuários</caption>
<c:forEach var="item" items="${ cadastroDiscente.discentesNaoAutorizados }">
<tr>
	<td>${ item.discente.matricula }</td>
	<td>${ item.pessoa.nome }</td>
	<td>${ item.login }</td>
	<td>
	<h:form>
	<input type="hidden" name="id" value="${ item.id }"/>
	<h:commandButton action="#{ cadastroDiscente.confirmar }" value="Confirmar"/> 
	</h:form>
	</td>
	<td>
	<h:form>
	<input type="hidden" name="id" value="${ item.id }"/>
	<h:commandButton action="#{ cadastroDiscente.negar }" value="Negar"/>
	</h:form>
	</td>
</tr>
</c:forEach>
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>