<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<a4j:keepAlive beanName="forumParticipanteBean" />
	<%@include file="/ava/menu.jsp" %>
	
	<h:form id="form">	
		<fieldset>
			<legend>F�rum</legend>
			
			<ul class="show">
				<li>
					<label>T�tulo:</label>
					<div class="campo"><h:outputText value="#{ forumParticipanteBean.obj.forum.titulo }" /></div>
				</li>
				
				<li>		
					<label>Descri��o: </label>
					<div class="campo"><h:outputText value="#{ forumParticipanteBean.obj.forum.descricao }" escape="false"/></div>
				</li>
			
				<li>		
					<label>Tipo: </label>
					<div class="campo"><h:outputText value="#{ forumParticipanteBean.obj.forum.tipo.descricao }" /></div>
				</li>
			
				<li>		
					<label>Criado em: </label>
					<div class="campo"><h:outputText value="#{ forumParticipanteBean.obj.forum.dataCadastro }" /></div>
				</li>					
			</ul>
		
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumParticipanteBean.cadastrar }" value="Confirmar Participa��o" id="cmdConfirmarParticipacao" />
				</div>
				<div class="other-actions">	
					<h:commandButton action="#{ forumParticipanteBean.cancelar }" value="Cancelar" id="cmdCancelar" immediate="true" /> |
					<h:commandButton action="#{ forumParticipanteBean.listar }" value="Voltar" id="cmdListar" immediate="true"/>
				</div>
			</div>				
		</fieldset>	
			
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
