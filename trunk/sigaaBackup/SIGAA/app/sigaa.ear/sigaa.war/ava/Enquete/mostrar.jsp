<%@include file="/ava/cabecalho.jsp"%>

<f:view>
<%@include file="/ava/menu.jsp"%>
<h:form>


<fieldset>
<legend><h:outputText value="#{ enquete.object.pergunta }"/></legend>

<div style="margin-bottom:10px;margin-left:auto;margin-right:auto;width:95%;">Prazo para votação: <strong style="font-weight:bold;"><ufrn:format type="dataHora" valor="${enquete.object.dataFim}" /></strong></div>

<table class="listing">
	<thead><tr><th><P align="left">Resposta</P></th><th><P align="right">Votos</P></th><th><P align="right">Porcentagem</P></th></tr></thead>
	<tbody>
		<c:forEach var="resposta" items="${ enquete.object.respostas }">
			<tr><td class="first">${ resposta.resposta }</td><td><P align="right">${ resposta.totalVotos }</P></td><td><P align="right">${ resposta.porcentagemVotos } %</P></td></tr>
		</c:forEach>
	</tbody>
</table>
<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.enquete }">
<table class="listing">
	<caption style="font-weight:bold;text-align:center;background:#CCCCCC;">Lista de usuários que responderam a enquete</caption>
	<thead><tr><th><P align="left">Usuário</P></th><th><P align="left">Resposta</P></th><th width="150">Data/Hora Votação</th></tr></thead>
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
<h:commandButton value="<< Voltar" action="#{ enquete.listar }"/>
</div>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>
