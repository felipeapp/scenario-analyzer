<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>

<h:form enctype="multipart/form-data">

	<div class="secaoComunidade">
	<rich:panel header="Inserir Arquivo na Comunidade" headerClass="headerBloco">
	<table class="formulario" width="80%">
	<caption>Enviar arquivo</caption>
			
			<tr>
			<th class="required"><h:outputLabel for="titulo">Arquivo:</h:outputLabel></th>
			<td><t:inputFileUpload id="arquivo" value="#{arquivoUsuarioCVMBean.arquivo}" size="49"/></td>
		</tr>

		<tr>
			<th class="required"><h:outputLabel for="titulo">Tópico de Comunidade:</h:outputLabel></th>
			<td>
				<h:selectOneMenu value="#{ arquivoUsuarioCVMBean.arquivoComunidade.topico.id }" 
					rendered="#{ not empty topicoComunidadeMBean.comboIdentado }">
					<f:selectItems value="#{ topicoComunidadeMBean.comboIdentado }"/>
				</h:selectOneMenu>
				
				<h:selectOneMenu value="#{ arquivoUsuarioCVMBean.arquivoComunidade.topico.id }" styleClass="sem-topicos-aula" 
					rendered="#{ empty topicoComunidadeMBean.comboIdentado }">
					<f:selectItem itemLabel="Nenhum Tópico de Comunidade foi cadastrado" itemValue="0"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<th><h:outputLabel for="titulo">Nome:</h:outputLabel></th>
			<td>
				<h:inputText id="nome" value="#{ arquivoUsuarioCVMBean.arquivoComunidade.nome }" size="60" /> <br/>
			</td>
		</tr>
		
		<tr>
			<th><h:outputLabel for="titulo">Descrição:</h:outputLabel></th>
			<td>
				<h:inputTextarea id="descricao" value="#{ arquivoUsuarioCVMBean.arquivoComunidade.descricao }" rows="5" cols="58"/>
			</td>
		</tr>
		
		<!--
		<li>
			<label class="required">Criar em: <span class="required">&nbsp;</span></label>
			<t:selectManyCheckbox value="#{ arquivoUsuarioCVMBean.cadastrarEm }" layout="pageDirection">
				<t:selectItems var="ts" itemLabel="#{ ts.descricao }" itemValue="#{ ts.id }" value="#{ topicoComunidadeMBean.topicosComunidade }"/>
			</t:selectManyCheckbox>
		</li>
		-->
		<tr>
			<td style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ arquivoUsuarioCVMBean.notificar }" styleClass="noborder" /> </td>
			<th style="text-align: left;">Notificar por e-mail?</th>
		</tr>
			
		<tfoot>
			<tr> 
				<td colspan="2"> 
					<h:commandButton value="Enviar Arquivo" action="#{arquivoUsuarioCVMBean.cadastrar}" />
					<h:commandButton action="#{arquivoUsuarioCVMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>	
				</td>
			</tr>
		</tfoot>
	</table>
		
		
	<%--
		<tr>
			<th><h:outputLabel for="titulo">Nome:</h:outputLabel></th>
			<td>
				<h:inputText id="nome" value="#{ arquivoUsuarioCVMBean.arquivoComunidade.nome }" size="60" /> <br/>
			</td>
		</tr>
		
		<label for="form:descricao">Descrição: </label>
		<li>
			<h:inputTextarea id="descricao" value="#{ arquivoUsuarioCVMBean.arquivoComunidade.descricao }" rows="5" cols="58"/>
		</li>
	

	<div class="botoes">
		<!--<h:inputHidden value="#{arquivoUsuario.associarTurma}"/> -->
		<div class="form-actions">
			<h:commandButton value="Enviar Arquivo" action="#{arquivoUsuarioCVMBean.cadastrar}" />
		</div>
		<div class="other-actions">
			<h:commandLink value="Voltar" action="#{arquivoUsuarioCVMBean.cancelar}" immediate="true" onclick="return(confirm('Deseja realmente sair dessa página? Os dados informados serão perdidos.'));"/>
		</div>
		<div class="required-items">
			<span class="required"/>
			Itens de Preenchimento Obrigatório
		</div>
	</div>
	--%>
	</rich:panel>
	</div>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
<%@include file="/cv/include/rodape.jsp" %>