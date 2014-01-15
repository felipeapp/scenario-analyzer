<%@include file="/ava/cabecalho.jsp" %>

<a4j:keepAlive beanName="chatTurmaBean" />
<f:view>

	<%@include file="/ava/menu.jsp" %>

	<h:form id="formAva">
		<fieldset>
			<legend> Chat Agendado</legend>

			<%@include file="/ava/ChatTurma/_form.jsp" %>

			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ chatTurmaBean.cadastrar }" value="Cadastrar" id="btnCadastrarChat" rendered="#{ chatTurmaBean.permiteCadastrarChat }"/>
				</div>
				<div class="other-actions" style="width: 20%">					
					<h:commandButton action="#{ chatTurmaBean.listar }" value="<< Voltar" immediate="true" id="btListar"/>
					<h:commandButton action="#{ chatTurmaBean.cancelar }" value="Cancelar" immediate="true" id="btnCancelar"  onclick="#{confirm}"/>					 
				</div>
				<div class="required-items">
					<span class="required"></span>Itens de Preenchimento Obrigatório
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
