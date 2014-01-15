<%@include file="/ava/cabecalho.jsp"%>

<f:view>
<%@include file="/ava/menu.jsp"%>
<h:form enctype="multipart/form-data">


<h:messages showDetail="true"/>

<fieldset>
<legend>Atualizar Arquivo</legend>
<ul class="form">	
	<li>
		<label for="form:arquivo" class="required">Arquivo: <span class="required">&nbsp;</span></label>
		<t:inputFileUpload value="#{ atualizaArquivoTurmaMBean.arquivo }"/>
		<h:inputHidden value="#{atualizaArquivoTurmaMBean.arquivoTurma.id}"/>
		<h:inputHidden value="#{ atualizaArquivoTurmaMBean.arquivoTurma.arquivo.id }"/>
		<span class="descricao-campo">(Arquivo para substituir o atual.)</span> 
	</li>
	<li>
		<label for="form:topico" class="required">T�pico de Aula:<span class="required">&nbsp;</span></label>
		<h:selectOneMenu value="#{ atualizaArquivoTurmaMBean.arquivoTurma.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
			<f:selectItems value="#{ topicoAula.comboIdentado }"/>
		</h:selectOneMenu>
		<h:selectOneMenu value="#{ atualizaArquivoTurmaMBean.arquivoTurma.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
			<f:selectItem itemLabel="Nenhum T�pico de Aula foi cadastrado" itemValue="0"/>
		</h:selectOneMenu>
		<span class="descricao-campo">(O T�pico de Aula ao qual o arquivo ser� associado.)</span> 
	</li>
	<li>
		<label for="nome">Nome:</label>
		<h:inputText value="#{ atualizaArquivoTurmaMBean.arquivoTurma.nome }" size="59"/>
		<span class="descricao-campo">(Nome que ser� visto pelos discentes na p�gina principal da Turma Virtual. Se nenhum nome for informado, ser� utilizado o nome do arquivo.)</span>
	</li>
	<li>
		<label for="form:descricao">Descri��o:</label>
		<h:inputTextarea value="#{ atualizaArquivoTurmaMBean.arquivoTurma.descricao }" rows="3" cols="57"/>
		<span class="descricao-campo">(Breve descri��o do arquivo. N�o obrigat�rio.)</span> 
	</li>

</ul>

<div class="botoes">
	
	
	<div class="form-actions">
		<h:commandButton value="Atualizar Arquivo" action="#{atualizaArquivoTurmaMBean.atualizaAssociacaoArquivo}" />
	</div>
	<div class="other-actions">
		<h:commandButton value="Cancelar" action="#{atualizaArquivoTurmaMBean.cancelar}" immediate="true"/>
	</div>
	<div class="required-items">
		<span class="required">&nbsp;</span>
		Itens de Preenchimento Obrigat�rio
	</div>
</div>
</h:form>


</f:view>
<%@include file="/ava/rodape.jsp"%>