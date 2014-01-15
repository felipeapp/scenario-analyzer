<%@include file="/ava/cabecalho.jsp"%>

<f:view>
<%@include file="/ava/menu.jsp"%>
<h:form>

<fieldset>
<legend><h:outputText value="#{ enquete.object.pergunta }"/></legend>

<table class="listing">
	<thead><tr><th>Resposta</th><th>Votos</th><th>%</th></tr></thead>
	<tbody>
		<c:forEach var="resposta" items="${ enquete.object.respostas }">
			<tr><td class="first">${ resposta.resposta }</td><td>${ resposta.totalVotos }</td><td>${ resposta.porcentagemVotos } %</td></tr>
		</c:forEach>
	</tbody>
</table>

<c:if test="${ turmaVirtual.docente }">
<table class="listing">
	<thead><tr><th>Usuário</th><th>Resposta</th><th width="150">Data Votação</th></tr></thead>
	<tbody>
	<c:forEach var="resposta" items="${ enquete.object.respostas }">
		<c:forEach var="v" items="${ resposta.votos }">
		<tr><td class="first">${ v.usuario.pessoa.nome }</td><td>${ v.enqueteResposta.resposta }</td><td align="center"><fmt:formatDate value="${ v.dataVoto }" pattern="dd/MM/yyyy HH:mm"/></td></tr>
		</c:forEach>
	</c:forEach>
	</tbody>
</table>
</c:if>

<div class="botoes-show">
<h:commandLink value="Voltar" action="#{ enquete.listar }"/>
</div>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>
