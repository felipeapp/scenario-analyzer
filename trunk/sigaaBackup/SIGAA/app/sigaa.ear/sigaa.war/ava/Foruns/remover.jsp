<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form">
		<fieldset>
			<legend>Remover Fórum</legend>
			
			<ul class="show">
				<li>		
					<div class="descricaoOperacao">
						<b>Atenção:</b><br/>
						Ao remover este fórum todos os tópicos e todas as respostas associadas também serão removidas.						
					</div>
				</li>		
				
				<li>
					<label>Título:</label>
					<div class="campo"><h:outputText value="#{ forumBean.obj.titulo }" /></div>
				</li>
				
				<li>		
					<label>Descrição: </label>
					<div class="campo"><h:outputText value="#{ forumBean.obj.descricao }" escape="false"/></div>
				</li>
								
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumBean.remover }" value="Confirmar Remoção" id="cmdRemoverMensagem" 
						rendered="#{forumBean.usuarioLogado.id == forumBean.obj.usuario.id}">
						<f:setPropertyActionListener value="#{ forumBean.obj }" target="#{ forumBean.obj }"/> 
					</h:commandButton>		
				</div>
				<div class="other-actions">	
					<h:commandButton action="#{ forumBean.cancelar }" value="Cancelar" id="cmdCancelar"  onclick="#{confirm}"/>
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
