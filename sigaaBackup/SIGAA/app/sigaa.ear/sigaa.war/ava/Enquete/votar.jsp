
<%@include file="/ava/cabecalho.jsp"%>

<f:view>
<%@include file="/ava/menu.jsp"%>
<h:form>


<c:set var="enqueteAtual" value="#{ enquete.object }"/>
<c:set var="respostaUsuarioEnquete" value="${enquete.respostaUsuarioEnquete}" />
<c:set var="totalVotos" value="${enquete.totalVotos}" />

<c:if test="${ enqueteAtual != null }">
<fieldset>
<legend>${ enqueteAtual.pergunta }</legend>

<c:if test="${turmaVirtual.discente}" >
	<div class="descricaoOperacao" style="width:70%;">
			<p>Caro aluno, devido a relação pedagógica da turma virtual o voto da enquete não é secreto.</p>
	</div>
</c:if>

<ul class="enquete">
<c:if test="${ respostaUsuarioEnquete == null }" >

	<c:if test="${not enqueteAtual.prazoExpirado}">
		<c:forEach var="resposta" items="${ enqueteAtual.respostas }">
			<li><td><input type="radio" name="idEnqueteResposta" id="idEnqueteResposta" value="${resposta.id}" class="noborder"/> ${ resposta.resposta }</td></li>
		</c:forEach>
	</c:if>
	<c:if test="${enqueteAtual.prazoExpirado}">
		<div style="text-align:center;font-weight:bold;">O prazo para votar nesta enquete expirou.</div>
	</c:if>
	
</c:if>
 
<c:if test="${ respostaUsuarioEnquete != null }">
	<c:forEach var="item" items="${ enquete.estatisticaDeVotos }">
    <li ${ item.id == respostaUsuarioEnquete.id ? 'class="votado"' : '' }>${item.resposta} - <fmt:formatNumber pattern="#" value="${item.porcentagemVotos}" />% (${item.totalVotos } Voto${item.totalVotos == 1 ? "" : "s" })</li>
	</c:forEach>
</c:if>

</ul>


<div class="botoes-show">
	<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.enquete || permissaoAva.permissaoUsuario.docente}" >
		<h:commandButton value="Votar" action="#{ enquete.alo }" rendered="#{ enquete.respostaUsuarioEnquete == null && !enqueteAtual.prazoExpirado}"/>
	</c:if>
	
	<c:if test="${turmaVirtual.discente}" >
		<h:commandButton value="Votar" action="#{ enquete.alo }" onclick="return(confirm('Caro aluno seu voto não é secreto. Deseja realmente votar?'));" rendered="#{ enquete.respostaUsuarioEnquete == null && !enqueteAtual.prazoExpirado}"/>
	</c:if>
	<h:commandButton value="Visualizar Votos" action="#{ enquete.mostrar }" rendered="#{ turmaVirtual.docente }"/> <%-- || portalTurma.permissao.enquete  --%>
	&nbsp;
	<h:commandButton value="<< Voltar" action="#{ enquete.listar }"/>
</div>

<input id="idEnqueteAtual" type="hidden" name="id" value="${ enqueteAtual.id }"/>
<input id="voltarListagem" type="hidden" name="voltarListagem" value="true"/>
</fieldset>
</c:if>
<c:if test="${ enqueteAtual == null }">
<h3 style="color: #FF0000;">Informe a enquete a ser visualizada!</h3>
</c:if>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>
