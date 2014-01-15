<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
	
		
		<h:messages showDetail="true" />
		
		<fieldset>
			<legend>Novo Tópico</legend>
			
			<%@include file="/ava/TopicoAula/_form.jsp" %>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{topicoAula.cadastrar}" value="Cadastrar" /> 
				</div>
				
				<div class="other-actions">
					<h:commandButton action="#{ topicoAula.listar }" value="<< Voltar"/> 
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