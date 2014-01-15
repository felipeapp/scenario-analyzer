<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${ sessionScope.paComTurma == 'true' }">
<%@include file="/ava/cabecalho.jsp" %>
</c:if>
<c:if test="${ sessionScope.paComTurma == 'false' }">
<%@include file="/ava/PortaArquivos/cabecalho.jsp"%>
</c:if>


<f:view>

<a4j:keepAlive beanName="arquivoUsuario" />

<c:if test="${ sessionScope.paComTurma == 'true' }">
	<%@include file="/ava/menu.jsp" %>
	
	<script>
		JAWR.loader.style('/javascript/ext-2.0.a.1/resources/css/ext-all.css', 'all');
	</script>
	
	<script src="/shared/javascript/ext-2.0.a.1/ext-base.js"></script>
	<script src="/shared/javascript/ext-2.0.a.1/ext-all.js"></script>

</c:if>
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
	<h:form id="formPasta">
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

<h:form enctype="multipart/form-data">


<fieldset>
<legend>Inserir Arquivo no Porta-Arquivos</legend>

<h:messages showDetail="true"/>
<h:outputText value="#{ arquivoUsuario.selecionarTurmaPadrao }"/>
	<ul class="form">
		<li>
			<label for="form:pasta" class="required">Pasta:<span class="required">&nbsp;</span></label>
			<h:selectOneMenu id="pasta" value="#{ arquivoUsuario.obj.pasta.id }">
				<f:selectItems value="#{ arquivoUsuario.pastasUsuarioCombo }"/>
			</h:selectOneMenu>
			<span class="descricao-campo">(Pasta do seu porta-arquivos onde o arquivo deverá ser salvo.) <h:outputText style="font-weight: bold;text-decoration:underline;cursor:pointer;" value="Criar Nova Pasta" id="linkNovaPasta" /><rich:componentControl for="panelNovaPasta" attachTo="linkNovaPasta" operation="show" event="onclick" /></span> 
		</li>
		
		<li>
			<label for="form:arquivo" class="required">Arquivo:<span class="required">&nbsp;</span></label>
			<t:inputFileUpload id="arquivo" value="#{arquivoUsuario.arquivo}" size="50"/>
			<span class="descricao-campo">(Selecione o arquivo a ser enviado para a Turma Virtual. Tamanho máximo: ${arquivoUsuario.tamanhoMaximoArquivo} MB)</span> 
		</li>

		<c:if test="${ arquivoUsuario.associarTurma }">
		<li>
			<label for="form:topico" class="required">Tópico de Aula: <span class="required">&nbsp;</span></label>
			<h:selectOneMenu value="#{ arquivoUsuario.arquivoTurma.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
				<f:selectItems value="#{ topicoAula.comboIdentado }"/>
			</h:selectOneMenu>
			<h:selectOneMenu value="#{ arquivoUsuario.arquivoTurma.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
				<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
			</h:selectOneMenu>
			<span class="descricao-campo">(O Tópico de Aula ao qual o arquivo será associado.)</span> 
		</li>

		<li>
			<label for="form:nome">Nome: </label>
			<h:inputText id="nome" value="#{ arquivoUsuario.arquivoTurma.nome }" size="59" maxlength="200"/> <br/>
			<span class="descricao-campo">(Nome que será visto pelos discentes na página principal da Turma Virtual. 
										Se nenhum nome for informado, será utilizado o nome do arquivo.)</span> 
		</li>
		
		<li>
			<label for="form:descricao">Descrição:</label>
			<h:inputHidden value="#{ arquivoUsuario.associarTurma }"/>
			<h:inputTextarea id="descricao" value="#{ arquivoUsuario.arquivoTurma.descricao }" rows="3" cols="57"/>
			<span class="descricao-campo">(Breve descrição do arquivo. Não obrigatório.)</span> 
		</li>
		
		<li>
			<label for="form:descricao">Notificação: </label>
			<h:inputHidden value="#{ arquivoUsuario.arquivoTurma.notificarAlunos}" id="inputHiddenNotificarAluno"/>
			<h:selectBooleanCheckbox id="notificacao" value="#{ arquivoUsuario.arquivoTurma.notificarAlunos }" />
			<span class="descricao-campo">(Notificar os alunos por e-mail)</span> 
		</li>
		
		<c:if test="${ not empty turmaVirtual.turmasSemelhantes }">	
		<li>
			<table><tr><th style="width:130px;vertical-align:top;">Criar em: <span class="required">&nbsp;</span>
			</th><td>
				<t:selectManyCheckbox value="#{ arquivoUsuario.cadastrarEm }" layout="pageDirection">
					<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }"/>
				</t:selectManyCheckbox>
			</td></tr></table>
		</li>
		</c:if>
		
		</c:if>

	</ul>
	
	<div class="botoes">
		<h:inputHidden value="#{arquivoUsuario.associarTurma}" id="inputHiddenAssociarTurma"/>
		<div class="form-actions">
			<h:commandButton value="Enviar Arquivo" action="#{arquivoUsuario.cadastrar}" id="botaoEnviarArquivo"/>
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

<c:if test="${ sessionScope.paComTurma == 'true' }">
<%@include file="/ava/rodape.jsp" %>
</c:if>
<c:if test="${ sessionScope.paComTurma == 'false' }">
<%@include file="/ava/PortaArquivos/rodape.jsp"%>
</c:if>


</f:view>

<script type="text/javascript">
function validarNomePasta() {
	if(document.getElementById('formPasta:nomePasta').value.trim() == '') {
		alert("Digite o nome da pasta.");
		return false;
	}
	return true;
}
</script>