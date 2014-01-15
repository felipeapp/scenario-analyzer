<table class="formAva" align="center">
	<tr>
		<th>Pessoa:</th>
		<td>
			<h:outputText value="#{ permissaoAva.object.pessoa.nome }"/>
		</td>
	</tr>
	<tr>
		<th>Turma:</th>
		<td>
			${ permissaoAva.object.turma.descricaoSemDocente }
		</td>	
	</tr>
	<tr>
		<th>Permissão de Docente:</th>
		<td>
			<h:selectOneMenu value="#{ permissaoAva.object.docente }">
				<f:selectItems value="#{ permissaoAva.simNao }"/>
			</h:selectOneMenu>
		</td>	
	</tr>
	<tr>
		<th>Gerenciar Fóruns:</th>
		<td>
			<h:selectOneMenu value="#{ permissaoAva.object.forum }">
			<f:selectItems value="#{ permissaoAva.simNao }"/>
			</h:selectOneMenu>
		</td>	
	</tr>
	<tr>
		<th>Gerenciar Enquetes:</th>
		<td>
			<h:selectOneMenu value="#{ permissaoAva.object.enquete }">
			<f:selectItems value="#{ permissaoAva.simNao }"/>
			</h:selectOneMenu>
		</td>	
	</tr>
	<tr>
		<th>Gerenciar Tarefas:</th>
		<td>
			<h:selectOneMenu value="#{ permissaoAva.object.tarefa }">
			<f:selectItems value="#{ permissaoAva.simNao }"/>
			</h:selectOneMenu>
		</td>	
	</tr>
	<tr>
		<th>Corrigir Tarefas:</th>
		<td>
			<h:selectOneMenu value="#{ permissaoAva.object.corrigirTarefa }">
			<f:selectItems value="#{ permissaoAva.simNao }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th>Inserir Arquivos:</th>
		<td>
			<h:selectOneMenu value="#{ permissaoAva.object.inserirArquivo }">
			<f:selectItems value="#{ permissaoAva.simNao }"/>
			</h:selectOneMenu>
		</td>	
	</tr>

</table>

<h:inputHidden value="#{ permissaoAva.object.pessoa.id }"/>