<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${ sessionScope.paComTurma == 'true' }">
<%@include file="/ava/cabecalho.jsp" %>
</c:if>
<c:if test="${ sessionScope.paComTurma == 'false' }">
<%@include file="/ava/PortaArquivos/cabecalho.jsp"%>
</c:if>

<f:view>

<c:if test="${ sessionScope.paComTurma == 'true' }">
<%@include file="/ava/menu.jsp"%>
</c:if>

<h:form enctype="multipart/form-data">
<a4j:keepAlive beanName="arquivoUsuario"></a4j:keepAlive>

<h:messages showDetail="true"/>
<h:outputText value="#{ arquivoUsuario.associarATopico}"/>
<h:outputText value="#{ arquivoUsuario.selecionarTurmaPadrao }"/>
<h:outputText value="#{ arquivoUsuario.selecionarTopicoInicial }"/>

<fieldset>
<legend>Associar Arquivo a um Tópico de Aula</legend>
<ul class="form">	
	<li>
		<label for="form:arquivo"><b>Arquivo:</b>&nbsp; </label>
		<h:outputText value="#{ arquivoUsuario.obj.nome }"/>
		<h:inputHidden value="#{ arquivoUsuario.obj.id }"/>
		<span class="descricao-campo">(Arquivo a ser enviado para a Turma Virtual.)</span> 
	</li>
	<li>
		<label for="form:topico" class="required">Tópico de Aula:<span class="required">&nbsp;</span></label>
		<h:selectOneMenu value="#{ arquivoUsuario.arquivoTurma.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
			<f:selectItems value="#{ topicoAula.comboIdentado }"/>
		</h:selectOneMenu>
		<h:selectOneMenu value="#{ arquivoUsuario.arquivoTurma.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
			<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
		</h:selectOneMenu>
		<span class="descricao-campo">(O Tópico de Aula ao qual o arquivo será associado.)</span> 
	</li>
	<li>
		<label for="nome">Nome:</label>
		<h:inputText value="#{ arquivoUsuario.arquivoTurma.nome }" size="59" maxlength="200"/>
		<span class="descricao-campo">(Nome que será visto pelos discentes na página principal da Turma Virtual. Se nenhum nome for informado, será utilizado o nome do arquivo.)</span>
	</li>
	<li>
		<label for="form:descricao">Descrição:</label>
		<h:inputTextarea value="#{ arquivoUsuario.arquivoTurma.descricao }" rows="3" cols="57"/>
		<span class="descricao-campo">(Breve descrição do arquivo. Não obrigatório.)</span> 
	</li>
	<c:if test="${ not empty turmaVirtual.turmasSemelhantes }">	
	<li>
		<label class="required">Criar em:<span class="required">&nbsp;</span></label>
		<t:selectManyCheckbox value="#{ arquivoUsuario.cadastrarEm }" layout="pageDirection">
			<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemelhantes }"/>
		</t:selectManyCheckbox>
	</li>
	</c:if>
</ul>

<div class="botoes">
	<input type="hidden" name="id" value="${ param['id'] }"/>
	<h:inputHidden value="#{arquivoUsuario.associarTurma}"/>
	
	<div class="form-actions">
		<h:commandButton value="Associar Arquivo" action="#{arquivoUsuario.cadastrar}"/>
	</div>
	<div class="other-actions">
		<h:commandButton value="Cancelar" onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados serão perdidos!');" action="#{arquivoUsuario.cancelar}" immediate="true"/>
	</div>
	<div class="required-items">
		<span class="required">&nbsp;</span>
		Itens de Preenchimento Obrigatório
	</div>
</div>

</fieldset>
</h:form>


</f:view>
<c:if test="${ sessionScope.paComTurma == 'true' }">
<%@include file="/ava/rodape.jsp" %>
</c:if>
<c:if test="${ sessionScope.paComTurma == 'false' }">
<%@include file="/ava/PortaArquivos/rodape.jsp"%>
</c:if>