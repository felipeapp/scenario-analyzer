<%@include file="/ava/cabecalho.jsp" %>

<a4j:keepAlive beanName="rotuloTurmaBean" />
<f:view>

<%@include file="/ava/menu.jsp" %>

<h:form id="form">

	<fieldset>
		<legend>Visualização de Rótulo</legend>
	
		<ul class="form">	
		
			<li>
				<label for="turma">Turma: </label> 
				<h:outputText value="#{ rotuloTurmaBean.object.turma.nome }" id="turma"/>
			</li>

			<li>
				<label for="turma">Aula: </label> 
				<h:outputText value="#{ rotuloTurmaBean.object.aula.descricao }" id="aula" rendered="#{not empty rotuloTurmaBean.object.aula.descricao}"/>
			</li>
		
			<li>
				<label for="descricao">Descrição: </label> 
				<h:outputText value="#{ rotuloTurmaBean.object.descricao }" id="descricao" escape="false"/>
			</li>

			<li>
				<label for="descricao">Visível: </label> 
				<h:outputText value="#{ rotuloTurmaBean.object.visivel ? 'SIM' : 'NÃO' }" id="visivel" escape="false"/>
			</li>

			
		</ul>
		
		<div class="botoes">
			<div class="form-actions">
			</div>
			<div class="other-actions" style="width: 20%">					
				<h:commandButton action="#{ turmaVirtual.retornarParaTurma }" value="<< Voltar"/>
			</div>
		</div>
	
	</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
