<%@include file="/ava/cabecalho.jsp" %>

<a4j:keepAlive beanName="rotuloTurmaBean" />
<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form">
		<fieldset>
			<legend> Rótulo</legend>
			
			<%@include file="/ava/RotuloTurma/_form.jsp" %>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ rotuloTurmaBean.cadastrar }" value="Cadastrar" id="btnCadastrarRotulo" rendered="#{ rotuloTurmaBean.permiteCadastrarRotulo }"/>
				</div>
				<div class="other-actions">
					<h:commandButton action="#{ turmaVirtual.retornarParaTurma }" value="<< Voltar" immediate="true" id="btnListar" />
					<h:commandButton action="#{ rotuloTurmaBean.cancelar }" value="Cancelar" immediate="true" id="btnCancelar" onclick="#{confirm}"/> 
				</div>
				<div class="required-items">
					<span class="required"></span>Campos de Preenchimento Obrigatório
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
