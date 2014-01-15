<%@include file="/ava/cabecalho.jsp" %>

<a4j:keepAlive beanName="rotuloTurmaBean" />
<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form">
		<fieldset>
			<legend>Editar R�tulo</legend>
			
			<%@include file="/ava/RotuloTurma/_form.jsp" %>
			
			<input type="hidden" name="id" value="${ rotuloTurmaBean.object.id }"/>
			<h:inputHidden value="#{ rotuloTurmaBean.object.id }"/>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ rotuloTurmaBean.atualizar }" value="Atualizar Dados" /> 
				</div>
				<div class="right-buttons">
					<h:commandButton action="#{ turmaVirtual.retornarParaTurma }" value="<< Voltar"/> 
					<h:commandButton action="#{ rotuloTurmaBean.mostrar }" value="Mostrar" />
				</div>
				<div class="required-items">
					<span class="required">&nbsp;</span>
					Campos de Preenchimento Obrigat�rio.
				</div>
			</div>
		
		</fieldset>
	
	</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>