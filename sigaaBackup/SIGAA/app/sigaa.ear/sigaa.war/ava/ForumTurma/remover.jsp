<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<a4j:keepAlive beanName="forumTurmaBean" />
	<h:form id="form">
		<fieldset>
			<legend>Remover F�rum</legend>
			
			<ul class="show">
				<li>		
					<div class="descricaoOperacao">
						<b>Aten��o:</b><br/>
						Ao remover este f�rum todos os t�picos e todas as respostas associadas tamb�m ser�o removidas.						
					</div>
				</li>					
				
				<li>
					<label>T�tulo:</label>
					<div class="campo"><h:outputText value="#{ forumTurmaBean.obj.forum.titulo }" /></div>
				</li>
				
				<li>		
					<label>Descri��o: </label>
					<div class="campo"><h:outputText value="#{ forumTurmaBean.obj.forum.descricao }" escape="false"/></div>
				</li>
				
					
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumTurmaBean.remover }" value="Confirmar Remo��o" id="cmdRemoverMensagem" 
						rendered="#{forumTurmaBean.usuarioLogado.id == forumTurmaBean.obj.forum.usuario.id}">
						<f:setPropertyActionListener value="#{ forumTurmaBean.obj.id }" target="#{ forumTurmaBean.obj.id }"/> 
					</h:commandButton>		
				</div>
				<div class="other-actions">	
					<h:commandButton action="#{ forumTurmaBean.cancelar }" value="Cancelar" id="cmdCancelar"  onclick="#{confirm}"/>
				</div>
			</div>	
		</fieldset>
		
		<ul>
			<li>
				<%@include file="/ava/Foruns/_topicos.jsp" %>
			</li>
		</ul>
		
	</h:form>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>
