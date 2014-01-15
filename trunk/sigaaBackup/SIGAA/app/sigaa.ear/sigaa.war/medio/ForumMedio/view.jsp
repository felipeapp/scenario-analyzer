<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ forumMedio.create }" />
	<table class="panel-table" width="100%">
		<thead>
			<tr>
				<td width="55%">Nome do Fórum</td>
				<td width="25%">Criado Por</td>
				<td width="10%" align="center">Tópicos</td>
				<td width="10%" align="center">Última Mensagem</td>
			</tr>
		</thead>

		<c:forEach items="${ forumMedio.allTurma }" var="item" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td><strong>
					<a href="${ pageContext.request.contextPath }/portais/turma/Forum/topicos.jsf?idForum=${ item.id }">${ item.titulo }</a>
					</strong><br>
					<em>
					  <a href="${ pageContext.request.contextPath }/portais/turma/Forum/topicos.jsf?idForum=${ item.id }">
					   ${ item.descricao }
					  </a> 
					</em>
				</td>
				<td>${ item.usuario.pessoa.nome }</td>
				<td align="center">${ item.totalTopicos }</td>
				<td align="center">
					<c:if test="${ item.dataUltimaMensagem != null }">
					<fmt:formatDate value="${ item.dataUltimaMensagem }" pattern="dd/MM/yyyy HH:mm:ss"/>
					</c:if>
					<c:if test="${ item.dataUltimaMensagem == null }">
					-
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
