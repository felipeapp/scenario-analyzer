<c:if test="${not empty forumCursoDocente.forunsComPermissao}">
<div class="infoAltRem">
	<h:graphicImage value="/img/delete.gif"/>: Remover F�rum
</div>

<table class="listagem">
	<caption>Lista de F�runs Acess�veis Pelo Docente (${fn:length(forumCursoDocente.forunsComPermissao)})</caption>
	<thead>
		<tr>
			<th style="width: 60%">T�tulo</th>
			<th style="width: 20%">Autor</th>
			<th style="width: 20%; text-align: center">Data de Cria��o</th>
			<th width="1%"></th>
		</tr>
	</thead>
	
	<tbody>
		<c:forEach var="n" items="#{ forumCursoDocente.forunsComPermissao }" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
				<td><h:outputText value="#{ n.forum.titulo }"/></td>
				<td class="nomeUsuario"> ${ n.forum.usuario.pessoa.nomeResumido } </td>
				<td style="text-align: center">
					<ufrn:format type="dataHora" valor="${ n.forum.data }"/>
				</td>
				<td>
					<h:commandLink action="#{forumCursoDocente.removerForum}" title="Remover F�rum" onclick="#{confirmDelete}">
						<h:graphicImage value="/img/delete.gif"/>
						<f:param name="id" value="#{n.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>	

</c:if>