<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<h2>Vestibular > Solicitação de Recuperação de Senha</h2>
	
		<div class="descricaoOperacao">
			<h3>E-Mail Enviado</h3>
		
			<p>Você receberá um e-mail com instruções de como recuperar sua senha de acesso à área pessoal. Esta solicitação terá validade de 24 horas. Após este período, será necessário realizar uma nova solicitação de recuperação de senha.</p>
			<br/>
			<div align="center"><h:commandLink value="Página Inicial" action="#{acompanhamentoVestibular.cancelar}" id="acompanhamentoVestibular_cancelar"/></div>
		</div>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>