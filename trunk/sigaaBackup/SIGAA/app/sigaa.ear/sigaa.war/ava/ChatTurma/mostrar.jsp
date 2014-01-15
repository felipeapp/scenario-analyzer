<%@include file="/ava/cabecalho.jsp" %>

<style>
<!--
	label {	font-weight: bold; }
-->
</style>

<a4j:keepAlive beanName="chatTurmaBean" />
<f:view>

<%@include file="/ava/menu.jsp" %>

<h:form id="form">

	<fieldset>
		<legend>Visualização de Chat Agendado</legend>
	
		<ul class="form">	
		
			<li>
				<label for="titulo">Título:</label> 
				<h:outputText value="#{ chatTurmaBean.object.titulo }" id="titulo"/>
			</li>

			<li>
				<label for="turma">Turma: </label> 
				<h:outputText value="#{ chatTurmaBean.object.turma.nome }" id="turma"/>
			</li>

			<li>
				<label for="turma">Aula: </label> 
				<h:outputText value="#{ chatTurmaBean.object.aula.descricao }" id="aula" rendered="#{not empty chatTurmaBean.object.aula.descricao}"/>
				<h:outputText value="- <i> (Chat de turma)</i>" escape="false" rendered="#{empty chatTurmaBean.object.aula}" />
			</li>
		
			<li>
				<label for="descricao">Descrição: </label> 
				<h:outputText value="#{ chatTurmaBean.object.descricao }" id="descricao" escape="false"/>
			</li>
		
			<li>
				<label for="inicio">Início:</label>
				<h:outputText value="#{ chatTurmaBean.object.dataInicio }" id="inicio"/>&nbsp;
				<h:outputText value="#{ chatTurmaBean.object.horaInicio }">
					<f:convertDateTime pattern="HH:mm"/>
				</h:outputText>
			</li>
		
			<li>
				<label>Fim:</label>
				<h:outputText value="#{ chatTurmaBean.object.dataFim }" id="fim"/>&nbsp;
				<h:outputText value="#{ chatTurmaBean.object.horaFim }">
					<f:convertDateTime pattern="HH:mm"/>
				</h:outputText>
			</li>

			<li>
				<label>Publicar conteúdo:</label>
				<h:outputText value="#{ chatTurmaBean.object.publicarConteudo ? 'Sim' : 'Não' }" />
			</li>
			
			
			<li>
				<label>Conteúdo:</label>
				<h:outputText value="<i>[Publicação do conteúdo não autorizada]</i>" escape="false" rendered="#{ !chatTurmaBean.object.publicarConteudo }"/>
				<br/>
					<div style="padding-left:135px;">
						<h:outputText value="#{ chatTurmaBean.object.conteudo }" escape="false" rendered="#{ chatTurmaBean.object.publicarConteudo }"/>
					</div>
			</li>
			
		</ul>
		
		<div class="botoes">
			<div class="form-actions">
			</div>
			<div class="other-actions" style="width: 20%">					
				<h:commandButton action="#{ chatTurmaBean.listar }" value="<< Voltar"/>
			</div>
		</div>
	
	</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
