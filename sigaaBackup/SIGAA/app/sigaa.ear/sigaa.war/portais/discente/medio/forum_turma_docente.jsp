<c:set var="foruns" value="#{forumMensagemMedio.forumMensagemDocente}" />

<h4> Fóruns das Turmas de Ensino Médio </h4>

<h:form>
	<div class="descricaoOperacao">
		Caro Docente, este fórum é destinado para discussões relacionadas às suas turmas. Todos os alunos e docentes 
		das Turmas tem acesso a ele.
	</div>
	
	<c:if test="${forumMedio.usuarioAtivo}">
	<center>
		<h:commandLink id="novoTopicoForum" action="#{ forumMedio.listarTurmaSeriesDocente }"	value="Cadastrar novo tópico para fórum de turma do Ensino Médio" />
	</center>
	<br/>
	</c:if>
	<c:if test="${ empty foruns }">
		<center>Nenhum item foi encontrado</center>
	</c:if>

	<c:if test="${ not empty foruns }">

		<table class="listagem">
			<thead>
				<tr>
					<th>Turma</th>
					<th>Título</th>
					<th>Autor</th>
					<th style="text-align: center">Respostas</th>
					<th style="text-align: center">Data</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>

			<tbody>

				<c:forEach var="n" items="#{ foruns }" varStatus="status" end="5">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
						<td width="20%">${ n.forum.turmaSerie.descricaoCompleta }</td>
						<td width="40%">
						<h:commandLink id="mostrarForumMensagemTurma"
							action="#{ forumMensagemMedio.mostrarForumMensagemTurmaSerie }">
							<h:outputText value="#{ n.titulo }" />
							<f:param name="idForumMensagem" value="#{ n.id }" />
							<f:param name="id" value="#{ n.forum.id }" />
						</h:commandLink>
						</td>

						<td><acronym title="${ n.usuario.pessoa.nome}"> ${
						n.usuario.login } </acronym></td>
						<td style="text-align: center">${n.respostas }</td>
						<td style="text-align: center">
							<c:if test="${ n.ultimaPostagem != null }">
								<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.ultimaPostagem }" />
							</c:if> 
							<c:if test="${ n.ultimaPostagem == null }">
								<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.data }" />
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</h:form>