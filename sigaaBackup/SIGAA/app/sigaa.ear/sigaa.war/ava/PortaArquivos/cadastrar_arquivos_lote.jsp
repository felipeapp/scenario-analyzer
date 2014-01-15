<%@include file="/ava/cabecalho.jsp" %>	

<a4j:keepAlive beanName="arquivoUsuario"></a4j:keepAlive>

<style>
	.descricaoCampos { display: block; font-weight: normal; font-size: 0.9em; color: #444; }
	.tabelaArquivo   { background-color: #F9FBFD;border: 1px solid #DEDFE3; }
</style>

<f:view>
	
	<%@include file="/ava/menu.jsp" %>
	
	<rich:modalPanel id="panelNovaPasta" autosized="true">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="Criar Nova Pasta"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
		<h:panelGroup>
			<h:graphicImage value="/img/close.png" styleClass="hidelink"
				id="hidelink" />
			<rich:componentControl for="panelNovaPasta" attachTo="hidelink" operation="hide" event="onclick" />
		</h:panelGroup>
	</f:facet>
	<h:form id="formAva">
		<table style="border-collapse: separate;">
			<tr>
				<th nowrap="nowrap">Dentro de:</th>
				<td>
					<h:selectOneMenu value="#{pastaArquivos.obj.pai.id }">
						<f:selectItems value="#{arquivoUsuario.pastasUsuarioCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
				
			<tr>
				<th>Nome:</th>
				<td>
					<h:inputText size="50" maxlength="200" value="#{pastaArquivos.obj.nome }" id="nomePasta" />
				</td>
			</tr>
			<tr>
				<td colspan="2"> <h:commandButton value="Criar Pasta" action="#{pastaArquivos.cadastrar }" onclick="return validarNomePasta();" /> </td>
			</tr>
		</table>
	</h:form>
</rich:modalPanel>

	<h:form enctype="multipart/form-data" id="form" >
	
	<fieldset>
	<legend>Inserir Arquivos no Porta-Arquivos</legend>
	<br />
	
	<table class="formAva" width="100%" >
			<div class="descricaoOperacao">
				<p>Caro(a) docente,</p>
				<p>Nesse cadastro é possível associar mais de um arquivo à turma de uma única vez. Para isso, será necessário preencher os dados do formulário abaixo, selecionar o arquivo e clicar no botão "Adicionar". Após adicionar todos os arquivos, clique no botão "Enviar Arquivos" para finalizar a operação.</p>
			</div>
			
			
			<tr>
				<th class="required">Pasta:</th>
				<td>
					<h:selectOneMenu id="pasta" value="#{ arquivoUsuario.obj.pasta.id }" onblur="submit();">
					<f:selectItems value="#{ arquivoUsuario.pastasUsuarioCombo }"/>
					</h:selectOneMenu>
					<span class="descricaoCampos">(Pasta do seu porta-arquivos onde o arquivo deverá ser salvo.) <h:outputText style="font-weight: bold;text-decoration:underline;cursor:pointer;" value="Criar Nova Pasta" id="linkNovaPasta" /><rich:componentControl for="panelNovaPasta" attachTo="linkNovaPasta" operation="show" event="onclick" /></span>
				</td>
			</tr>
			<tr>
				<th class="required">Tópico de Aula:</th>
				<td> 
				<h:selectOneMenu value="#{ arquivoUsuario.arquivoTurma.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
				<f:selectItems value="#{ topicoAula.comboIdentado }"/>
				</h:selectOneMenu>
				<h:selectOneMenu value="#{ arquivoUsuario.arquivoTurma.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
					<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
				</h:selectOneMenu>
				<span class="descricaoCampos">(O Tópico de Aula ao qual o arquivo será associado.)</apan> 
				</td>
			</tr>	
			
			<tr>
				<th class="required">Arquivos:</th>
				<td>
					<table class="tabelaArquivo" >
					<tr>
						<th valign="top">Nome:</th>
						<td>
							<h:inputText value="#{ arquivoUsuario.arquivoTurma.nome }" size="59" maxlength="200"/>
							<span class="descricaoCampos">(Nome que será visto pelos discentes na página principal da Turma Virtual. Se nenhum nome for informado, será utilizado o nome do arquivo.)</span>
						</td>
					</tr>
					
					<tr>
						<th valign="top">Descricao:</th>
						<td>
							<h:inputTextarea value="#{ arquivoUsuario.arquivoTurma.descricao }" rows="3" cols="57"/>
							<span class="descricaoCampos">(Breve descrição do arquivo. Não obrigatório.)</span>
						</td>
					</tr>
					<tr>
					
										
					<tr>
						<th></th>
						<td>						
							<t:inputFileUpload id="arquivo" size="50" value="#{arquivoUsuario.arquivo}"/>		
							<span class="descricaoCampos">(Selecione os arquivos a serem enviados para a Turma Virtual. Tamanho máximo: ${arquivoUsuario.tamanhoMaximoArquivo} MB)</span>
						</td>
					</tr>
					
					<tr>
						<th></th>
						<td>
							<h:commandButton action="#{arquivoUsuario.adicionarArquivo}" value="Adicionar" title="Adicionar"/>
						</td>
					</tr>
		
						<th></th>
						<td>
							<a4j:outputPanel id="arquivos">
								
								<c:if test="${not empty arquivoUsuario.arquivosTurma}">
									<rich:dataTable value="#{arquivoUsuario.arquivosTurma}" var="arquivo" style="width:50%;" rowKeyVar="row">
										<rich:column>
											<f:facet name="header"><h:outputText value="Nome"/></f:facet>
											<h:outputText value="#{arquivo.nome}" />
										</rich:column>
										<rich:column style="text-align: right" width="3%">
											<f:facet name="header"><h:outputText value=""/></f:facet>
											<a4j:commandLink action="#{arquivoUsuario.removerArquivoItem}" title="Remover Arquivo" reRender="arquivos">
												<h:graphicImage value="/img/delete.gif" />
												<f:param name="indice" value="#{row}"/> 
											</a4j:commandLink>
										</rich:column>
									</rich:dataTable>
								</c:if>
								<c:if test="${empty arquivoUsuario.arquivosTurma}">
									<span style="font-style:italic;font-weight:bold;">Nenhum arquivo adicionado.</span>
								</c:if>
							</a4j:outputPanel>
						</td>	
					</tr>
					</table>
				</td>
			</tr>
			
			<tr>			
				<th></th>
				<td>
					<div>
						<label for="form:descricao">Notificação: </label>
						<h:inputHidden value="#{ arquivoUsuario.arquivoTurma.notificarAlunos}" id="inputHiddenNotificarAluno"/>
						<h:selectBooleanCheckbox id="notificacao" value="#{ arquivoUsuario.arquivoTurma.notificarAlunos }" />
						<span class="descricaoCampos">(Notificar os alunos por e-mail)</span> 
					</div>
				</td>
			</tr>
			
			<c:if test="${ not empty turmaVirtual.turmasSemelhantes }">
				<tr>
					<th class="required">Criar em:</th>
					<td>	
						<div>
							<t:selectManyCheckbox value="#{ arquivoUsuario.cadastrarEm }" layout="pageDirection">
								<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }"/>
							</t:selectManyCheckbox>
						</div>
					</td>
				</tr>
			</c:if>
			
	</table>		
	
	
		
		<div class="botoes">
			
			<div class="form-actions">
				<h:commandButton value="Enviar Arquivos" action="#{arquivoUsuario.cadastrarVarios}" id="botaoEnviarArquivo"/>
			</div>
			<div class="other-actions">
				<h:commandButton value="Cancelar" action="#{arquivoUsuario.cancelar}" immediate="true" id="botaoCancelar" onclick="#{confirm}"/>
			</div>
			<div class="required-items">
				<span class="required">&nbsp;</span>
				Campos de Preenchimento Obrigatório
			</div>
		</div>
		
		</fieldset>
	</h:form>

</f:view>

<script type="text/javascript">

function limpar() {
		//document.getElementById("form:nome").value = ''
		"submit();"
}


function validarNomePasta() {
	if(document.getElementById('formPasta:nomePasta').value.trim() == '') {
		alert("Digite o nome da pasta.");
		return false;
	}
	return true;
}

</script>		

<%@include file="/ava/rodape.jsp" %>








