
<ul class="form">
	<li>
		<input type=hidden value="${forumMensagem.object.forum.id}" />
		<label for="titulo">T�tulo: <span class="required">&nbsp;</span></label>
		<h:inputText id="titulo" size="59" maxlength="200" value="#{forumMensagem.object.titulo}"/>
	</li>
	
	<li>
		<label for="conteudo">Conte�do: <span class="required">&nbsp;</span></label>
		<t:inputTextarea value="#{forumMensagem.object.conteudo}" rows="10" cols="70"/>
	</li>
	<c:if test="${turmaVirtual.docente  || permissaoAva.permissaoUsuario.forum}">
		<li>
			<label>T�pico de Aula:</label>
			<h:selectOneMenu id="aula" value="#{ forumMensagem.idTopicoAula }" rendered="#{ not empty topicoAula.comboIdentado }">
				<f:selectItem itemValue="0" itemLabel=" -- Selecione um t�pico de aula -- "/>
				<f:selectItems value="#{topicoAula.comboIdentado}" />
			</h:selectOneMenu>
			<h:selectOneMenu id="sem-aula" value="#{ forumMensagem.idTopicoAula }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
				<f:selectItem itemLabel="Nenhum T�pico de Aula foi cadastrado" itemValue="0"/>
			</h:selectOneMenu>
			<ufrn:help>Selecione um t�pico de aula para exibir este f�rum na p�gina inicial da turma virtual.</ufrn:help>
		</li>
	</c:if>

</ul>
