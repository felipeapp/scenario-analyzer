<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
	
		
		<h:messages showDetail="true" />
		
		<fieldset>
			<legend>Editar Tópico</legend>
			
			<%@include file="/ava/TopicoAula/_form.jsp" %>
			
			<input type="hidden" name="id" value="${ topicoAula.object.id }"/>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{topicoAula.atualizar}" value="Atualizar Dados" /> 
				</div>
				
				<div class="right-buttons">
					<h:commandButton action="#{ topicoAula.mostrar }" value="Mostrar" /> | <h:commandButton action="#{ topicoAula.listar }" value="<< Voltar"/> 
					<h:commandButton action="#{ topicoAula.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
				</div>
						
				<div class="required-items">
					<span class="required">&nbsp;</span>
					Campos de Preenchimento Obrigatório
				</div>
			</div>
		</fieldset>
	</h:form>


</f:view>
<%@include file="/ava/rodape.jsp" %>