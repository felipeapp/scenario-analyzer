
<ul class="form">
	<li>
		<input type=hidden value="${forumMensagem.object.forum.id}" />
		<label for="titulo">Título: <span class="required">&nbsp;</span></label>
		<h:inputText id="titulo" size="59" maxlength="200" value="#{forumMensagem.object.titulo}"/>
	</li>
	
	<li>
		<label for="conteudo">Conteúdo: <span class="required">&nbsp;</span></label>
		<t:inputTextarea value="#{forumMensagem.object.conteudo}" rows="10" cols="70"/>
	</li>
	<c:if test="${turmaVirtual.docente  || permissaoAva.permissaoUsuario.forum}">
		<li>
			<label>Tópico de Aula:</label>
			<h:selectOneMenu id="aula" value="#{ forumMensagem.idTopicoAula }" rendered="#{ not empty topicoAula.comboIdentado }">
				<f:selectItem itemValue="0" itemLabel=" -- Selecione um tópico de aula -- "/>
				<f:selectItems value="#{topicoAula.comboIdentado}" />
			</h:selectOneMenu>
			<h:selectOneMenu id="sem-aula" value="#{ forumMensagem.idTopicoAula }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
				<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
			</h:selectOneMenu>
			<ufrn:help>Selecione um tópico de aula para exibir este fórum na página inicial da turma virtual.</ufrn:help>
		</li>
	</c:if>

</ul>
