<%@include file="/ava/cabecalho.jsp" %>

<a4j:keepAlive beanName="chatTurmaBean" />
<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="formAva">
	
		<fieldset>
			<legend>Editar Chat Agendado</legend>
			
			<%@include file="/ava/ChatTurma/_form.jsp" %>
			
			<input type="hidden" name="id" value="${ chatTurmaBean.object.id }"/>
			<h:inputHidden value="#{chatTurmaBean.object.id}"/>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{chatTurmaBean.atualizar}" value="Atualizar Dados" /> 
				</div>
				<div class="right-buttons">
					<h:commandButton action="#{ chatTurmaBean.listar }" value="<< Voltar"/> 
					| <h:commandButton action="#{ chatTurmaBean.mostrar }" value="Mostrar" onclick="#{confirm}"/>
					|<h:commandButton action="#{ chatTurmaBean.cancelar }" value="Cancelar" immediate="true" id="btnCancelar"  onclick="#{confirm}"/>
				</div>
				<div class="required-items">
					<span class="required">&nbsp;</span>
					Itens de Preenchimento Obrigatório.
				</div>
			</div>
		
		</fieldset>
	
	</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>